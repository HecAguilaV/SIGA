import { describe, it, expect } from 'vitest';
import { buildUserSession } from '../../../src/lib/server/auth.server';

describe('buildUserSession', () => {
	it('should extract permissions from payload', () => {
		const payload = {
			sub: 'user-123',
			email: 'test@example.com',
			name: 'Test User',
			principalType: 'user',
			rol: 'ADMINISTRATOR',
			permissions: ['dashboard:view', 'inventory:*'],
			tenantId: 'tenant-456'
		};

		const session = buildUserSession(payload);

		expect(session.permissions).toEqual(['dashboard:view', 'inventory:*']);
	});

	it('should handle missing permissions in payload', () => {
		const payload = {
			sub: 'user-123',
			email: 'test@example.com'
		};

		const session = buildUserSession(payload);

		expect(session.permissions).toBeUndefined();
	});
});
