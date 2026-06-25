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
 * Roles con acceso total — pueden hacer TODO sin verificación de permisos.
 * Por ahora solo OWNER (dueño de la plataforma / dueño de tenant).
 */
export const ADMIN_ROLES = ['OWNER', 'ADMINISTRATOR'];

/** Roles que pueden VER páginas de lectura (lectura + listado). */
export const VIEWER_ROLES = ['OWNER', 'ADMINISTRATOR', 'OPERATOR'];

/** Roles que pueden CREAR/EDITAR/ELIMINAR. */
export const MANAGER_ROLES = ['OWNER', 'ADMINISTRATOR'];

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

/**
 * canAccessByRole — Verifica acceso basado puramente en el rol del usuario.
 * Útil para guards en +page.server.ts donde no se evalúan permisos individuales.
 */
export function canAccessByRole(rol: string | undefined, allowedRoles: string[]): boolean {
	return !!rol && allowedRoles.includes(rol);
}

/**
 * canAccess — Verifica si un rol puede acceder a una sección.
 * Los roles ADMIN pueden pasar sin verificar permisos individuales.
 * Para roles operativos, verifica contra la lista de permisos del usuario.
 */
export function canAccess(
	rol: string | undefined,
	userPermissions: string[] | undefined,
	requiredPermission: string
): boolean {
	// Roles administrativos: acceso total
	if (rol && ADMIN_ROLES.includes(rol)) {
		return true;
	}
	// Roles operativos: verificar permiso específico
	return hasPermission(userPermissions, requiredPermission);
}
