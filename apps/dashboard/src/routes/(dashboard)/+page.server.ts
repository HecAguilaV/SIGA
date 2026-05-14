import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { MOCK_PRODUCTS, MOCK_STORES } from '$lib/server/mock-data';

export const load: PageServerLoad = async ({ fetch, url, locals }) => {
	const user = locals.user;

	let insights: any[] = [];
	let lowStock: any[] = [];
	let anomalies: any[] = [];
	let error = null;

	try {
		const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, '/api/v1/dashboard/insights');

		if (res.ok) {
			const body = await res.json();
			insights = body.insights ?? [];
			lowStock = body.lowStock ?? [];
			anomalies = body.anomalies ?? [];
		} else if (res.status === 404) {
			// Endpoint not available — use composition fallback
			const fallback = await composeFallback(fetch, url);
			insights = fallback.insights;
			lowStock = fallback.lowStock;
			anomalies = fallback.anomalies;
		} else {
			error = 'Servicio no disponible';
		}
	} catch {
		// Fallback to mock data
		const fallback = mockDashboardFallback();
		insights = fallback.insights;
		lowStock = fallback.lowStock;
		anomalies = fallback.anomalies;
	}

	return {
		user,
		insights,
		lowStock,
		anomalies,
		error,
		timestamp: Date.now()
	};
};

async function composeFallback(fetch: typeof globalThis.fetch, url: URL) {
	let productCount = 0;
	let storeCount = 0;

	try {
		const prodRes = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, '/api/inventory/products?size=1');
		if (prodRes.ok) {
			const body = await prodRes.json();
			productCount = body.total ?? 0;
		}
	} catch { /* ignore */ }

	try {
		const storeRes = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, '/api/stores?size=1');
		if (storeRes.ok) {
			const body = await storeRes.json();
			storeCount = body.total ?? 0;
		}
	} catch { /* ignore */ }

	return {
		insights: [
			{ id: '1', title: 'Total Productos', value: productCount, icon: 'package', variant: 'primary' },
			{ id: '2', title: 'Total Locales', value: storeCount, icon: 'storefront', variant: 'info' }
		],
		lowStock: [],
		anomalies: []
	};
}

function mockDashboardFallback() {
	const lowStockItems = MOCK_PRODUCTS
		.filter((p) => p.stock < p.stockMin)
		.map((p) => ({
			id: p.id,
			name: p.name,
			sku: p.sku,
			stock: p.stock,
			stockMin: p.stockMin
		}));

	return {
		insights: [
			{ id: '1', title: 'Total Productos', value: MOCK_PRODUCTS.length, trend: 'stable', trendValue: 0, icon: 'package', variant: 'primary' as const },
			{ id: '2', title: 'Total Locales', value: MOCK_STORES.length, trend: 'stable', trendValue: 0, icon: 'storefront', variant: 'info' as const },
			{ id: '3', title: 'Stock Bajo', value: lowStockItems.length, trend: 'down', trendValue: lowStockItems.length, icon: 'warning', variant: 'danger' as const },
			{ id: '4', title: 'Valor Inventario', value: `$${MOCK_PRODUCTS.reduce((s, p) => s + p.price * p.stock, 0).toLocaleString('es-AR')}`, trend: 'up', trendValue: 5, icon: 'coin', variant: 'success' as const }
		],
		lowStock: lowStockItems,
		anomalies: [
			{ id: 'a1', type: 'stock', message: 'Ajuste de stock sin justificación en Producto #3', severity: 'high' as const, timestamp: new Date().toISOString() },
			{ id: 'a2', type: 'price', message: 'Cambio de precio fuera de horario laboral en 2 productos', severity: 'medium' as const, timestamp: new Date().toISOString() }
		]
	};
}
