export interface Sale {
	id: string;
	storeId: string;
	storeName?: string;
	total: number;
	items: SaleItem[];
	paymentMethod: string;
	status: 'completed' | 'cancelled' | 'refunded';
	createdAt: string;
	userId?: string;
}

export interface SaleItem {
	productId: string;
	productName: string;
	quantity: number;
	unitPrice: number;
	total: number;
}

export interface SaleSummary {
	totalSales: number;
	totalRevenue: number;
	averageTicket: number;
	salesCount: number;
	periodStart: string;
	periodEnd: string;
}
