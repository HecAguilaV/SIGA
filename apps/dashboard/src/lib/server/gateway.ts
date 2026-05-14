import { redirect } from '@sveltejs/kit';
import type { RequestEvent } from '@sveltejs/kit';

const GATEWAY_BASE = 'http://localhost:8080';
const TIMEOUT_MS = 8000;

interface FetchWithAuthOptions extends RequestInit {
	timeout?: number;
}

/**
 * fetchWithAuth — Wrapper central para llamadas al gateway desde server load functions.
 *
 * Inyecta el Authorization header desde la cookie httpOnly.
 * Si recibe 401, intenta refresh via POST /api/auth/refresh (1 reintento).
 * Si refresh ok, retry original request con nuevo token.
 * Si refresh fail, lanza redirect a /login.
 * Timeout por defecto: 8s.
 */
export async function fetchWithAuth(
	fetchFn: typeof globalThis.fetch,
	event: { request: Request; cookies: RequestEvent['cookies']; url: URL },
	endpoint: string,
	init?: FetchWithAuthOptions
): Promise<Response> {
	const token = event.cookies.get('siga_token');
	const headers = new Headers(init?.headers);

	if (token) {
		headers.set('Authorization', `Bearer ${token}`);
	}

	headers.set('Content-Type', headers.get('Content-Type') || 'application/json');
	headers.set('Accept', 'application/json');

	const url = endpoint.startsWith('http') ? endpoint : `${GATEWAY_BASE}${endpoint}`;
	const controller = new AbortController();
	const timeout = init?.timeout ?? TIMEOUT_MS;

	const timeoutId = setTimeout(() => controller.abort(), timeout);

	try {
		let response = await fetchFn(url, {
			...init,
			headers,
			signal: controller.signal
		});

		// 401 → intentar refresh una vez
		if (response.status === 401 && token) {
			const refreshed = await attemptRefresh(fetchFn, event);

			if (refreshed) {
				// Reintentar request original con nuevo token
				const newToken = event.cookies.get('siga_token');
				if (newToken) {
					headers.set('Authorization', `Bearer ${newToken}`);
				}

				response = await fetchFn(url, {
					...init,
					headers,
					signal: controller.signal
				});
			} else {
				// Refresh falló → redirect a login
				const redirectTo = event.url.pathname + event.url.search;
				event.cookies.delete('siga_token', { path: '/' });
				event.cookies.delete('siga_refresh', { path: '/api/auth/refresh' });
				throw redirect(303, `/login?redirect=${encodeURIComponent(redirectTo)}`);
			}
		}

		return response;
	} catch (err) {
		if (err instanceof Response || (err as { status?: number })?.status) throw err;
		if (err instanceof DOMException && err.name === 'AbortError') {
			throw new Error('Gateway timeout');
		}
		throw err;
	} finally {
		clearTimeout(timeoutId);
	}
}

/**
 * attemptRefresh — Intenta refrescar el token usando el refresh cookie.
 * Returns true si el refresh fue exitoso.
 */
export async function attemptRefresh(
	fetchFn: typeof globalThis.fetch,
	event: { cookies: RequestEvent['cookies'] }
): Promise<boolean> {
	try {
		const refreshToken = event.cookies.get('siga_refresh');
		if (!refreshToken) return false;

		const res = await fetchFn(`${GATEWAY_BASE}/api/auth/refresh`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'Cookie': `siga_refresh=${refreshToken}`
			},
			body: JSON.stringify({ refreshToken })
		});

		if (!res.ok) return false;

		const data = await res.json();

		// Setear nuevas cookies
		if (data.accessToken) {
			event.cookies.set('siga_token', data.accessToken, {
				path: '/',
				httpOnly: true,
				secure: true,
				sameSite: 'lax',
				maxAge: 60 * 15 // 15 min
			});
		}

		if (data.refreshToken) {
			event.cookies.set('siga_refresh', data.refreshToken, {
				path: '/api/auth/refresh',
				httpOnly: true,
				secure: true,
				sameSite: 'strict',
				maxAge: 60 * 60 * 24 * 7 // 7 días
			});
		}

		return true;
	} catch {
		return false;
	}
}
