import { error, redirect, fail } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { canAccessByRole, MANAGER_ROLES } from '$lib/auth/permissions';

export const load: PageServerLoad = async (event) => {
	const { params, fetch, locals } = event;
	const user = locals.user;
	
	if (!user || !canAccessByRole(user.rol, MANAGER_ROLES)) {
		error(403, 'No tienes permisos');
	}

	try {
		const res = await fetchWithAuth(fetch, event, `/api/auth/users/${params.id}`);
		if (!res.ok) {
			if (res.status === 404) error(404, 'Usuario no encontrado');
			error(res.status, 'Error al obtener usuario');
		}
		return { usr: await res.json() };
	} catch (err) {
		console.error('[User Detail Load] Error:', err);
		if ((err as any).status) throw err;
		error(503, 'Error de conexión con el servicio de autenticación');
	}
};

export const actions: Actions = {
	default: async (event) => {
		const { params, request, fetch, locals } = event;
		const user = locals.user;
		
		if (!user || !canAccessByRole(user.rol, MANAGER_ROLES)) {
			return fail(403, { error: 'Sin permisos' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, event, `/api/auth/users/${params.id}`, {
				method: 'PUT',
				body: JSON.stringify(data)
			});
			if (!res.ok) {
				const body = await res.json();
				return fail(res.status, { error: body.message || 'Error al actualizar' });
			}
			throw redirect(303, '/users');
		} catch (err) {
			if (err instanceof Response || (err as any).status === 303) throw err;
			return fail(500, { error: 'Error de red al actualizar' });
		}
	}
};
