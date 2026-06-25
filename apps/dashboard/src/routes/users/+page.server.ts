import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';
import { canAccessByRole, MANAGER_ROLES } from '$lib/auth/permissions';

export const load: PageServerLoad = async (event) => {
	const { fetch, url, locals } = event;
	const user = locals.user;

	if (!user || !canAccessByRole(user.rol, MANAGER_ROLES)) {
		error(403, 'No tienes permisos para acceder a usuarios');
	}

	const search = url.searchParams.get('search') ?? '';

	try {
		// Gateway rewrite: /api/auth/users → /api/v1/auth/users
		const res = await fetchWithAuth(fetch, event, `/api/auth/users`);

		if (!res.ok) {
			if (res.status === 403) error(403, 'Sin permisos');
			error(res.status, 'Error al obtener usuarios');
		}

		let users = await res.json();

		// Filtrado básico en cliente ya que el endpoint actual de auth no soporta search aún
		if (search) {
			const query = search.toLowerCase();
			users = users.filter((u: any) => 
				u.name?.toLowerCase().includes(query) || 
				u.email?.toLowerCase().includes(query) || 
				u.rol?.toLowerCase().includes(query)
			);
		}

		return {
			users: users.map((u: any) => ({
				id: u.id,
				email: u.email,
				name: u.name,
				rol: u.rol,
				active: u.active ?? true
			})),
			total: users.length,
			search
		};
	} catch (err) {
		console.error('[Users Load] Error:', err);
		if ((err as any).status) throw err;
		error(503, 'Error de conexión con el servicio de autenticación');
	}
};
