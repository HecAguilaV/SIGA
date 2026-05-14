import { error, redirect, fail } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { MOCK_USERS } from '$lib/server/mock-data';

export const load: PageServerLoad = async ({ params, fetch, url, locals }) => {
	const user = locals.user;
	if (!user || user.rol !== 'ADMINISTRATOR') {
		error(403, 'No tienes permisos');
	}

	try {
		const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/auth/users/${params.id}`);
		if (!res.ok) {
			if (res.status === 404) error(404, 'Usuario no encontrado');
			error(res.status, res.statusText);
		}
		return { usr: await res.json() };
	} catch {
		const usr = MOCK_USERS.find((u) => u.id === params.id);
		if (!usr) error(404, 'Usuario no encontrado');
		return { usr };
	}
};

export const actions: Actions = {
	default: async ({ params, request, fetch, url, locals }) => {
		const user = locals.user;
		if (!user || user.rol !== 'ADMINISTRATOR') {
			return fail(403, { error: 'Sin permisos' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/auth/users/${params.id}`, {
				method: 'PUT',
				body: JSON.stringify(data)
			});
			if (!res.ok) return fail(res.status, { error: 'Error al actualizar' });
			redirect(303, '/users');
		} catch {
			redirect(303, '/users');
		}
	}
};
