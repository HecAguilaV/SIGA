import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';

export const load: PageServerLoad = async (event) => {
	const { fetch, url, locals } = event;
	const user = locals.user;
	
	if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
		error(403, 'No tienes permisos para acceder a categorías');
	}

	const page = parseInt(url.searchParams.get('page') ?? '1', 10);
	const search = url.searchParams.get('search') ?? '';
	const pageSize = 20;

	try {
		const res = await fetchWithAuth(fetch, event, `/api/inventory/categories?page=${page - 1}&size=${pageSize}`);

		if (!res.ok) {
			error(res.status, 'Servicio de categorías no disponible');
		}

		let categories = await res.json();

		if (search) {
			const query = search.toLowerCase();
			categories = categories.filter((c: any) => c.name?.toLowerCase().includes(query));
		}

		return {
			categories: categories.map((c: any) => ({
				id: c.id,
				name: c.name,
				productCount: c.productCount || 0,
				active: c.active ?? true
			})),
			total: categories.length,
			page,
			search
		};
	} catch (err) {
		console.error('[Categories Load] Error:', err);
		if ((err as any).status) throw err;
		error(503, 'Error al conectar con el servidor de inventario');
	}
};
