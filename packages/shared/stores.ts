export interface Store {
	id: string;
	name: string;
	address?: string;
	phone?: string;
	email?: string;
	active: boolean;
	createdAt: string;
	updatedAt: string;
}

export interface StoreListItem {
	id: string;
	name: string;
	address: string;
	productCount: number;
	active: boolean;
}

export interface StoreFormData {
	name: string;
	address?: string;
	phone?: string;
	email?: string;
	active: boolean;
}
