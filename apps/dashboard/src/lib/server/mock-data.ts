/**
 * Mock data helpers for development.
 * Used when the gateway is not available.
 */

export const MOCK_PRODUCTS = [
	{ id: '1', name: 'Harina de Trigo 1kg', sku: 'HAR-001', categoryName: 'Harinas', price: 850, stock: 42, stockMin: 10, active: true, categoryId: 'cat1', description: 'Harina de trigo refinada', storeId: 'store1', createdAt: '2025-01-15', updatedAt: '2025-06-01' },
	{ id: '2', name: 'Azúcar Blanca 1kg', sku: 'AZU-001', categoryName: 'Azúcares', price: 720, stock: 38, stockMin: 10, active: true, categoryId: 'cat2', description: 'Azúcar blanca refinada', storeId: 'store1', createdAt: '2025-01-15', updatedAt: '2025-06-01' },
	{ id: '3', name: 'Aceite de Girasol 1.5L', sku: 'ACE-001', categoryName: 'Aceites', price: 1200, stock: 25, stockMin: 5, active: true, categoryId: 'cat3', description: 'Aceite de girasol refinado', storeId: 'store1', createdAt: '2025-02-01', updatedAt: '2025-06-10' },
	{ id: '4', name: 'Leche Entera 1L', sku: 'LEC-001', categoryName: 'Lácteos', price: 650, stock: 15, stockMin: 20, active: true, categoryId: 'cat4', description: 'Leche entera pasteurizada', storeId: 'store2', createdAt: '2025-02-15', updatedAt: '2025-06-15' },
	{ id: '5', name: 'Arroz Largo Fino 1kg', sku: 'ARR-001', categoryName: 'Granos', price: 780, stock: 60, stockMin: 15, active: true, categoryId: 'cat5', description: 'Arroz largo fino', storeId: 'store1', createdAt: '2025-03-01', updatedAt: '2025-05-20' },
	{ id: '6', name: 'Fideos Tallarín 500g', sku: 'FID-001', categoryName: 'Pastas', price: 420, stock: 90, stockMin: 20, active: true, categoryId: 'cat6', description: 'Fideos tallarín', storeId: 'store2', createdAt: '2025-03-10', updatedAt: '2025-06-05' },
	{ id: '7', name: 'Yerba Mate 1kg', sku: 'YER-001', categoryName: 'Bebidas', price: 1500, stock: 8, stockMin: 10, active: true, categoryId: 'cat7', description: 'Yerba mate tradicional', storeId: 'store1', createdAt: '2025-03-15', updatedAt: '2025-06-18' },
	{ id: '8', name: 'Pan Lactal 500g', sku: 'PAN-001', categoryName: 'Panificados', price: 580, stock: 3, stockMin: 10, active: true, categoryId: 'cat8', description: 'Pan lactal blanco', storeId: 'store2', createdAt: '2025-04-01', updatedAt: '2025-06-20' }
];

export const MOCK_STORES = [
	{ id: 'store1', name: 'Sucursal Centro', address: 'Av. Siempre Viva 123', phone: '555-0101', email: 'centro@siga.cl', active: true, productCount: 35, createdAt: '2024-01-01', updatedAt: '2025-05-01' },
	{ id: 'store2', name: 'Sucursal Norte', address: 'Av. Libertador 456', phone: '555-0102', email: 'norte@siga.cl', active: true, productCount: 28, createdAt: '2024-03-15', updatedAt: '2025-05-15' },
	{ id: 'store3', name: 'Depósito Central', address: 'Ruta 8 Km 15', phone: '555-0103', email: 'deposito@siga.cl', active: true, productCount: 120, createdAt: '2024-06-01', updatedAt: '2025-04-20' }
];

export const MOCK_CATEGORIES = [
	{ id: 'cat1', name: 'Harinas', description: 'Harinas y derivados', productCount: 12, active: true },
	{ id: 'cat2', name: 'Azúcares', description: 'Azúcares y edulcorantes', productCount: 8, active: true },
	{ id: 'cat3', name: 'Aceites', description: 'Aceites y grasas', productCount: 15, active: true },
	{ id: 'cat4', name: 'Lácteos', description: 'Productos lácteos', productCount: 20, active: true },
	{ id: 'cat5', name: 'Granos', description: 'Granos y legumbres', productCount: 10, active: true },
	{ id: 'cat6', name: 'Pastas', description: 'Pastas y fideos', productCount: 18, active: true },
	{ id: 'cat7', name: 'Bebidas', description: 'Bebidas en general', productCount: 25, active: true },
	{ id: 'cat8', name: 'Panificados', description: 'Pan y panificados', productCount: 6, active: true }
];

export const MOCK_USERS = [
	{ id: 'user1', email: 'demo@siga.cl', name: 'Demo Principal', rol: 'ADMINISTRATOR', tenantId: 'tenant1', active: true, principalType: 'user' as const, createdAt: '2024-01-01', updatedAt: '2025-06-01' },
	{ id: 'user2', email: 'oper@siga.cl', name: 'Operador Juan', rol: 'OPERATOR', tenantId: 'tenant1', active: true, principalType: 'user' as const, createdAt: '2024-02-01', updatedAt: '2025-05-15' },
	{ id: 'user3', email: 'caja@siga.cl', name: 'Cajero Pedro', rol: 'CASHIER', tenantId: 'tenant1', active: true, principalType: 'user' as const, createdAt: '2024-03-01', updatedAt: '2025-04-20' }
];

export function paginateMock<T>(items: T[], page: number, pageSize: number): { items: T[]; total: number; page: number; pageSize: number; totalPages: number } {
	const start = (page - 1) * pageSize;
	const paged = items.slice(start, start + pageSize);
	return {
		items: paged,
		total: items.length,
		page,
		pageSize,
		totalPages: Math.ceil(items.length / pageSize)
	};
}

export function searchMock<T extends Record<string, unknown>>(items: T[], search: string, fields: (keyof T)[]): T[] {
	if (!search.trim()) return items;
	const q = search.toLowerCase();
	return items.filter((item) =>
		fields.some((field) => {
			const val = item[field];
			return val != null && String(val).toLowerCase().includes(q);
		})
	);
}
