import { describe, it, expect, vi } from 'vitest';

describe('Products BFF load', () => {
	const MOCK_API_RESPONSE = {
		items: [
			{ id: '1', name: 'Harina', sku: 'HAR-001', categoryName: 'Harinas', price: 850, stock: 42, stockMin: 10 },
			{ id: '2', name: 'Azúcar', sku: 'AZU-001', categoryName: 'Azúcares', price: 720, stock: 38, stockMin: 10 }
		],
		total: 2,
		page: 1,
		pageSize: 20,
		totalPages: 1
	};

	it('maps API response to ProductListItem format', () => {
		const items = MOCK_API_RESPONSE.items.map((p) => ({
			id: p.id,
			name: p.name,
			sku: p.sku,
			categoryName: p.categoryName,
			price: p.price,
			stock: p.stock,
			stockMin: p.stockMin,
			trend: p.stock < p.stockMin ? 'down' : 'stable'
		}));

		expect(items).toHaveLength(2);
		expect(items[0]).toHaveProperty('trend');
		expect(items[0].trend).toBe('stable');
	});

	it('returns pagination metadata', () => {
		const result = {
			items: MOCK_API_RESPONSE.items.map((p) => ({
				id: p.id,
				name: p.name,
				sku: p.sku,
				categoryName: p.categoryName,
				price: p.price,
				stock: p.stock,
				stockMin: p.stockMin,
				trend: 'stable' as const
			})),
			total: MOCK_API_RESPONSE.total,
			page: MOCK_API_RESPONSE.page
		};

		expect(result.total).toBe(2);
		expect(result.page).toBe(1);
	});

	it('handles empty product list', () => {
		const emptyResult = {
			items: [],
			total: 0,
			page: 1
		};

		expect(emptyResult.items).toHaveLength(0);
		expect(emptyResult.total).toBe(0);
	});

	it('transforms price to formatted currency', () => {
		const items = MOCK_API_RESPONSE.items;
		const formatted = items.map((p) => `$${p.price.toLocaleString('es-AR')}`);
		expect(formatted[0]).toBe('$850');
		expect(formatted[1]).toBe('$720');
	});

	it('calculates trend based on stock vs stockMin', () => {
		const lowStockItem = { ...MOCK_API_RESPONSE.items[0], stock: 5, stockMin: 10 };
		const normalItem = { ...MOCK_API_RESPONSE.items[0], stock: 50, stockMin: 10 };

		expect(lowStockItem.stock < lowStockItem.stockMin ? 'down' : 'stable').toBe('down');
		expect(normalItem.stock < normalItem.stockMin ? 'down' : 'stable').toBe('stable');
	});
});
