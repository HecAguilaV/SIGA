import type { PageServerLoad, Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { fail } from '@sveltejs/kit';

export const load: PageServerLoad = async (event) => {
	const { fetch } = event;

	let products: any[] = [];
	let activeShift: any = null;

	try {
		// 1. Obtener productos reales
		const productsRes = await fetchWithAuth(fetch, event, '/api/inventory/products?size=100');
		if (productsRes.ok) {
			const body = await productsRes.json();
			products = body.content || body.items || [];
		}

		// 2. Verificar si hay un turno (caja) abierto para el usuario actual
		const shiftRes = await fetchWithAuth(fetch, event, '/api/sales/shifts/active');
		if (shiftRes.ok) {
			activeShift = await shiftRes.json();
		}
	} catch (e) {
		console.error('[POS Load] Critical Error:', e);
	}

	return {
		products,
		activeShift
	};
};

export const actions: Actions = {
	openShift: async (event) => {
		const { request, fetch } = event;
		const formData = await request.formData();
		const initialBalance = formData.get('initialBalance');

		try {
			const res = await fetchWithAuth(fetch, event, '/api/sales/shifts/open', {
				method: 'POST',
				body: JSON.stringify({
					initialBalance: parseFloat(initialBalance?.toString() || '0'),
					currency: 'CLP'
				})
			});

			if (!res.ok) {
				const error = await res.json();
				return fail(res.status, { message: error.message || 'Error al abrir caja' });
			}

			return { success: true };
		} catch (e) {
			return fail(500, { message: 'Error de conexión con el servicio de ventas' });
		}
	},

	checkout: async (event) => {
		const { request, fetch } = event;
		const formData = await request.formData();
		const cartData = formData.get('cart');
		const paymentMethod = formData.get('paymentMethod');

		if (!cartData) return fail(400, { message: 'El carrito está vacío' });

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
			return fail(500, { message: 'Error interno en el procesamiento de venta' });
		}
	}
};
