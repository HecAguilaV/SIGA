import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import Button from '../../../src/lib/components/ui/Button.svelte';

describe('Button', () => {
	it('renders with default props', () => {
		const { container } = render(Button, { props: { children: () => 'Click me' } });
		const button = container.querySelector('button');
		expect(button).toBeTruthy();
		expect(button?.className).toContain('btn-primary');
		expect(button?.className).toContain('btn-md');
	});

	it('renders with primary variant', () => {
		const { container } = render(Button, { props: { variant: 'primary', children: () => 'Primary' } });
		const button = container.querySelector('button');
		expect(button?.className).toContain('btn-primary');
	});

	it('renders with secondary variant', () => {
		const { container } = render(Button, { props: { variant: 'secondary', children: () => 'Secondary' } });
		const button = container.querySelector('button');
		expect(button?.className).toContain('btn-secondary');
	});

	it('renders with ghost variant', () => {
		const { container } = render(Button, { props: { variant: 'ghost', children: () => 'Ghost' } });
		const button = container.querySelector('button');
		expect(button?.className).toContain('btn-ghost');
	});

	it('renders with danger variant', () => {
		const { container } = render(Button, { props: { variant: 'danger', children: () => 'Danger' } });
		const button = container.querySelector('button');
		expect(button?.className).toContain('btn-danger');
	});

	it('renders with different sizes', () => {
		const { container: smContainer } = render(Button, { props: { size: 'sm', children: () => 'Small' } });
		expect(smContainer.querySelector('.btn-sm')).toBeTruthy();

		const { container: lgContainer } = render(Button, { props: { size: 'lg', children: () => 'Large' } });
		expect(lgContainer.querySelector('.btn-lg')).toBeTruthy();
	});

	it('disables button when disabled prop is true', () => {
		const { container } = render(Button, { props: { disabled: true, children: () => 'Disabled' } });
		const button = container.querySelector('button');
		expect(button).toBeDisabled();
	});

	it('shows spinner and disables when loading', () => {
		const { container } = render(Button, { props: { loading: true, children: () => 'Loading' } });
		const button = container.querySelector('button');
		expect(button).toBeDisabled();
		expect(button).toHaveAttribute('aria-busy', 'true');
		// Spinner should be rendered (role="status")
		const spinner = container.querySelector('[role="status"]');
		expect(spinner).toBeTruthy();
	});

	it('renders as submit type when specified', () => {
		const { container } = render(Button, { props: { type: 'submit', children: () => 'Submit' } });
		const button = container.querySelector('button');
		expect(button).toHaveAttribute('type', 'submit');
	});
});
