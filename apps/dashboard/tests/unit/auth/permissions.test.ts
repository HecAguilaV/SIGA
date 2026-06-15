import { describe, it, expect } from 'vitest';
import { hasPermission } from '../../../src/lib/auth/permissions';

describe('hasPermission', () => {
	it('should return true if user has the exact permission', () => {
		const permissions = ['dashboard:view', 'inventory:edit'];
		expect(hasPermission(permissions, 'dashboard:view')).toBe(true);
	});

	it('should return false if user does not have the permission', () => {
		const permissions = ['dashboard:view'];
		expect(hasPermission(permissions, 'inventory:edit')).toBe(false);
	});

	it('should return true if user has the wildcard permission', () => {
		const permissions = ['*'];
		expect(hasPermission(permissions, 'any:permission')).toBe(true);
	});

	it('should return true if user has a prefix wildcard permission', () => {
		const permissions = ['inventory:*'];
		expect(hasPermission(permissions, 'inventory:view')).toBe(true);
		expect(hasPermission(permissions, 'inventory:edit')).toBe(true);
	});

	it('should return false if prefix wildcard does not match', () => {
		const permissions = ['inventory:*'];
		expect(hasPermission(permissions, 'dashboard:view')).toBe(false);
	});

	it('should handle undefined or empty permissions', () => {
		expect(hasPermission(undefined, 'dashboard:view')).toBe(false);
		expect(hasPermission([], 'dashboard:view')).toBe(false);
	});
});
