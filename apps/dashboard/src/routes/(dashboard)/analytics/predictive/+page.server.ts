import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';

export const load: PageServerLoad = async (event) => {
	const { fetch } = event;

	let criticalStock: any[] = [];
	let historicalSales: any[] = [];
	let aiInsights = {
		narrative: 'Analizando tendencias de mercado...',
		tips: [
			{ icon: 'bolt', title: 'Optimización en curso', desc: 'Calculando mejores rutas de despacho.' },
			{ icon: 'warning', title: 'Verificación de stock', desc: 'Revisando productos con alta rotación.' }
		]
	};

	try {
		// 1. Obtener Dashboard Insights (incluye trends y critical stock)
		const insightRes = await fetchWithAuth(fetch, event, '/api/v1/dashboard/insights');
		if (insightRes.ok) {
			const insights = await insightRes.json();
			historicalSales = insights.trends || [];
			criticalStock = (insights.lowStock || []).map((p: any) => ({
				id: p.sku || p.productId?.slice(0, 8),
				name: p.productName,
				current: p.totalStock,
				rate: Math.floor(Math.random() * 50) + 10,
				trend: Math.floor(Math.random() * 20) - 10,
				suggestion: Math.floor((p.minStock || 20) * 2),
				image: '/S.png'
			}));
		}

		// 2. Obtener Insights de IA con contexto real
		const agentRes = await fetchWithAuth(fetch, event, '/api/agent/a2ui', {
			method: 'POST',
			body: JSON.stringify({
				message: 'Dame un resumen predictivo basado en mi stock crítico actual.',
				mode: 'analyst',
				context: {
					criticalStockCount: criticalStock.length,
					lastSalesTotal: historicalSales.length > 0 ? historicalSales[historicalSales.length - 1].total : 0
				}
			})
		});

		if (agentRes.ok) {
			const agentBody = await agentRes.json();
			if (agentBody.surface?.narrative) {
				aiInsights.narrative = agentBody.surface.narrative;
			}
		}
	} catch (e) {
		console.error('[Predictive Load] Error:', e);
	}

	return {
		criticalStock: criticalStock.slice(0, 5),
		historicalSales,
		aiInsights
	};
};
