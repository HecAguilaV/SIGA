import type { PageServerLoad, Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { fail } from '@sveltejs/kit';

export const load: PageServerLoad = async (event) => {
	const { fetch } = event;

	// Cargar productos iniciales para el POS (los más vendidos o recientes)
	let products: any[] = [];
	try {
		const res = await fetchWithAuth(fetch, event, '/api/inventory/products?size=50');
		if (res.ok) {
			const body = await res.json();
			products = body.content || body.items || [];
		}
	} catch (e) {
		console.error('[POS Load] Error:', e);
	}

	return {
		products
	};
};

export const actions: Actions = {
	checkout: async (event) => {
		const { request, fetch } = event;
		const formData = await request.formData();
		const cartData = formData.get('cart');
		const paymentMethod = formData.get('paymentMethod');

		if (!cartData) {
			return fail(400, { message: 'El carrito está vacío' });
		}

		try {
			const cart = JSON.parse(cartData.toString());
			const items = cart.map((item: any) => ({
				productId: item.id,
				quantity: item.quantity,
				price: item.price
			}));

			const res = await fetchWithAuth(fetch, event, '/api/sales/checkout', {
				method: 'POST',
				body: JSON.stringify({
					items,
					paymentMethod,
					currency: 'CLP'
				})
			});

			if (!res.ok) {
				const error = await res.json();
				return fail(res.status, { message: error.message || 'Error al procesar la venta' });
			}

			return { success: true };
		} catch (e) {
			console.error('[POS Checkout] Error:', e);
			return fail(500, { message: 'Error interno del servidor' });
		}
	}
};
