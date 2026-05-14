import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import CrudTable from '../../../../src/lib/components/crud/CrudTable.svelte';
import type { ColumnDef, ActionDef } from '../../../../src/lib/components/crud/CrudTable.svelte';

describe('CrudTable', () => {
	const columns: ColumnDef<any>[] = [
		{ key: 'name', label: 'Nombre', sortable: true },
		{ key: 'email', label: 'Email' }
	];

	const data = [
		{ id: '1', name: 'Juan', email: 'juan@test.com' },
		{ id: '2', name: 'María', email: 'maria@test.com' }
	];

	it('renders column headers', () => {
		const { container } = render(CrudTable, {
			props: { columns, data, total: 2, page: 1, pageSize: 20 }
		});
		expect(container.querySelector('table')).toBeTruthy();
		expect(container.textContent).toContain('Nombre');
		expect(container.textContent).toContain('Email');
	});

	it('renders data rows', () => {
		const { container } = render(CrudTable, {
			props: { columns, data, total: 2, page: 1, pageSize: 20 }
		});
		expect(container.textContent).toContain('Juan');
		expect(container.textContent).toContain('María');
	});

	it('shows pagination info', () => {
		const { container } = render(CrudTable, {
			props: { columns, data, total: 50, page: 1, pageSize: 20 }
		});
		expect(container.textContent).toContain('1-20');
		expect(container.textContent).toContain('50');
	});

	it('shows pagination page indicator', () => {
		const { container } = render(CrudTable, {
			props: { columns, data, total: 50, page: 2, pageSize: 20 }
		});
		expect(container.textContent).toContain('2 / 3');
	});

	it('shows empty state when no data', () => {
		const { container } = render(CrudTable, {
			props: { columns, data: [], total: 0, page: 1, pageSize: 20 }
		});
		expect(container.textContent).toContain('Sin datos disponibles');
	});

	it('renders default empty state content', () => {
		const { container } = render(CrudTable, {
			props: {
				columns,
				data: [],
				total: 0,
				page: 1,
				pageSize: 20
			}
		});
		expect(container.textContent).toContain('Sin datos disponibles');
	});

	it('disables prev button on first page', () => {
		const { container } = render(CrudTable, {
			props: { columns, data, total: 50, page: 1, pageSize: 20 }
		});
		const prevBtn = container.querySelector('[aria-label="Página anterior"]');
		expect(prevBtn).toBeDisabled();
	});

	it('disables next button on last page', () => {
		const { container } = render(CrudTable, {
			props: { columns, data, total: 20, page: 1, pageSize: 20 }
		});
		const nextBtn = container.querySelector('[aria-label="Página siguiente"]');
		expect(nextBtn).toBeDisabled();
	});
});
