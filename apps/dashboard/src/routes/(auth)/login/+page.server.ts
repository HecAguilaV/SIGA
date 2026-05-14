import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { login, setSessionCookies } from '$lib/server/auth.server';
import { isMockMode, mockLogin } from '$lib/server/mock-auth';

// Si la load function se ejecuta con sesión activa, redirigir al dashboard
export const load: PageServerLoad = async ({ locals, url }) => {
	if (locals.user) {
		const redirectTo = url.searchParams.get('redirect') || '/';
		throw redirect(303, redirectTo);
	}
	return {};
};

export const actions: Actions = {
	default: async ({ request, cookies, fetch, url }) => {
		const formData = await request.formData();
		const email = (formData.get('email') as string)?.trim();
		const password = formData.get('password') as string;
		const redirectParam = formData.get('redirect') as string || url.searchParams.get('redirect') || '/';

		// ── Validación cliente ──
		if (!email) {
			return fail(400, { email, error: 'El correo electrónico es requerido', missing: 'email' });
		}

		if (!password) {
			return fail(400, { email, error: 'La contraseña es requerida', missing: 'password' });
		}

		if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
			return fail(400, { email, error: 'Formato de correo inválido' });
		}

		// ── Login — intentar gateway real, fallback a mock ──
		let result: Awaited<ReturnType<typeof login>>;

		if (isMockMode()) {
			result = mockLogin(email, password);
		} else {
			result = await login(fetch, email, password);

			// Fallback a mock si el gateway no está disponible o responde error no-crítico
			if (result.error && result.error !== 'Credenciales inválidas') {
				result = mockLogin(email, password);
				setMockModeFlag(cookies);
			}
		}

		if (result.error || !result.data) {
			return fail(401, { email, error: result.error || 'Credenciales inválidas' });
		}

		// ── Setear cookies httpOnly ──
		setSessionCookies(cookies, result.data.accessToken, result.data.refreshToken);

		// ── Redirigir (usando throw redirect de SvelteKit) ──
		throw redirect(303, redirectParam);
	}
};

function setMockModeFlag(cookies: import('@sveltejs/kit').RequestEvent['cookies']): void {
	cookies.set('siga_mock', 'true', {
		path: '/',
		maxAge: 60 * 60 * 24 // 24h
	});
}
