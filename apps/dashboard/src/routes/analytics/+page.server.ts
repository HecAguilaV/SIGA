import type { PageServerLoad } from './$types';
import { fetchWithAuth } from '$lib/server/gateway';

interface TrendPoint {
	date: string;
	value: number;
}

interface InsightFinding {
	id: string;
	title: string;
	description: string;
	type: 'positive' | 'info' | 'warning' | 'danger';
	context: string;
}

interface AnalyticsData {
	trends: TrendPoint[];
	insights: InsightFinding[];
	anomalies: Array<{
		id: string;
		type: string;
		message: string;
		severity: string;
		timestamp: string;
	}>;
	summary: string;
}

export const load: PageServerLoad = async (event) => {
	const { fetch, locals } = event;
	const user = locals.user;
	
	let analytics: AnalyticsData = {
		trends: [],
		insights: [],
		anomalies: [],
		summary: 'No se encontraron datos de análisis.'
	};
	let error: string | null = null;

	try {
		const res = await fetchWithAuth(fetch, event, '/api/v1/dashboard/insights?deep=true');

		if (res.ok) {
			const body = await res.json();
			analytics = {
				trends: body.trends ?? [],
				insights: body.insights ?? [],
				anomalies: body.anomalies ?? [],
				summary: body.summary ?? ''
			};
		} else {
			console.warn('[Analytics Load] BFF returned error status:', res.status);
			error = 'No se pudo conectar con el motor de análisis';
		}
	} catch (e) {
		console.error('[Analytics Load] Exception:', e);
		error = 'Error de red con el servicio de análisis';
	}

	return {
		user,
		analytics,
		error,
		timestamp: Date.now()
	};
};
