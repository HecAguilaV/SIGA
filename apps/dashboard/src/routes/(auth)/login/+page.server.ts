import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { setSessionCookies, login } from '$lib/server/auth.server';

// Si la load function se ejecuta con sesión activa, redirigir al dashboard
export const load: PageServerLoad = async ({ locals, url }) => {
	if (locals.user) {
		const redirectTo = url.searchParams.get('redirect') || '/dashboard';
		throw redirect(303, redirectTo);
	}
	return {};
};

export const actions: Actions = {
	default: async ({ request, cookies, url, fetch }) => {
		const formData = await request.formData();
		const email = (formData.get('email') as string)?.trim();
		const password = formData.get('password') as string;
		const redirectParam = url.searchParams.get('redirect') || '/dashboard';

		// ── Validación ──
		if (!email) return fail(400, { email, error: 'El correo electrónico es requerido', missing: 'email' });
		if (!password) return fail(400, { email, error: 'La contraseña es requerida', missing: 'password' });
		if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return fail(400, { email, error: 'Formato de correo inválido' });

		// ── Login real vía gateway ──
		const result = await login(fetch, email, password);

		if (result.error || !result.data) {
			return fail(401, { email, error: result.error || 'Credenciales inválidas' });
		}

		// ── Setear cookies ──
		// Nota: El backend Kotlin devuelve 'token' y 'refreshToken' (o similar)
		// Ajustamos a lo que espera setSessionCookies
		setSessionCookies(
			cookies, 
			result.data.token || result.data.accessToken || '', 
			result.data.refreshToken || ''
		);

		// ── Redirigir al dashboard directamente ──
		throw redirect(303, redirectParam);
	}
};
