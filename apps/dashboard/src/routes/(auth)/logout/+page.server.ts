import { redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { logout, clearSessionCookies } from '$lib/server/auth.server';

export const load: PageServerLoad = async () => {
	// La página de logout no debería renderizarse directamente
	throw redirect(303, '/login');
};

export const actions: Actions = {
	default: async (event) => {
		const { request, fetch, cookies, url } = event;

		// Llamar al gateway para invalidar el refresh token
		await logout(fetch, event);

		// Limpiar cookies de sesión
		clearSessionCookies(event);

		// Redirigir a login
		throw redirect(303, '/login');
	}
};
