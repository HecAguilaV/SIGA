export type PrincipalType = 'customer' | 'user';

export interface LoginRequest {
	email: string;
	password: string;
}

export interface LoginResponse {
	accessToken: string;
	refreshToken: string;
	principalType: PrincipalType;
	user: UserSession;
}

export interface UserSession {
	id: string;
	email: string;
	name: string;
	principalType: PrincipalType;
	rol?: string;
	tenantId?: string;
	avatar?: string;
}

export interface RefreshResponse {
	accessToken: string;
	refreshToken: string;
}

export interface ApiError {
	status: number;
	message: string;
	code?: string;
}
