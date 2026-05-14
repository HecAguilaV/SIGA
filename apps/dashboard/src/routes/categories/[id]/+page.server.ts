import { error, redirect, fail } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { MOCK_CATEGORIES } from '$lib/server/mock-data';

export const load: PageServerLoad = async ({ params, fetch, url, locals }) => {
	const user = locals.user;
	if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
		error(403, 'No tienes permisos');
	}

	try {
		const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/inventory/categories/${params.id}`);
		if (!res.ok) {
			if (res.status === 404) error(404, 'Categoría no encontrada');
			error(res.status, res.statusText);
		}
		return { category: await res.json() };
	} catch {
		const category = MOCK_CATEGORIES.find((c) => c.id === params.id);
		if (!category) error(404, 'Categoría no encontrada');
		return { category };
	}
};

export const actions: Actions = {
	default: async ({ params, request, fetch, url, locals }) => {
		const user = locals.user;
		if (!user || user.rol !== 'ADMINISTRATOR') {
			return fail(403, { error: 'Solo administradores pueden editar categorías' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/inventory/categories/${params.id}`, {
				method: 'PUT',
				body: JSON.stringify(data)
			});
			if (!res.ok) return fail(res.status, { error: 'Error al actualizar' });
			redirect(303, '/categories');
		} catch {
			redirect(303, '/categories');
		}
	}
};
