export interface Product {
	id: string;
	name: string;
	sku: string;
	description?: string;
	categoryId: string;
	categoryName?: string;
	price: number;
	stock: number;
	stockMin: number;
	storeId?: string;
	storeName?: string;
	imageUrl?: string;
	active: boolean;
	createdAt: string;
	updatedAt: string;
}

export interface ProductListItem {
	id: string;
	name: string;
	sku: string;
	categoryName: string;
	price: number;
	stock: number;
	stockMin: number;
	trend?: 'up' | 'down' | 'stable';
}

export interface ProductDetail extends Product {
	trend?: 'up' | 'down' | 'stable';
}

export interface Category {
	id: string;
	name: string;
	description?: string;
	productCount?: number;
	active: boolean;
}

export interface CategoryListItem {
	id: string;
	name: string;
	productCount: number;
}

export interface Page<T> {
	items: T[];
	total: number;
	page: number;
	pageSize: number;
	totalPages: number;
}

export interface ProductFormData {
	name: string;
	sku: string;
	description?: string;
	categoryId: string;
	price: number;
	stock: number;
	stockMin: number;
	storeId?: string;
}
