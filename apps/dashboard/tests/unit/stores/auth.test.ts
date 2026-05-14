import { describe, it, expect, beforeEach } from 'vitest';
import { get } from 'svelte/store';
import { user, isAuthenticated, userRole, userPrincipalType } from '../../../src/lib/stores/auth.svelte';
import type { UserSession } from '../../../src/lib/types/auth';

describe('Auth Store', () => {
	beforeEach(() => {
		user.set(null);
	});

	const mockCustomer: UserSession = {
		id: 'cust-1',
		email: 'cliente@demo.com',
		name: 'Cliente Demo',
		principalType: 'customer',
		tenantId: 'tenant-1'
	};

	const mockAdmin: UserSession = {
		id: 'admin-1',
		email: 'admin@siga.com',
		name: 'Admin SIGA',
		principalType: 'user',
		rol: 'admin',
		tenantId: 'tenant-1'
	};

	it('starts with null user', () => {
		expect(get(user)).toBeNull();
	});

	it('isAuthenticated is false when no user', () => {
		expect(get(isAuthenticated)).toBe(false);
	});

	it('login sets the user', () => {
		user.login(mockCustomer);
		expect(get(user)).toEqual(mockCustomer);
	});

	it('login makes isAuthenticated true', () => {
		user.login(mockCustomer);
		expect(get(isAuthenticated)).toBe(true);
	});

	it('logout clears the user', () => {
		user.login(mockCustomer);
		user.logout();
		expect(get(user)).toBeNull();
		expect(get(isAuthenticated)).toBe(false);
	});

	it('can set user with set method', () => {
		user.set(mockAdmin);
		expect(get(user)).toEqual(mockAdmin);
	});

	it('userRole derived store returns role for user type', () => {
		user.login(mockAdmin);
		expect(get(userRole)).toBe('admin');
	});

	it('userRole derived store returns null for customer type', () => {
		user.login(mockCustomer);
		expect(get(userRole)).toBeNull();
	});

	it('userPrincipalType derived store returns correct type', () => {
		user.login(mockCustomer);
		expect(get(userPrincipalType)).toBe('customer');

		user.login(mockAdmin);
		expect(get(userPrincipalType)).toBe('user');
	});

	it('login replaces existing user', () => {
		user.login(mockCustomer);
		user.login(mockAdmin);
		expect(get(user)).toEqual(mockAdmin);
	});
});
