/**
 * AhorremosTiempoButton.test.ts — Unit tests for the A2UI agentive toggle.
 *
 * Verifies the toggle renders Phosphor icons (Sparkle in classic mode,
 * CaretLeft in agentive mode) instead of pictographic emoji, with the
 * SVG hidden from AT (the button carries the accessible name).
 *
 * Spec: ui-icon-consistency R1, R2 (strict TDD — RED before GREEN swap).
 */

import { describe, it, expect, beforeEach } from 'vitest';
import { render } from '@testing-library/svelte';
import AhorremosTiempoButton from '$lib/components/a2ui/AhorremosTiempoButton.svelte';
import { a2ui } from '$lib/stores/a2ui.svelte';

describe('AhorremosTiempoButton', () => {
	beforeEach(() => {
		a2ui.exitAgentiveMode();
	});

	it('renders a Phosphor SVG in the toggle icon (classic mode)', () => {
		const { container } = render(AhorremosTiempoButton, { props: { currentRoute: '/' } });

		// Button keeps its accessible name (preserved behavior)
		const toggle = container.querySelector('.a2ui-toggle');
		expect(toggle?.getAttribute('aria-label')).toBe('Activar modo agéntico');

		// The emoji ✨ is replaced by a Phosphor SVG inside the icon host
		const svg = container.querySelector('.a2ui-toggle-icon svg');
		expect(svg).not.toBeNull();
		expect(svg?.getAttribute('aria-hidden')).toBe('true');
	});

	it('renders a Phosphor SVG in the toggle icon (agentive mode)', () => {
		a2ui.enterAgentiveMode({ route: '/' });
		const { container } = render(AhorremosTiempoButton, { props: { currentRoute: '/' } });

		const toggle = container.querySelector('.a2ui-toggle');
		expect(toggle?.getAttribute('aria-label')).toBe('Volver al modo clásico');

		// The emoji ← is replaced by a Phosphor SVG inside the icon host
		const svg = container.querySelector('.a2ui-toggle-icon svg');
		expect(svg).not.toBeNull();
		expect(svg?.getAttribute('aria-hidden')).toBe('true');
	});
});
