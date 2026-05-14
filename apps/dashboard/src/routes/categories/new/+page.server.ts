import { redirect, fail } from '@sveltejs/kit';
import type { Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';

export const actions: Actions = {
	default: async ({ request, fetch, url, locals }) => {
		const user = locals.user;
		if (!user || user.rol !== 'ADMINISTRATOR') {
			return fail(403, { error: 'Solo administradores pueden crear categorías' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, '/api/inventory/categories', {
				method: 'POST',
				body: JSON.stringify(data)
			});
			if (!res.ok) return fail(res.status, { error: 'Error al crear categoría' });
			redirect(303, '/categories');
		} catch {
			redirect(303, '/categories');
		}
	}
};
