import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { setSessionCookies } from '$lib/server/auth.server';
import { mockLogin } from '$lib/server/mock-auth';

// Si la load function se ejecuta con sesión activa, redirigir al dashboard
export const load: PageServerLoad = async ({ locals, url }) => {
	if (locals.user) {
		const redirectTo = url.searchParams.get('redirect') || '/';
		throw redirect(303, redirectTo);
	}
	return {};
};

export const actions: Actions = {
	default: async ({ request, cookies, url }) => {
		console.log('[Login Action] Recibida solicitud de login');
		const formData = await request.formData();
		const email = (formData.get('email') as string)?.trim();
		const password = formData.get('password') as string;
		const redirectParam = url.searchParams.get('redirect') || '/';
		console.log(`[Login Action] Email: ${email}, Redirect: ${redirectParam}`);

		// ── Validación ──
		if (!email) return fail(400, { email, error: 'El correo electrónico es requerido', missing: 'email' });
		if (!password) return fail(400, { email, error: 'La contraseña es requerida', missing: 'password' });
		if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return fail(400, { email, error: 'Formato de correo inválido' });

		// ── Login directo mock (sin gateway) ──
		const result = mockLogin(email, password);

		if (result.error || !result.data) {
			return fail(401, { email, error: result.error || 'Credenciales inválidas' });
		}

		// ── Setear cookies ──
		setSessionCookies(cookies, result.data.accessToken, result.data.refreshToken);

		// ── En lugar de redirect, devolver éxito para que el cliente lo maneje ──
		return { success: true, redirectTo: redirectParam };
	}
};
