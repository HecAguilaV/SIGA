/**
 * PERMISSION_GUARDS — Mapeo de rutas a permisos requeridos.
 * Reemplaza ROLE_GUARDS.
 */
export const PERMISSION_GUARDS: Record<string, string> = {
	'/products': 'inventory:view',
	'/stores': 'inventory:view',
	'/categories': 'inventory:view',
	'/users': 'admin:view',
	'/analytics': 'analytics:view',
	'/pos': 'pos:view'
};

/**
 * hasPermission — Verifica si una lista de permisos contiene el permiso requerido.
 * Soporta wildcards (*) y prefijos (ej: 'inventory:*').
 */
export function hasPermission(userPermissions: string[] | undefined, requiredPermission: string): boolean {
	if (!userPermissions || userPermissions.length === 0) {
		return false;
	}

	// Wildcard total
	if (userPermissions.includes('*')) {
		return true;
	}

	// Exact match
	if (userPermissions.includes(requiredPermission)) {
		return true;
	}

	// Prefix wildcard (ej: 'inventory:*' permite 'inventory:view')
	return userPermissions.some((p) => {
		if (p.endsWith(':*')) {
			const prefix = p.slice(0, -1); // 'inventory:'
			return requiredPermission.startsWith(prefix);
		}
		return false;
	});
}
