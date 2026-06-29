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
 * PLATFORM_ADMIN — Platform-level SaaS owner.
 * Lives in its own table (auth.platform_admins) and uses principalType='platform_admin'.
 * Has access to platform-wide surfaces (pricing, plans, tests, metrics) but
 * NOT to tenant-scoped resources like /users (which is tenant-owned).
 */
export const PLATFORM_ADMIN_ROLE = 'PLATFORM_ADMIN';

/**
 * Roles with full tenant-scoped access (admin of their tenant).
 * These roles can do everything within their own customerId, no per-permission check.
 * They do NOT have cross-tenant or platform-level access.
 */
export const ADMIN_ROLES = ['OWNER', 'ADMINISTRATOR'];

/** Roles that can VIEW read-only pages (read + listing). */
export const VIEWER_ROLES = ['OWNER', 'ADMINISTRATOR', 'OPERATOR'];

/** Roles that can CREATE/EDIT/DELETE within their tenant. */
export const MANAGER_ROLES = ['OWNER', 'ADMINISTRATOR'];

/**
 * isPlatformAdmin — Helper for guards: detect platform-level principals.
 * Use this in +layout.server.ts to gate /platform/* routes.
 */
export function isPlatformAdmin(rol: string | undefined, principalType: string | undefined): boolean {
	return principalType === 'platform_admin' || rol === PLATFORM_ADMIN_ROLE;
}

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
