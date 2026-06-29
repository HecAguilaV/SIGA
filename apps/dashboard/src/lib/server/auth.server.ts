import { redirect } from '@sveltejs/kit';
import type { RequestEvent } from '@sveltejs/kit';
import type { LoginResponse, UserSession, RegisterRequest, OnboardingData } from '$lib/types/auth';
import { env } from '$env/dynamic/private';

const GATEWAY_BASE = env.GATEWAY_BASE_URL || 'http://localhost:8080';
const AUTH_BASE = `${GATEWAY_BASE}/api/auth`;

/**
 * login — Autentica al usuario vía gateway (login dual: Customer primero, luego User).
 */
export async function login(
	fetchFn: typeof globalThis.fetch,
	email: string,
	password: string
): Promise<{ data?: LoginResponse; error?: string }> {
	try {
		const res = await fetchFn(`${GATEWAY_BASE}/api/auth/login`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ email, password })
		});

		if (!res.ok) {
			if (res.status === 401) {
				return { error: 'Credenciales inválidas' };
			}
			return { error: 'Error del servidor. Intente nuevamente.' };
		}

		const data: LoginResponse = await res.json();
		return { data };
	} catch {
		return { error: 'Error de conexión. Verifique su conexión a internet.' };
	}
}

/**
 * logout — Invalida la sesión en el gateway.
 */
export async function logout(
	fetchFn: typeof globalThis.fetch,
	event: RequestEvent
): Promise<void> {
	try {
		const token = event.cookies.get('siga_token');
		if (token) {
			await fetchFn(`${GATEWAY_BASE}/api/auth/logout`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					'Authorization': `Bearer ${token}`
				}
			});
		}
	} catch {
		// Silently ignore logout errors — session will be cleared client-side
	}
}

/**
 * register — Registra un nuevo customer (email+password, con name/companyName opcional).
 * Llama a POST /api/v1/auth/register.
 */
export async function register(
	fetchFn: typeof globalThis.fetch,
	data: RegisterRequest
): Promise<{ data?: { status: string }; error?: string }> {
	try {
		const body: Record<string, string> = { email: data.email, password: data.password };
		if (data.name) body.name = data.name;
		if (data.companyName) body.companyName = data.companyName;

		const res = await fetchFn(`${AUTH_BASE}/register`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify(body)
		});

		if (res.status === 201) {
			const data = await res.json();
			return { data };
		}

		const err = await res.json().catch(() => ({}));
		return { error: err.error || 'Error al registrar. Intente nuevamente.' };
	} catch {
		return { error: 'Error de conexión. Verifique su conexión a internet.' };
	}
}

/**
 * updateCustomer — Actualiza los datos de perfil de un customer (name, companyName).
 * Llama a PUT /api/v1/auth/customers/{id}.
 * Requiere token de autenticación.
 */
export async function updateCustomer(
	fetchFn: typeof globalThis.fetch,
	customerId: string | number,
	data: OnboardingData,
	token: string
): Promise<{ success?: boolean; error?: string }> {
	try {
		const body: Record<string, string> = { name: data.name };
		if (data.companyName) body.companyName = data.companyName;

		const res = await fetchFn(`${AUTH_BASE}/customers/${customerId}`, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': `Bearer ${token}`
			},
			body: JSON.stringify(body)
		});

		if (res.ok) return { success: true };

		const err = await res.json().catch(() => ({}));
		return { error: err.error || 'Error al actualizar perfil.' };
	} catch {
		return { error: 'Error de conexión. Verifique su conexión a internet.' };
	}
}

/**
 * setSessionCookies — Setea las cookies httpOnly access + refresh.
 */
export function setSessionCookies(
	cookies: RequestEvent['cookies'],
	accessToken: string,
	refreshToken: string
): void {
	cookies.set('siga_token', accessToken, {
		path: '/',
		httpOnly: true,
		sameSite: 'lax',
		secure: false, // Forzar false para Tailscale/IP
		maxAge: 60 * 60 * 24, // 24 horas (coincide con exp del JWT)
	});

	cookies.set('siga_refresh', refreshToken, {
		path: '/',
		httpOnly: true,
		sameSite: 'lax',
		secure: false, // Forzar false para Tailscale/IP
		maxAge: 60 * 60 * 24 * 7 // 7 días
	});
}

/**
 * clearSessionCookies — Elimina las cookies de sesión.
 */
export function clearSessionCookies(event: RequestEvent): void {
	event.cookies.delete('siga_token', { path: '/' });
	event.cookies.delete('siga_refresh', { path: '/api/auth/refresh' });
}

/**
 * redirectToLogin — Redirige a /login preservando la ruta original.
 */
export function redirectToLogin(event: RequestEvent): never {
	const redirectTo = event.url.pathname + event.url.search;
	throw redirect(303, `/login?redirect=${encodeURIComponent(redirectTo)}`);
}

/**
 * decodeJwtPayload — Decodifica el payload de un JWT sin verificar la firma.
 * Solo para leer claims como exp, rol, principalType.
 */
export function decodeJwtPayload(token: string): Record<string, unknown> | null {
	try {
		const parts = token.split('.');
		if (parts.length !== 3) return null;
		const payload = parts[1];
		const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
		return JSON.parse(decoded);
	} catch {
		return null;
	}
}

/**
 * buildUserSession — Construye UserSession desde el payload del JWT.
 */
export function buildUserSession(payload: Record<string, unknown>): UserSession {
	return {
		id: (payload.sub as string) || (payload.id as string) || '',
		email: (payload.email as string) || '',
		name: (payload.name as string) || (payload.preferred_username as string) || '',
		principalType: (payload.principalType as UserSession['principalType']) || 'user',
		rol: payload.rol as string | undefined,
		permissions: payload.permissions as string[] | undefined,
		tenantId: payload.tenantId as string | undefined,
		avatar: payload.avatar as string | undefined
	};
}
