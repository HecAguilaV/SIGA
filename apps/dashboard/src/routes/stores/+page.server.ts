import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { canAccessByRole, VIEWER_ROLES } from '$lib/auth/permissions';

export const load: PageServerLoad = async (event) => {
	const { fetch, url, locals } = event;
	const user = locals.user;
	
	if (!user || !canAccessByRole(user.rol, VIEWER_ROLES)) {
		error(403, 'No tienes permisos para acceder a locales');
	}

	const page = parseInt(url.searchParams.get('page') ?? '1', 10);
	const search = url.searchParams.get('search') ?? '';
	const pageSize = 20;

	try {
		const res = await fetchWithAuth(fetch, event, `/api/inventory/stores?page=${page - 1}&size=${pageSize}`);

		if (!res.ok) {
			error(res.status, 'Servicio de locales no disponible');
		}

		let stores = await res.json();

		if (search) {
			const query = search.toLowerCase();
			stores = stores.filter((s: any) => 
				s.name?.toLowerCase().includes(query) || 
				s.address?.toLowerCase().includes(query)
			);
		}

		return {
			stores: stores.map((s: any) => ({
				id: s.id,
				name: s.name,
				address: s.address || '',
				productCount: s.productCount || 0,
				active: s.active ?? true
			})),
			total: stores.length,
			page,
			search
		};
	} catch (err) {
		console.error('[Stores Load] Error:', err);
		if ((err as any).status) throw err;
		error(503, 'Error al conectar con el servidor de locales');
	}
};
