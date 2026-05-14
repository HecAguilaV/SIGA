import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { MOCK_PRODUCTS, paginateMock, searchMock } from '$lib/server/mock-data';
import type { ProductListItem } from '$lib/types/inventory';

export const load: PageServerLoad = async ({ fetch, url, locals }) => {
	const user = locals.user;
	if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
		error(403, 'No tienes permisos para acceder a productos');
	}

	const page = parseInt(url.searchParams.get('page') ?? '1', 10);
	const search = url.searchParams.get('search') ?? '';
	const pageSize = 20;

	try {
		const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/inventory/products?page=${page}&size=${pageSize}&search=${encodeURIComponent(search)}`);

		if (!res.ok) {
			if (res.status === 403) error(403, 'Sin permisos');
			if (res.status === 500) error(503, 'Servicio no disponible');
			error(res.status, res.statusText);
		}

		const body = await res.json();
		return {
			products: body.items.map(mapToProductListItem),
			total: body.total,
			page: body.page,
			search
		};
	} catch {
		// Fallback a mock data
		const filtered = searchMock(MOCK_PRODUCTS, search, ['name', 'sku', 'categoryName']);
		const paged = paginateMock(filtered, page, pageSize);
		return {
			products: paged.items.map((p) => ({
				id: p.id,
				name: p.name,
				sku: p.sku,
				categoryName: p.categoryName,
				price: p.price,
				stock: p.stock,
				stockMin: p.stockMin,
				trend: (p.stock < p.stockMin ? 'down' : 'stable') as 'up' | 'down' | 'stable'
			})),
			total: paged.total,
			page: paged.page,
			search
		};
	}
};

function mapToProductListItem(p: any): ProductListItem {
	return {
		id: p.id,
		name: p.name,
		sku: p.sku,
		categoryName: p.categoryName,
		price: p.price,
		stock: p.stock,
		stockMin: p.stockMin,
		trend: p.trend ?? 'stable'
	};
}
