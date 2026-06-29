import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';

export const load: PageServerLoad = async (event) => {
	const { fetch, locals } = event;
	const user = locals.user;

	let insights: any[] = [];
	let lowStock: any[] = [];
	let anomalies: any[] = [];
	let trends: any[] = [];
	let error = null;

	try {
		// Llamar al endpoint consolidado del BFF en el Gateway
		const res = await fetchWithAuth(fetch, event, '/api/v1/dashboard/insights');

		if (res.ok) {
			const body = await res.json();
			insights = body.insights ?? [];
			lowStock = body.lowStock ?? [];
			anomalies = body.anomalies ?? [];
			trends = body.trends ?? [];
		} else {
			console.warn('[Dashboard Load] BFF endpoint failed, using simplified composition');
			const fallback = await composeDashboardFromServices(fetch, event);
			insights = fallback.insights;
			lowStock = fallback.lowStock;
		}
	} catch (e) {
		console.error('[Dashboard Load] Error:', e);
		error = 'Error al cargar datos del servidor';
	}

	return {
		user,
		insights,
		lowStock,
		anomalies,
		trends,
		error,
		timestamp: Date.now()
	};
};

/**
 * Compone la vista del dashboard llamando a múltiples microservicios.
 */
async function composeDashboardFromServices(fetch: typeof globalThis.fetch, event: any) {
	let productCount = 0;
	let storeCount = 0;
	let lowStockItems: any[] = [];

	// 1. Obtener conteo de productos y stock bajo desde Inventory
	try {
		const stockRes = await fetchWithAuth(fetch, event, '/api/inventory/stock/consolidated?size=100');
		if (stockRes.ok) {
			const body = await stockRes.json();
			// Adaptar: array plano, Spring Page ({content:[...]}), o ConsolidatedStockResponse ({products:[...]})
			const products = Array.isArray(body) ? body : (body.products || body.content || body.items || []);
			productCount = body.totalElements || products.length;
			lowStockItems = products
				.filter((p: any) => p.totalStock < (p.minStock || 10))
				.map((p: any) => ({
					id: p.productId,
					name: p.productName,
					sku: p.sku,
					stock: p.totalStock,
					stockMin: p.minStock || 10
				}));
		}
	} catch (e) { console.error('Error fetching inventory stats:', e); }

	// 2. Obtener conteo de locales
	try {
		const storeRes = await fetchWithAuth(fetch, event, '/api/inventory/stores');
		if (storeRes.ok) {
			const stores = await storeRes.json();
			storeCount = stores.length || 0;
		}
	} catch (e) { console.error('Error fetching store stats:', e); }

	// 3. Obtener anomalías desde el Agente (si aplica) o Sales
	const anomalies: any[] = [];

	return {
		insights: [
			{ id: '1', title: 'Total Productos', value: productCount, icon: 'package', variant: 'primary' },
			{ id: '2', title: 'Total Locales', value: storeCount, icon: 'storefront', variant: 'info' },
			{ id: '3', title: 'Stock Bajo', value: lowStockItems.length, icon: 'warning', variant: 'danger' }
		],
		lowStock: lowStockItems.slice(0, 5),
		anomalies,
		trends: []
	};
}
