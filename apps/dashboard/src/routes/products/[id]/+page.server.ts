import { error, redirect, fail } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';

export const load: PageServerLoad = async (event) => {
	const { fetch, params, locals } = event;
	const user = locals.user;
	
	if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
		error(403, 'No tienes permisos');
	}

	const { id } = params;

	try {
		// 1. Obtener el producto
		const productRes = await fetchWithAuth(fetch, event, `/api/inventory/products/${id}`);
		if (!productRes.ok) {
			if (productRes.status === 404) error(404, 'Producto no encontrado');
			error(productRes.status, 'Error al obtener producto');
		}
		const product = await productRes.json();

		// 2. Obtener categorías para el selector
		let categories: any[] = [];
		const catRes = await fetchWithAuth(fetch, event, '/api/inventory/categories');
		if (catRes.ok) {
			categories = await catRes.json();
		}

		return { 
			product: {
				...product,
				id: product.id || product.productId // Normalizar ID
			}, 
			categories 
		};
	} catch (err) {
		console.error('[Product Detail Load] Error:', err);
		if ((err as any).status) throw err;
		error(503, 'Error de conexión con el servidor de inventario');
	}
};

export const actions: Actions = {
	default: async (event) => {
		const { params, request, fetch, locals } = event;
		const user = locals.user;
		
		if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
			return fail(403, { error: 'Sin permisos' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, event, `/api/inventory/products/${params.id}`, {
				method: 'PUT',
				body: JSON.stringify(data)
			});

			if (!res.ok) {
				const body = await res.json();
				return fail(res.status, { error: body.message || 'Error al actualizar' });
			}

			throw redirect(303, '/products');
		} catch (err) {
			if (err instanceof Response || (err as any).status === 303) throw err;
			return fail(500, { error: 'Error de red al actualizar' });
		}
	}
};
