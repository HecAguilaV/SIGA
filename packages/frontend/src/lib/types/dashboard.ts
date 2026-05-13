export interface Insight {
	id: string;
	title: string;
	value: string | number;
	unit?: string;
	icon?: string;
	trend?: TrendDirection;
	trendValue?: number;
	variant?: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

export type TrendDirection = 'up' | 'down' | 'stable';

export interface KpiCard {
	title: string;
	value: string | number;
	trend: TrendDirection;
	trendValue: number;
	icon: string;
	variant: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

export interface Anomaly {
	id: string;
	type: string;
	message: string;
	severity: 'low' | 'medium' | 'high' | 'critical';
	timestamp: string;
	resource?: string;
	resourceId?: string;
}

export interface TrendData {
	date: string;
	value: number;
	label?: string;
}
