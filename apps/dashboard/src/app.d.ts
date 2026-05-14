import type { UserSession } from '$lib/types/auth';

declare global {
	namespace App {
		interface Locals {
			user: UserSession | null;
		}
		interface PageData {
			[key: string]: unknown;
		}
		interface Platform {
			env?: Record<string, string>;
		}
	}
}

export {};
