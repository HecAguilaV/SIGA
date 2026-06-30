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
			throw redirect(303, '/stores');
		} catch (err) {
			if (err instanceof Response || (err as any).status === 303) throw err;
			return fail(500, { error: 'Error de red al crear local' });
		}
	}
};
