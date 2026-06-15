import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { register } from '$lib/server/auth.server';

// If already authenticated, redirect to dashboard
export const load: PageServerLoad = async ({ locals }) => {
	if (locals.user) {
		throw redirect(303, '/dashboard');
	}
	return {};
};

export const actions: Actions = {
	default: async ({ request, fetch, locals }) => {
		// If already logged in, redirect
		if (locals.user) {
			throw redirect(303, '/dashboard');
		}

		const formData = await request.formData();
		const email = (formData.get('email') as string)?.trim();
		const password = formData.get('password') as string;
		const name = (formData.get('name') as string)?.trim() || undefined;
		const companyName = (formData.get('companyName') as string)?.trim() || undefined;

		// ── Validation ──
		if (!email) return fail(400, { email, error: 'El correo electrónico es requerido', missing: 'email' });
		if (!password) return fail(400, { email, error: 'La contraseña es requerida', missing: 'password' });
		if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
			return fail(400, { email, error: 'Formato de correo inválido' });
		}
		if (password.length < 6) {
			return fail(400, { email, error: 'La contraseña debe tener al menos 6 caracteres' });
		}

		// ── Register via backend ──
		const result = await register(fetch, { email, password, name, companyName });

		if (result.error) {
			return fail(400, { email, error: result.error });
		}

		// ── Success → redirect to login with success message ──
		throw redirect(303, '/login?registered=true');
	}
};
