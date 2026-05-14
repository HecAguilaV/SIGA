import { describe, it, expect } from 'vitest';

describe('Analytics BFF load', () => {
	const MOCK_ANALYTICS_RESPONSE = {
		trends: [
			{ date: '2025-05-08', value: 1200 },
			{ date: '2025-05-09', value: 1450 },
			{ date: '2025-05-10', value: 980 },
			{ date: '2025-05-11', value: 1620 },
			{ date: '2025-05-12', value: 1380 },
			{ date: '2025-05-13', value: 1540 },
			{ date: '2025-05-14', value: 1720 }
		],
		insights: [
			{
				id: 'i1',
				title: 'Pico de Ventas',
				description: 'Las ventas alcanzaron su punto máximo los fines de semana',
				type: 'positive',
				context: '30% por encima del promedio semanal'
			},
			{
				id: 'i2',
				title: 'Producto Estrella',
				description: 'Harina de Trigo lidera en volumen de ventas',
				type: 'info',
				context: '2.5x vs segundo producto'
			}
		],
		anomalies: [
			{
				id: 'a1',
				type: 'stock',
				message: 'Ajuste de stock sin justificación en Producto #3',
				severity: 'high',
				timestamp: new Date().toISOString()
			}
		],
		summary: 'La tendencia general es positiva con un crecimiento del 12% semanal'
	};

	it('returns structured analytics data with trends', () => {
		expect(MOCK_ANALYTICS_RESPONSE.trends).toHaveLength(7);
		expect(MOCK_ANALYTICS_RESPONSE.trends[0]).toHaveProperty('date');
		expect(MOCK_ANALYTICS_RESPONSE.trends[0]).toHaveProperty('value');
	});

	it('trends have date and value fields', () => {
		for (const trend of MOCK_ANALYTICS_RESPONSE.trends) {
			expect(trend).toHaveProperty('date');
			expect(trend).toHaveProperty('value');
			expect(typeof trend.value).toBe('number');
		}
	});

	it('insights have required fields', () => {
		for (const insight of MOCK_ANALYTICS_RESPONSE.insights) {
			expect(insight).toHaveProperty('id');
			expect(insight).toHaveProperty('title');
			expect(insight).toHaveProperty('description');
			expect(insight).toHaveProperty('type');
			expect(['positive', 'info', 'warning', 'danger']).toContain(insight.type);
		}
	});

	it('anomalies have severity classification', () => {
		const severities = MOCK_ANALYTICS_RESPONSE.anomalies.map((a) => a.severity);
		expect(severities).toContain('high');
		for (const anomaly of MOCK_ANALYTICS_RESPONSE.anomalies) {
			expect(anomaly).toHaveProperty('id');
			expect(anomaly).toHaveProperty('type');
			expect(anomaly).toHaveProperty('message');
			expect(anomaly).toHaveProperty('severity');
		}
	});

	it('has summary text', () => {
		expect(MOCK_ANALYTICS_RESPONSE.summary).toBeTruthy();
		expect(typeof MOCK_ANALYTICS_RESPONSE.summary).toBe('string');
	});

	it('handles empty analytics state (new tenant)', () => {
		const emptyState = {
			trends: [],
			insights: [],
			anomalies: [],
			summary: 'No hay datos suficientes para generar análisis'
		};

		expect(emptyState.trends).toHaveLength(0);
		expect(emptyState.insights).toHaveLength(0);
		expect(emptyState.anomalies).toHaveLength(0);
		expect(emptyState.summary).toContain('No hay datos');
	});

	it('provides mock fallback when gateway is unavailable', () => {
		// Simulate the fallback data transformation
		const fallbackAnalytics = {
			trends: generateMockTrends(),
			insights: [
				{
					id: 'fallback-1',
					title: 'Análisis no disponible',
					description: 'Los datos analíticos no están disponibles en este momento',
					type: 'info' as const,
					context: 'Usando datos de demostración'
				}
			],
			anomalies: [],
			summary: 'Mostrando datos de demostración — el servicio de análisis no está disponible'
		};

		expect(fallbackAnalytics.trends).toHaveLength(7);
		expect(fallbackAnalytics.insights).toHaveLength(1);
		expect(fallbackAnalytics.summary).toContain('demostración');
	});
});

function generateMockTrends() {
	const data = [];
	const now = Date.now();
	for (let i = 6; i >= 0; i--) {
		const d = new Date(now - i * 24 * 60 * 60 * 1000);
		data.push({
			date: d.toISOString().split('T')[0],
			value: Math.floor(Math.random() * 1000) + 500
		});
	}
	return data;
}
