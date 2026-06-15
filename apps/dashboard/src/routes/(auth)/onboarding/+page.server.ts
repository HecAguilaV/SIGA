import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { updateCustomer } from '$lib/server/auth.server';

/**
 * Load — Guard: redirect to login if no session.
 * Also validates the user has a placeholder name (needs onboarding).
 */
export const load: PageServerLoad = async ({ locals, url, cookies }) => {
	if (!locals.user) {
		throw redirect(303, `/login?redirect=${encodeURIComponent(url.pathname)}`);
	}

	// If user already has a real name (not placeholder), skip onboarding
	const emailPrefix = locals.user.email?.split('@')[0];
	if (locals.user.name && locals.user.name !== emailPrefix) {
		throw redirect(303, '/dashboard');
	}

	return {
		user: {
			email: locals.user.email,
			name: locals.user.name,
			id: locals.user.id
		}
	};
};

export const actions: Actions = {
	default: async ({ request, cookies, fetch, locals }) => {
		if (!locals.user) {
			throw redirect(303, '/login');
		}

		const formData = await request.formData();
		const name = (formData.get('name') as string)?.trim();
		const companyName = (formData.get('companyName') as string)?.trim() || undefined;

		// Name is required for onboarding
		if (!name) {
			return fail(400, { error: 'El nombre es requerido para completar tu perfil' });
		}

		const token = cookies.get('siga_token');
		if (!token) {
			throw redirect(303, '/login');
		}

		// Update customer profile via PUT /api/v1/auth/customers/{id}
		const result = await updateCustomer(fetch, locals.user.id, { name, companyName }, token);

		if (result.error) {
			return fail(400, { error: result.error });
		}

		// Success → redirect to dashboard
		throw redirect(303, '/dashboard');
	}
};
