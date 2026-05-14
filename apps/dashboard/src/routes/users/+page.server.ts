import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { MOCK_USERS, paginateMock, searchMock } from '$lib/server/mock-data';

export const load: PageServerLoad = async ({ fetch, url, locals }) => {
	const user = locals.user;
	if (!user || user.rol !== 'ADMINISTRATOR') {
		error(403, 'No tienes permisos para acceder a usuarios');
	}

	const page = parseInt(url.searchParams.get('page') ?? '1', 10);
	const search = url.searchParams.get('search') ?? '';
	const pageSize = 20;
	const tenantId = user.tenantId || '';

	try {
		const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/auth/users?page=${page}&size=${pageSize}&search=${encodeURIComponent(search)}&tenant=${tenantId}`);

		if (!res.ok) {
			if (res.status === 403) error(403, 'Sin permisos');
			if (res.status === 500) error(503, 'Servicio no disponible');
			error(res.status, res.statusText);
		}

		const body = await res.json();
		return {
			users: body.items,
			total: body.total,
			page: body.page,
			search
		};
	} catch {
		// Filter by tenant
		const tenantUsers = MOCK_USERS.filter((u) => u.tenantId === tenantId);
		const filtered = searchMock(tenantUsers, search, ['name', 'email', 'rol']);
		const paged = paginateMock(filtered, page, pageSize);
		return {
			users: paged.items.map((u) => ({
				id: u.id,
				email: u.email,
				name: u.name,
				rol: u.rol,
				active: u.active
			})),
			total: paged.total,
			page: paged.page,
			search
		};
	}
};
