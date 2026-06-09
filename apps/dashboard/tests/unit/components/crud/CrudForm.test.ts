import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/svelte';
import CrudForm from '$lib/components/crud/CrudForm.svelte';
import type { FieldDef } from '$lib/components/crud/types';

describe('CrudForm', () => {
	const fields: FieldDef<any>[] = [
		{ key: 'name', label: 'Nombre', type: 'text', required: true },
		{ key: 'email', label: 'Email', type: 'email', required: true },
		{ key: 'age', label: 'Edad', type: 'number' }
	];

	it('renders form fields', () => {
		const { container } = render(CrudForm, {
			props: { fields, onSubmit: async () => {}, mode: 'create' }
		});
		expect(container.textContent).toContain('Nombre');
		expect(container.textContent).toContain('Email');
		expect(container.textContent).toContain('Edad');
	});

	it('shows submit button with correct label for create mode', () => {
		const { container } = render(CrudForm, {
			props: { fields, onSubmit: async () => {}, mode: 'create' }
		});
		const submitBtn = container.querySelector('button[type="submit"]');
		expect(submitBtn).toBeTruthy();
		expect(container.textContent).toContain('Guardar');
	});

	it('shows submit button with correct label for edit mode', () => {
		const { container } = render(CrudForm, {
			props: { fields, onSubmit: async () => {}, mode: 'edit' }
		});
		expect(container.textContent).toContain('Actualizar');
	});

	it('calls onSubmit when form is submitted with valid data', async () => {
		const onSubmit = vi.fn().mockResolvedValue(undefined);
		const { container } = render(CrudForm, {
			props: { fields, onSubmit, mode: 'create' }
		});

		const nameInput = screen.getByLabelText('Nombre *');
		const emailInput = screen.getByLabelText('Email *');

		await fireEvent.input(nameInput, { target: { value: 'Juan Pérez' } });
		await fireEvent.input(emailInput, { target: { value: 'juan@test.com' } });

		const form = container.querySelector('form');
		if (form) {
			form.dispatchEvent(new SubmitEvent('submit', { cancelable: true }));
		}

		expect(onSubmit).toHaveBeenCalled();
	});
});
