import { describe, it, expect, beforeEach, vi } from 'vitest';
import { get } from 'svelte/store';
import { theme } from '../../../src/lib/stores/theme.svelte';

describe('Theme Store', () => {
	beforeEach(() => {
		// Limpiar localStorage entre tests
		localStorage.clear();
		// Resetear el store
		theme.set('light');
		// Limpiar atributo data-theme
		document.documentElement.removeAttribute('data-theme');
	});

	it('starts with light theme by default', () => {
		expect(get(theme)).toBe('light');
	});

	it('toggle switches from light to dark', () => {
		theme.set('light');
		theme.toggle();
		expect(get(theme)).toBe('dark');
	});

	it('toggle switches from dark to light', () => {
		theme.set('dark');
		theme.toggle();
		expect(get(theme)).toBe('light');
	});

	it('persists theme to localStorage on set', () => {
		theme.set('dark');
		expect(localStorage.getItem('siga-theme')).toBe('dark');
	});

	it('persists theme to localStorage on toggle', () => {
		theme.set('light');
		theme.toggle();
		expect(localStorage.getItem('siga-theme')).toBe('dark');
	});

	it('applies data-theme attribute to document', () => {
		theme.set('dark');
		expect(document.documentElement.getAttribute('data-theme')).toBe('dark');

		theme.set('light');
		expect(document.documentElement.getAttribute('data-theme')).toBe('light');
	});

	it('updates data-theme on toggle', () => {
		theme.set('light');
		theme.toggle();
		expect(document.documentElement.getAttribute('data-theme')).toBe('dark');
	});
});
