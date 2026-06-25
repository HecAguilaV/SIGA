import { redirect, fail } from '@sveltejs/kit';
import type { Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { canAccessByRole, VIEWER_ROLES } from '$lib/auth/permissions';

export const actions: Actions = {
	default: async ({ request, fetch, url, locals }) => {
		const user = locals.user;
		if (!user || !canAccessByRole(user.rol, VIEWER_ROLES)) {
			return fail(403, { error: 'Sin permisos' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, '/api/stores', {
				method: 'POST',
				body: JSON.stringify(data)
			});
			if (!res.ok) return fail(res.status, { error: 'Error al crear local' });
			redirect(303, '/stores');
		} catch {
			redirect(303, '/stores');
		}
	}
};
