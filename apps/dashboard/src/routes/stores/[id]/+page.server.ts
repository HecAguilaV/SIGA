import { error, redirect, fail } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { canAccessByRole, VIEWER_ROLES } from '$lib/auth/permissions';

export const load: PageServerLoad = async (event) => {
	const { params, fetch, locals } = event;
	const user = locals.user;
	
	if (!user || !canAccessByRole(user.rol, VIEWER_ROLES)) {
		error(403, 'No tienes permisos');
	}

	try {
		const res = await fetchWithAuth(fetch, event, `/api/inventory/stores/${params.id}`);
		if (!res.ok) {
			if (res.status === 404) error(404, 'Local no encontrado');
			error(res.status, 'Error al obtener local');
		}
		return { store: await res.json() };
	} catch (err) {
		console.error('[Store Detail Load] Error:', err);
		if ((err as any).status) throw err;
		error(503, 'Error de conexión con el servidor de inventario');
	}
};

export const actions: Actions = {
	default: async (event) => {
		const { params, request, fetch, locals } = event;
		const user = locals.user;
		
		if (!user || !canAccessByRole(user.rol, VIEWER_ROLES)) {
			return fail(403, { error: 'Sin permisos' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, event, `/api/inventory/stores/${params.id}`, {
				method: 'PUT',
				body: JSON.stringify(data)
			});
			if (!res.ok) {
				const body = await res.json();
				return fail(res.status, { error: body.message || 'Error al actualizar' });
			}
			throw redirect(303, '/stores');
		} catch (err) {
			if (err instanceof Response || (err as any).status === 303) throw err;
			return fail(500, { error: 'Error de red al actualizar' });
		}
	}
};
