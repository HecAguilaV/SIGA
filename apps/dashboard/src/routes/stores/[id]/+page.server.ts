import { error, redirect, fail } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { MOCK_STORES } from '$lib/server/mock-data';

export const load: PageServerLoad = async ({ params, fetch, url, locals }) => {
	const user = locals.user;
	if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
		error(403, 'No tienes permisos');
	}

	try {
		const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/stores/${params.id}`);
		if (!res.ok) {
			if (res.status === 404) error(404, 'Local no encontrado');
			error(res.status, res.statusText);
		}
		return { store: await res.json() };
	} catch {
		const store = MOCK_STORES.find((s) => s.id === params.id);
		if (!store) error(404, 'Local no encontrado');
		return { store };
	}
};

export const actions: Actions = {
	default: async ({ params, request, fetch, url, locals }) => {
		const user = locals.user;
		if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
			return fail(403, { error: 'Sin permisos' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, `/api/stores/${params.id}`, {
				method: 'PUT',
				body: JSON.stringify(data)
			});
			if (!res.ok) return fail(res.status, { error: 'Error al actualizar' });
			redirect(303, '/stores');
		} catch {
			redirect(303, '/stores');
		}
	}
};
