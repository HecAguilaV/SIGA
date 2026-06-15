import { writable, derived } from 'svelte/store';
import type { UserSession } from '$lib/types/auth';
import { hasPermission } from '$lib/auth/permissions';

function createAuthStore() {
	const { subscribe, set, update } = writable<UserSession | null>(null);

	return {
		subscribe,
		set(user: UserSession | null) {
			set(user);
		},
		login(user: UserSession) {
			set(user);
		},
		logout() {
			set(null);
		},
		updateUser(updater: (user: UserSession | null) => UserSession | null) {
			update(updater);
		}
	};
}

export const user = createAuthStore();

export const isAuthenticated = derived(user, ($user) => $user !== null);

export const userRole = derived(user, ($user) => $user?.rol ?? null);

export const userPrincipalType = derived(user, ($user) => $user?.principalType ?? null);

export const userPermissions = derived(user, ($user) => $user?.permissions ?? []);

/**
 * needsOnboarding — Detecta si el usuario tiene un nombre placeholder
 * (derivado del email) y necesita completar su perfil en la página de onboarding.
 * 
 * Un nombre es placeholder cuando coincide con el prefijo del email
 * (ej: email "juan@example.com" → name "juan").
 */
export const needsOnboarding = derived(user, ($user) => {
	if (!$user?.email || !$user?.name) return false;
	const emailPrefix = $user.email.split('@')[0];
	return $user.name === emailPrefix;
});

/**
 * Helper para verificar permisos de forma reactiva en componentes.
 * Uso: const canEdit = canAccess('inventory:edit'); if ($canEdit) { ... }
 */
export const canAccess = (requiredPermission: string) => 
	derived(userPermissions, ($permissions) => hasPermission($permissions, requiredPermission));
