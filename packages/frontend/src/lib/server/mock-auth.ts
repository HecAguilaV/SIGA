import type { LoginResponse, UserSession } from '$lib/types/auth';

/**
 * Mock auth helpers — permiten probar el flujo completo sin backend.
 * Se activan cuando el gateway no está disponible.
 */

// Preferencia: si está en modo mock
let mockMode = false;

export function isMockMode(): boolean {
	return mockMode;
}

export function setMockMode(enabled: boolean): void {
	mockMode = enabled;
}

// Mock user data
const MOCK_CUSTOMER: UserSession = {
	id: 'mock-cust-001',
	email: 'cliente@demo.com',
	name: 'Cliente Demo',
	principalType: 'customer',
	tenantId: 'tenant-001'
};

const MOCK_USER: UserSession = {
	id: 'mock-user-001',
	email: 'admin@siga.com',
	name: 'Admin SIGA',
	principalType: 'user',
	rol: 'admin',
	tenantId: 'tenant-001'
};

const MOCK_CREDENTIALS: Record<string, { password: string; session: UserSession }> = {
	'cliente@demo.com': {
		password: 'demo1234',
		session: MOCK_CUSTOMER
	},
	'admin@siga.com': {
		password: 'admin1234',
		session: MOCK_USER
	}
};

export function mockLogin(
	email: string,
	password: string
): { data?: LoginResponse; error?: string } {
	const user = MOCK_CREDENTIALS[email.toLowerCase()];
	if (!user || user.password !== password) {
		return { error: 'Credenciales inválidas' };
	}

	return {
		data: {
			accessToken: createMockToken(user.session, '15m'),
			refreshToken: createMockToken(user.session, '7d'),
			principalType: user.session.principalType,
			user: user.session
		}
	};
}

function createMockToken(session: UserSession, expiresIn: string): string {
	const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
	const now = Math.floor(Date.now() / 1000);
	const expMap: Record<string, number> = {
		'15m': now + 15 * 60,
		'7d': now + 7 * 24 * 60 * 60
	};
	const payload = btoa(
		JSON.stringify({
			sub: session.id,
			email: session.email,
			name: session.name,
			principalType: session.principalType,
			rol: session.rol,
			tenantId: session.tenantId,
			iat: now,
			exp: expMap[expiresIn] || now + 15 * 60
		})
	);
	const signature = btoa('mock-signature');
	return `${header}.${payload}.${signature}`;
}
