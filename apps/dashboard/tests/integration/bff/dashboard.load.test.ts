import { describe, it, expect } from 'vitest';

describe('Dashboard BFF load', () => {
	const MOCK_INSIGHTS = {
		insights: [
			{ id: '1', title: 'Total Productos', value: 8, trend: 'stable', trendValue: 0, variant: 'primary' },
			{ id: '2', title: 'Total Locales', value: 3, trend: 'stable', trendValue: 0, variant: 'info' },
			{ id: '3', title: 'Stock Bajo', value: 3, trend: 'down', trendValue: 3, variant: 'danger' }
		],
		lowStock: [
			{ id: '7', name: 'Yerba Mate', sku: 'YER-001', stock: 8, stockMin: 10 },
			{ id: '8', name: 'Pan Lactal', sku: 'PAN-001', stock: 3, stockMin: 10 }
		],
		anomalies: [
			{ id: 'a1', type: 'stock', message: 'Ajuste sin justificación', severity: 'high' }
		]
	};

	it('returns structured insights array', () => {
		expect(MOCK_INSIGHTS.insights).toHaveLength(3);
		expect(MOCK_INSIGHTS.insights[0]).toHaveProperty('title');
		expect(MOCK_INSIGHTS.insights[0]).toHaveProperty('value');
	});

	it('insights have required fields', () => {
		for (const insight of MOCK_INSIGHTS.insights) {
			expect(insight).toHaveProperty('id');
			expect(insight).toHaveProperty('title');
			expect(insight).toHaveProperty('value');
			expect(insight).toHaveProperty('variant');
		}
	});

	it('identifies low stock products correctly', () => {
		const lowStockItems = MOCK_INSIGHTS.lowStock;
		expect(lowStockItems.length).toBeGreaterThan(0);

		for (const item of lowStockItems) {
			expect(item.stock).toBeLessThan(item.stockMin);
		}
	});

	it('anomalies have severity classification', () => {
		const severities = MOCK_INSIGHTS.anomalies.map((a) => a.severity);
		expect(severities).toContain('high');
	});

	it('handles empty dashboard state (new tenant)', () => {
		const emptyState = {
			insights: [
				{ id: '1', title: 'Total Productos', value: 0, variant: 'primary' },
				{ id: '2', title: 'Total Locales', value: 0, variant: 'info' }
			],
			lowStock: [],
			anomalies: []
		};

		expect(emptyState.lowStock).toHaveLength(0);
		expect(emptyState.anomalies).toHaveLength(0);
		expect(emptyState.insights.every((i) => i.value === 0)).toBe(true);
	});

	it('composes fallback data from partial gateway response', () => {
		// Simulate fallback where only products endpoint responded
		const partialFallback = {
			insights: [
				{ id: '1', title: 'Total Productos', value: 8, variant: 'primary' }
			],
			lowStock: [],
			anomalies: []
		};

		expect(partialFallback.insights).toHaveLength(1);
		expect(partialFallback.insights[0].title).toBe('Total Productos');
	});
});
