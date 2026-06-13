import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import type { ProductListItem } from '$lib/types/inventory';

export const load: PageServerLoad = async (event) => {
	const { fetch, url, locals } = event;
	const user = locals.user;
	
	if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
		error(403, 'No tienes permisos para acceder a productos');
	}

	const page = parseInt(url.searchParams.get('page') ?? '1', 10);
	const search = url.searchParams.get('search') ?? '';
	const pageSize = 20;

	// Sincronizar parámetros con el backend Kotlin (Spring usa 0-based page)
	const queryParams = new URLSearchParams({
		page: (page - 1).toString(),
		size: pageSize.toString(),
		query: search
	});

	try {
		const res = await fetchWithAuth(fetch, event, `/api/inventory/products?${queryParams.toString()}`);

		if (!res.ok) {
			console.error(`[Inventory API] Status ${res.status}: ${res.statusText}`);
			error(res.status === 403 ? 403 : 503, 'Servicio de inventario no disponible');
		}

		const body = await res.json();
		
		// Adaptar respuesta de Spring (Page)
		const products = body.content || body.items || [];
		const total = body.totalElements || body.total || 0;

		return {
			products: products.map(mapToProductListItem),
			total,
			page,
			search
		};
	} catch (err) {
		if (err instanceof Response || (err as any).status) throw err;
		console.error('[Inventory Load] Unexpected error:', err);
		error(503, 'Error al conectar con el servidor de inventario');
	}
};

function mapToProductListItem(p: any): ProductListItem {
	return {
		id: p.id || p.productId,
		name: p.name || p.productName,
		sku: p.sku,
		categoryName: p.categoryName || 'General',
		price: p.price || 0,
		stock: p.stock ?? p.totalStock ?? 0,
		stockMin: p.stockMin ?? p.minStock ?? 10,
		trend: p.trend ?? ( (p.stock < p.stockMin) ? 'down' : 'stable')
	};
}
