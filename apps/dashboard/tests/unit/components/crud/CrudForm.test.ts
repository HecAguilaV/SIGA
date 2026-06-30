import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/svelte';
import CrudForm from '$lib/components/crud/CrudForm.svelte';
import type { FieldDef } from '$lib/components/crud/types';

describe('CrudForm (use:enhance)', () => {
	const fields: FieldDef<any>[] = [
		{ key: 'name', label: 'Nombre', type: 'text', required: true },
		{ key: 'email', label: 'Email', type: 'email', required: true },
		{ key: 'age', label: 'Edad', type: 'number' }
	];

	it('renders form fields', () => {
		const { container } = render(CrudForm, {
			props: { fields, mode: 'create' }
		});
		expect(container.textContent).toContain('Nombre');
		expect(container.textContent).toContain('Email');
		expect(container.textContent).toContain('Edad');
	});

	it('renders as a native form with method POST (SvelteKit form action)', () => {
		const { container } = render(CrudForm, {
			props: { fields, mode: 'create' }
		});
		const form = container.querySelector('form');
		expect(form).toBeTruthy();
		expect(form?.getAttribute('method')).toBe('POST');
	});

	it('shows submit button with "Guardar" for create mode', () => {
		const { container } = render(CrudForm, {
			props: { fields, mode: 'create' }
		});
		const submitBtn = container.querySelector('button[type="submit"]');
		expect(submitBtn).toBeTruthy();
		expect(container.textContent).toContain('Guardar');
	});

	it('shows submit button with "Actualizar" for edit mode', () => {
		const { container } = render(CrudForm, {
			props: { fields, mode: 'edit' }
		});
		expect(container.textContent).toContain('Actualizar');
	});

	it('populates fields from initialValues in edit mode', () => {
		render(CrudForm, {
			props: {
				fields,
				mode: 'edit',
				initialValues: { name: 'Juan Pérez', email: 'juan@test.com', age: 30 }
			}
		});
		const nameInput = screen.getByLabelText('Nombre *') as HTMLInputElement;
		const emailInput = screen.getByLabelText('Email *') as HTMLInputElement;
		expect(nameInput.value).toBe('Juan Pérez');
		expect(emailInput.value).toBe('juan@test.com');
	});

	it('displays a banner with the server error from form action result', () => {
		const { container } = render(CrudForm, {
			props: {
				fields,
				mode: 'create',
				form: { error: 'SKU duplicado' } as any
			}
		});
		expect(container.textContent).toContain('SKU duplicado');
		expect(container.querySelector('.form-banner-error')).toBeTruthy();
	});

	it('validates required fields on submit and shows inline errors', async () => {
		const { container } = render(CrudForm, {
			props: { fields, mode: 'create' }
		});
		const form = container.querySelector('form') as HTMLFormElement;
		await fireEvent.submit(form);
		expect(container.textContent).toContain('Nombre es requerido');
		expect(container.textContent).toContain('Email es requerido');
	});
});
