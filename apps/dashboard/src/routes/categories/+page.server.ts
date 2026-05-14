import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { MOCK_CATEGORIES, paginateMock, searchMock } from '$lib/server/mock-data';

export const load: PageServerLoad = async ({ fetch, url, locals }) => {
	const user = locals.user;
	if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
		error(403, 'No tienes permisos para acceder a categorías');
	}

	const page = parseInt(url.searchParams.get('page') ?? '1', 10);
	const search = url.searchParams.get('search') ?? '';
	const pageSize = 20;

	try {
		const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/inventory/categories?page=${page}&size=${pageSize}&search=${encodeURIComponent(search)}`);

		if (!res.ok) {
			if (res.status === 403) error(403, 'Sin permisos');
			if (res.status === 500) error(503, 'Servicio no disponible');
			error(res.status, res.statusText);
		}

		const body = await res.json();
		return {
			categories: body.items,
			total: body.total,
			page: body.page,
			search
		};
	} catch {
		const filtered = searchMock(MOCK_CATEGORIES, search, ['name', 'description']);
		const paged = paginateMock(filtered, page, pageSize);
		return {
			categories: paged.items.map((c) => ({
				id: c.id,
				name: c.name,
				productCount: c.productCount || 0,
				active: c.active
			})),
			total: paged.total,
			page: paged.page,
			search
		};
	}
};
