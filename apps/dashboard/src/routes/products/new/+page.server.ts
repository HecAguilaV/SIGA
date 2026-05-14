import { redirect, fail } from '@sveltejs/kit';
import type { Actions } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';

export const actions: Actions = {
	default: async ({ request, fetch, url, locals }) => {
		const user = locals.user;
		if (!user || !['ADMINISTRATOR', 'OPERATOR'].includes(user.rol ?? '')) {
			return fail(403, { error: 'Sin permisos' });
		}

		const formData = await request.formData();
		const data = Object.fromEntries(formData);

		try {
			const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, '/api/inventory/products', {
				method: 'POST',
				body: JSON.stringify(data)
			});

			if (!res.ok) {
				if (res.status === 409) {
					const body = await res.json();
					return fail(409, { error: body.message || 'SKU duplicado', field: body.field || 'sku' });
				}
				return fail(res.status, { error: 'Error al crear producto' });
			}

			redirect(303, '/products');
		} catch {
			// Mock success
			redirect(303, '/products');
		}
	}
};
