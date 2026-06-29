import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import type { ProductListItem } from '$lib/types/inventory';
import { canAccessByRole, VIEWER_ROLES } from '$lib/auth/permissions';

export const load: PageServerLoad = async (event) => {
	const { fetch, url, locals } = event;
	const user = locals.user;
	
	if (!user || !canAccessByRole(user.rol, VIEWER_ROLES)) {
		error(403, 'No tienes permisos para acceder a productos');
	}

	const page = parseInt(url.searchParams.get('page') ?? '1', 10);
	const search = url.searchParams.get('search') ?? '';
	const pageSize = 20;

	// Nota: El backend getProducts() devuelve todos los productos sin paginación.
	// El filtrado se hace client-side por ahora.
	try {
		const res = await fetchWithAuth(fetch, event, '/api/inventory/products?size=100');

		if (!res.ok) {
			console.error(`[Inventory API] Status ${res.status}: ${res.statusText}`);
			error(res.status === 403 ? 403 : 503, 'Servicio de inventario no disponible');
		}

		const body = await res.json();
		
		// Adaptar respuesta: array plano (List<Product>) o Spring Page ({content:[...]})
		let products = Array.isArray(body) ? body : (body.content || body.items || []);

		// Filtro client-side por búsqueda
		if (search) {
			const q = search.toLowerCase();
			products = products.filter((p: any) =>
				p.name?.toLowerCase().includes(q) ||
				p.sku?.toLowerCase().includes(q) ||
				p.barcode?.toLowerCase().includes(q)
			);
		}

		// Paginación client-side
		const start = (page - 1) * pageSize;
		const paginated = products.slice(start, start + pageSize);

		const total = products.length;

		return {
			products: paginated.map(mapToProductListItem),
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
		sku: p.sku || '',
		categoryName: p.categoryName || 'General',
		price: p.unitPrice ?? p.price ?? 0,
		stock: p.stock ?? p.totalStock ?? 0,
		stockMin: p.stockMin ?? p.minStock ?? 10,
		trend: p.trend ?? 'stable'
	};
}
