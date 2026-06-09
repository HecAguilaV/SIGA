import { redirect } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ locals }) => {
	// Si el usuario ya está autenticado, redirigir directo al dashboard
	if (locals.user) {
		throw redirect(303, '/dashboard');
	}
	return {};
};
