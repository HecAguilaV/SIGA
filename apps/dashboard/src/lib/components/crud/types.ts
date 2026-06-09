export interface ColumnDef<T> {
	key: string;
	label: string;
	sortable?: boolean;
	render?: (item: T) => string;
	class?: string;
}

export interface ActionDef<T = any> {
	label: string;
	variant?: 'primary' | 'secondary' | 'ghost' | 'danger';
	icon?: string;
	onClick: (item: T) => void;
}

export interface FieldDef<T> {
	key: string;
	label: string;
	type: 'text' | 'number' | 'email' | 'select' | 'textarea' | 'password';
	options?: { value: string; label: string }[];
	required?: boolean;
	placeholder?: string;
	validate?: (value: string) => string | undefined;
}
