import { error, redirect, fail } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { MOCK_PRODUCTS } from '$lib/server/mock-data';

export const load: PageServerLoad = async ({ params, fetch, url, locals }) => {
	const user = locals.user;
	if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
		error(403, 'No tienes permisos');
	}

	const { id } = params;

	try {
		const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/inventory/products/${id}`);

		if (!res.ok) {
			if (res.status === 404) error(404, 'Producto no encontrado');
			if (res.status === 403) error(403, 'Sin permisos');
			error(res.status, res.statusText);
		}

		const product = await res.json();
		return { product, categories: [] };
	} catch {
		// Fallback mock
		const product = MOCK_PRODUCTS.find((p) => p.id === id);
		if (!product) error(404, 'Producto no encontrado');

		return {
			product,
			categories: []
		};
	}
};

export const actions: Actions = {
	default: async ({ params, request, fetch, url, locals }) => {
		const user = locals.user;
		if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
			return fail(403, { error: 'Sin permisos' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/inventory/products/${params.id}`, {
				method: 'PUT',
				body: JSON.stringify(data)
			});

			if (!res.ok) {
				if (res.status === 409) {
					const body = await res.json();
					return fail(409, { error: body.message || 'Conflicto al actualizar', field: body.field });
				}
				return fail(res.status, { error: 'Error al actualizar' });
			}

			redirect(303, '/products');
		} catch {
			// Mock success
			redirect(303, '/products');
		}
	}
};
