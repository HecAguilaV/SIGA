import { redirect, fail } from '@sveltejs/kit';
import type { Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { canAccessByRole, MANAGER_ROLES } from '$lib/auth/permissions';

export const actions: Actions = {
	default: async ({ request, fetch, url, locals }) => {
		const user = locals.user;
		if (!user || !canAccessByRole(user.rol, MANAGER_ROLES)) {
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
			throw redirect(303, '/categories');
		} catch (err) {
			if (err instanceof Response || (err as any).status === 303) throw err;
			return fail(500, { error: 'Error de red al crear categoría' });
		}
	}
};
