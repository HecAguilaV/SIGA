import { error } from '@sveltejs/kit';
import type { LayoutServerLoad } from './$types';

/**
 * Role-based route access map.
 * Each route prefix restricts to specific roles.
 * If a route is not listed, all authenticated users can access.
 */
const ROLE_GUARDS: Record<string, string[]> = {
	'/products': ['ADMINISTRATOR', 'OPERATOR'],
	'/stores': ['ADMINISTRATOR', 'OPERATOR'],
	'/categories': ['ADMINISTRATOR', 'OPERATOR'],
	'/users': ['ADMINISTRATOR'],
	'/analytics': ['ADMINISTRATOR', 'OPERATOR'],
	'/pos': ['CASHIER']
};

export const load: LayoutServerLoad = async ({ locals, url }) => {
	const user = locals.user;

	if (!user) {
		// Should not happen since hooks.server.ts redirects, but guard anyway
		return {
			user: null,
			pathname: url.pathname
		};
	}

	// Role-based access control
	const pathname = url.pathname;
	for (const [prefix, allowedRoles] of Object.entries(ROLE_GUARDS)) {
		if (pathname === prefix || pathname.startsWith(prefix + '/')) {
			if (!allowedRoles.includes(user.rol ?? '')) {
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
