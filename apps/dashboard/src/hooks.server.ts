import { redirect, error } from '@sveltejs/kit';
import type { Handle } from '@sveltejs/kit';
import { decodeJwtPayload, buildUserSession, clearSessionCookies } from '$lib/server/auth.server';
import { attemptRefresh } from '$lib/server/gateway';

// Rutas públicas que no requieren autenticación
const PUBLIC_ROUTES = ['/login', '/logout', '/chat-handler/stream', '/_app'];

// Route-level role guards (check before layout load functions)
const ROLE_GUARDS: Record<string, string[]> = {
	'/users': ['ADMINISTRATOR'],
	'/analytics': ['ADMINISTRATOR', 'OPERATOR'],
	'/pos': ['CASHIER']
};

// Umbral para refresh anticipado: 5 minutos antes de expiración
const REFRESH_THRESHOLD_SEC = 5 * 60;

// Race condition lock: evita refrescos concurrentes para el mismo usuario
const refreshLocks = new Map<string, Promise<boolean>>();

/**
 * handle — Middleware principal.
 *
 * 1. Rutas públicas pasan sin verificación
 * 2. Lee cookie siga_token (JWT)
 * 3. Si no existe → redirect a /login
 * 4. Si exp < now + 5min → refresh automático (con race condition lock)
 * 5. Setea event.locals.user
 * 6. Verifica role guards para rutas específicas
 */
export const handle: Handle = async ({ event, resolve }) => {
	const { pathname } = event.url;

	// ── 1. Rutas públicas ──
	if (PUBLIC_ROUTES.some((route) => pathname.startsWith(route))) {
		return resolve(event);
	}

	// ── 2. Leer cookie ──
	const token = event.cookies.get('siga_token');
	console.log(`[Hooks] Path: ${pathname}, Token presente: ${!!token}`);
	
	if (!token) {
		console.warn(`[Hooks] Redirigiendo a login porque no hay token en ${pathname}`);
		return redirectToLogin(event);
	}

	// ── 3. Decodificar payload (sin verificar firma — el gateway lo hace) ──
	const payload = decodeJwtPayload(token);

	if (!payload || !payload.exp) {
		clearSessionCookies(event);
		return redirectToLogin(event);
	}

	const exp = (payload.exp as number) * 1000; // convertir a ms
	const now = Date.now();
	const threshold = REFRESH_THRESHOLD_SEC * 1000;

	// ── 4. Refresh anticipado ──
	if (exp - now < threshold) {
		const refreshToken = event.cookies.get('siga_refresh');

		if (refreshToken) {
			// Race condition lock: si ya hay un refresh en progreso para este refreshToken, esperar
			let refreshPromise = refreshLocks.get(refreshToken);

			if (!refreshPromise) {
				refreshPromise = attemptRefresh(fetch, event);
				refreshLocks.set(refreshToken, refreshPromise);

				// Limpiar el lock cuando termine (sea éxito o fallo)
				refreshPromise.finally(() => {
					if (refreshLocks.get(refreshToken) === refreshPromise) {
						refreshLocks.delete(refreshToken);
					}
				});
			}

			const refreshed = await refreshPromise;

			if (!refreshed) {
				clearSessionCookies(event);
				return redirectToLogin(event);
			}
		} else {
			// No hay refresh token → redirigir a login
			clearSessionCookies(event);
			return redirectToLogin(event);
		}
	}

	// ── 5. Setear event.locals.user ──
	event.locals.user = buildUserSession(payload);

	// ── 6. Role-based route guards ──
	const userRole = event.locals.user?.rol ?? '';
	for (const [prefix, allowedRoles] of Object.entries(ROLE_GUARDS)) {
		if (pathname === prefix || pathname.startsWith(prefix + '/')) {
			if (!allowedRoles.includes(userRole)) {
				error(403, 'No tienes permisos para acceder a esta sección');
			}
			break;
		}
	}

	// ── 7. Resolver ──
	const response = await resolve(event);
	return response;
};

function redirectToLogin(event: Parameters<Handle>[0]['event']): Response {
	const redirectTo = event.url.pathname + event.url.search;
	throw redirect(303, `/login?redirect=${encodeURIComponent(redirectTo)}`);
}
