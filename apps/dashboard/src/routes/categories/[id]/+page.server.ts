import { error, redirect, fail } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';

export const load: PageServerLoad = async (event) => {
	const { params, fetch, locals } = event;
	const user = locals.user;
	
	if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
		error(403, 'No tienes permisos');
	}

	try {
		const res = await fetchWithAuth(fetch, event, `/api/inventory/categories/${params.id}`);
		if (!res.ok) {
			if (res.status === 404) error(404, 'Categoría no encontrada');
			error(res.status, 'Error al obtener categoría');
		}
		return { category: await res.json() };
	} catch (err) {
		console.error('[Category Detail Load] Error:', err);
		if ((err as any).status) throw err;
		error(503, 'Error de conexión con el servidor de inventario');
	}
};

export const actions: Actions = {
	default: async (event) => {
		const { params, request, fetch, locals } = event;
		const user = locals.user;
		
		if (!user || user.rol !== 'ADMINISTRATOR') {
			return fail(403, { error: 'Solo administradores pueden editar categorías' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, event, `/api/inventory/categories/${params.id}`, {
				method: 'PUT',
				body: JSON.stringify(data)
			});
			if (!res.ok) {
				const body = await res.json();
				return fail(res.status, { error: body.message || 'Error al actualizar' });
			}
			throw redirect(303, '/categories');
		} catch (err) {
			if (err instanceof Response || (err as any).status === 303) throw err;
			return fail(500, { error: 'Error de red al actualizar' });
		}
	}
};
