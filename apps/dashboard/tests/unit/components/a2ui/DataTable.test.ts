/**
 * DataTable.test.ts — Tests unitarios para DataTable.svelte.
 *
 * Verifica:
 * - Renderizado de columnas como headers
 * - Renderizado de filas con datos
 * - Paginación opcional (pageSize)
 * - Empty state sin rows
 */

import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import DataTable from '../../../../src/lib/components/a2ui/DataTable.svelte';

describe('DataTable', () => {
	const columns = [
		{ key: 'name', label: 'Nombre' },
		{ key: 'role', label: 'Rol' },
		{ key: 'status', label: 'Estado' }
	];

	const rows = [
		{ name: 'Ana López', role: 'Admin', status: 'Activo' },
		{ name: 'Carlos Ruiz', role: 'Editor', status: 'Inactivo' },
		{ name: 'María Gómez', role: 'Visor', status: 'Activo' }
	];

	it('renders column headers', () => {
		render(DataTable, {
			props: { columns, rows }
		});

		expect(screen.getByText('Nombre')).toBeInTheDocument();
		expect(screen.getByText('Rol')).toBeInTheDocument();
		expect(screen.getByText('Estado')).toBeInTheDocument();
	});

	it('renders all rows', () => {
		render(DataTable, {
			props: { columns, rows }
		});

		expect(screen.getByText('Ana López')).toBeInTheDocument();
		expect(screen.getByText('Carlos Ruiz')).toBeInTheDocument();
		expect(screen.getByText('María Gómez')).toBeInTheDocument();
	});

	it('renders correct number of rows', () => {
		render(DataTable, {
			props: { columns, rows }
		});

		const table = screen.getByTestId('data-table');
		const bodyRows = table.querySelectorAll('tbody tr');
		expect(bodyRows.length).toBe(3);
	});

	it('renders correct number of columns', () => {
		render(DataTable, {
			props: { columns, rows }
		});

		const table = screen.getByTestId('data-table');
		const headerCols = table.querySelectorAll('thead th');
		expect(headerCols.length).toBe(3);
	});

	it('renders cell values in correct order', () => {
		render(DataTable, {
			props: { columns, rows }
		});

		const table = screen.getByTestId('data-table');
		const firstRowCells = table.querySelectorAll('tbody tr:first-child td');
		expect(firstRowCells[0]).toHaveTextContent('Ana López');
		expect(firstRowCells[1]).toHaveTextContent('Admin');
		expect(firstRowCells[2]).toHaveTextContent('Activo');
	});

	it('limits rows when pageSize is set', () => {
		const manyRows = Array.from({ length: 10 }, (_, i) => ({
			name: `Usuario ${i + 1}`,
			role: 'Rol',
			status: 'Activo'
		}));

		render(DataTable, {
			props: { columns, rows: manyRows, pageSize: 5 }
		});

		const table = screen.getByTestId('data-table');
		const bodyRows = table.querySelectorAll('tbody tr');
		expect(bodyRows.length).toBe(5);
	});

	it('shows empty state when rows array is empty', () => {
		render(DataTable, {
			props: { columns, rows: [] }
		});

		expect(screen.getByText('Sin datos disponibles')).toBeInTheDocument();
	});

	it('shows empty state when rows is undefined', () => {
		render(DataTable, {
			props: { columns, rows: undefined }
		});

		expect(screen.getByText('Sin datos disponibles')).toBeInTheDocument();
	});

	it('renders pageSize notice when pagination is active', () => {
		const manyRows = Array.from({ length: 10 }, (_, i) => ({
			name: `Item ${i + 1}`,
			role: 'Test',
			status: 'OK'
		}));

		render(DataTable, {
			props: { columns, rows: manyRows, pageSize: 3 }
		});

		expect(screen.getByText(/Mostrando 3 de 10/)).toBeInTheDocument();
	});
});
