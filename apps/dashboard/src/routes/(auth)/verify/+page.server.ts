import { redirect } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';
import { env } from '$env/dynamic/private';

const GATEWAY_BASE = env.GATEWAY_BASE_URL || 'http://localhost:8080';

export const load: PageServerLoad = async ({ url, fetch }) => {
	const token = url.searchParams.get('token');

	if (!token) {
		return { status: 'error', message: 'Token de verificación no encontrado.' };
	}

	try {
		const res = await fetch(`${GATEWAY_BASE}/api/auth/verify?token=${encodeURIComponent(token)}`);

		if (res.ok) {
			throw redirect(303, '/login?verified=true');
		}

		if (res.status === 404) {
			return { status: 'error', message: 'Token de verificación inválido o ya utilizado.' };
		}

		if (res.status === 410) {
			return { status: 'error', message: 'El token de verificación ha expirado. Solicita un nuevo registro.' };
		}

		return { status: 'error', message: 'Error al verificar la cuenta. Intenta nuevamente.' };
	} catch (e) {
		if (e?.constructor?.name === 'Redirect') throw e;
		return { status: 'error', message: 'Error de conexión con el servidor.' };
	}
};
