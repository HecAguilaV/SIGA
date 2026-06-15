import { error } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';
import { PERMISSION_GUARDS, hasPermission } from '$lib/auth/permissions';

export const load: LayoutServerLoad = async ({ locals, url }) => {
	const user = locals.user;

	if (!user) {
		// Should not happen since hooks.server.ts redirects, but guard anyway
		return {
			user: null,
			pathname: url.pathname
		};
	}

	// Permission-based access control
	const pathname = url.pathname;
	const userPermissions = user.permissions;

	for (const [prefix, requiredPermission] of Object.entries(PERMISSION_GUARDS)) {
		if (pathname === prefix || pathname.startsWith(prefix + '/')) {
			if (!hasPermission(userPermissions, requiredPermission)) {
				error(403, 'No tienes permisos para acceder a esta sección');
			}
			break;
		}
	}

	return {
		user,
		pathname
	};
};
