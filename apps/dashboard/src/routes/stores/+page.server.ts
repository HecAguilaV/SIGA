import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { MOCK_STORES, paginateMock, searchMock } from '$lib/server/mock-data';

export const load: PageServerLoad = async (event) => {
	const { fetch, url, locals } = event;
	const user = locals.user;
	if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
		error(403, 'No tienes permisos para acceder a locales');
	}

	const page = parseInt(url.searchParams.get('page') ?? '1', 10);
	const search = url.searchParams.get('search') ?? '';
	const pageSize = 20;

	try {
		const res = await fetchWithAuth(fetch, event, `/api/inventory/stores?page=${page - 1}&size=${pageSize}&search=${encodeURIComponent(search)}`);

		if (!res.ok) {
			error(res.status, 'Servicio de locales no disponible');
		}

		const stores = await res.json();
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
		error(503, 'Error al conectar con el servidor de locales');
	}
};
