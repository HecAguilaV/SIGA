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

export const load: PageServerLoad = async ({ fetch, url, locals }) => {
	const user = locals.user;
	let analyticsData: AnalyticsData;
	let error: string | null = null;

	try {
		const res = await fetchWithAuth(fetch, { request: {} as Request, cookies: {} as any, url }, '/api/v1/dashboard/insights?deep=true');

		if (res.ok) {
			const body = await res.json();
			analyticsData = {
				trends: body.trends ?? [],
				insights: body.insights ?? [],
				anomalies: body.anomalies ?? [],
				summary: body.summary ?? ''
			};
		} else {
			// Gateway returned error — use fallback
			analyticsData = getMockAnalyticsFallback();
			error = 'Servicio de análisis no disponible — mostrando datos de demostración';
		}
	} catch {
		// Network error — fallback to mock data
		analyticsData = getMockAnalyticsFallback();
		error = 'Servicio de análisis no disponible — mostrando datos de demostración';
	}

	return {
		user,
		analytics: analyticsData,
		error,
		timestamp: Date.now()
	};
};

function getMockAnalyticsFallback(): AnalyticsData {
	const trends: TrendPoint[] = [];
	const now = Date.now();

	for (let i = 6; i >= 0; i--) {
		const d = new Date(now - i * 24 * 60 * 60 * 1000);
		trends.push({
			date: d.toISOString().split('T')[0],
			value: Math.floor(Math.random() * 1000) + 500
		});
	}

	return {
		trends,
		insights: [
			{
				id: 'fallback-1',
				title: 'Análisis no disponible',
				description: 'Los datos analíticos no están disponibles en este momento',
				type: 'info',
				context: 'Usando datos de demostración'
			}
		],
		anomalies: [],
		summary: 'Mostrando datos de demostración — el servicio de análisis no está disponible'
	};
}
