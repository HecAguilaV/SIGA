import { describe, it, expect, vi } from 'vitest';
import { hasPermission } from '../../../src/lib/auth/permissions';

// Mocking SvelteKit error function
const mockError = vi.fn();

describe('Route Guards Logic', () => {
	const PERMISSION_GUARDS: Record<string, string> = {
		'/users': 'admin:view',
		'/analytics': 'analytics:view'
	};

	function checkGuard(pathname: string, userPermissions: string[] | undefined) {
		for (const [prefix, requiredPermission] of Object.entries(PERMISSION_GUARDS)) {
			if (pathname === prefix || pathname.startsWith(prefix + '/')) {
				if (!hasPermission(userPermissions, requiredPermission)) {
					mockError(403, 'No tienes permisos para acceder a esta sección');
				}
				return;
			}
		}
	}

	it('should allow access if user has permission', () => {
		mockError.mockClear();
		checkGuard('/users', ['admin:view']);
		expect(mockError).not.toHaveBeenCalled();
	});

	it('should deny access if user lacks permission', () => {
		mockError.mockClear();
		checkGuard('/users', ['analytics:view']);
		expect(mockError).toHaveBeenCalledWith(403, expect.any(String));
	});

	it('should allow access with wildcard', () => {
		mockError.mockClear();
		checkGuard('/analytics/reports', ['*']);
		expect(mockError).not.toHaveBeenCalled();
	});

	it('should allow access with prefix wildcard', () => {
		mockError.mockClear();
		checkGuard('/analytics/predictive', ['analytics:*']);
		expect(mockError).not.toHaveBeenCalled();
	});
});
