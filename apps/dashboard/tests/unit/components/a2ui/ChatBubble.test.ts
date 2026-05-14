/**
 * ChatBubble.test.ts — Tests unitarios para ChatBubble.svelte.
 *
 * Verifica:
 * - Renderiza correctamente para roles user y assistant
 * - Muestra el contenido del mensaje
 * - Muestra dots de streaming cuando corresponda
 * - Muestra timestamp formateado
 */

import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/svelte';
import ChatBubble from '../../../../src/lib/components/a2ui/ChatBubble.svelte';

describe('ChatBubble', () => {
	it('renders user message with content', () => {
		const { container } = render(ChatBubble, {
			props: {
				role: 'user',
				content: 'Hola, ¿cómo estás?',
				streaming: false,
				timestamp: new Date('2026-05-14T10:00:00')
			}
		});

		const bubble = container.querySelector('.chat-bubble');
		expect(bubble).toBeTruthy();
		expect(bubble?.className).toContain('user');
		expect(bubble?.textContent).toContain('Hola, ¿cómo estás?');
	});

	it('renders assistant message with content', () => {
		const { container } = render(ChatBubble, {
			props: {
				role: 'assistant',
				content: 'Estoy bien, gracias por preguntar.',
				streaming: false,
				timestamp: new Date('2026-05-14T10:00:05')
			}
		});

		const bubble = container.querySelector('.chat-bubble');
		expect(bubble).toBeTruthy();
		expect(bubble?.className).toContain('assistant');
		expect(bubble?.textContent).toContain('Estoy bien, gracias por preguntar.');
	});

	it('shows streaming dots when streaming and content is empty', () => {
		const { container } = render(ChatBubble, {
			props: {
				role: 'assistant',
				content: '',
				streaming: true,
				timestamp: new Date()
			}
		});

		const dots = container.querySelector('.streaming-dots');
		expect(dots).toBeTruthy();
	});

	it('does NOT show streaming dots when streaming but content exists', () => {
		const { container } = render(ChatBubble, {
			props: {
				role: 'assistant',
				content: 'Respuesta parcial',
				streaming: true,
				timestamp: new Date()
			}
		});

		const dots = container.querySelector('.streaming-dots');
		expect(dots).toBeFalsy();
		// El contenido debe mostrarse
		expect(container.textContent).toContain('Respuesta parcial');
	});

	it('does NOT show streaming dots for user messages', () => {
		const { container } = render(ChatBubble, {
			props: {
				role: 'user',
				content: '',
				streaming: false,
				timestamp: new Date()
			}
		});

		const dots = container.querySelector('.streaming-dots');
		expect(dots).toBeFalsy();
	});

	it('displays formatted timestamp', () => {
		const testDate = new Date('2026-05-14T15:30:00');
		const { container } = render(ChatBubble, {
			props: {
				role: 'user',
				content: 'Test',
				streaming: false,
				timestamp: testDate
			}
		});

		// Debe mostrar la hora (depende del locale del entorno)
		// Verificamos que exista un elemento con la hora formateada
		const timeElement = container.querySelector('.bubble-time');
		expect(timeElement).toBeTruthy();
		expect(timeElement?.textContent).toBeTruthy();
		// Verificar que contiene dígitos (hora:minutos)
		expect(timeElement?.textContent).toMatch(/\d{1,2}[:.]\d{2}/);
	});

	it('shows role labels (Tú / Asistente)', () => {
		const { container: userContainer } = render(ChatBubble, {
			props: {
				role: 'user',
				content: 'User msg',
				streaming: false,
				timestamp: new Date()
			}
		});
		expect(userContainer.textContent).toContain('Tú');

		const { container: assistantContainer } = render(ChatBubble, {
			props: {
				role: 'assistant',
				content: 'Assistant msg',
				streaming: false,
				timestamp: new Date()
			}
		});
		expect(assistantContainer.textContent).toContain('Asistente');
	});

	it('has role="log" for accessibility', () => {
		const { container } = render(ChatBubble, {
			props: {
				role: 'assistant',
				content: 'Test',
				streaming: false,
				timestamp: new Date()
			}
		});

		const log = container.querySelector('[role="log"]');
		expect(log).toBeTruthy();
	});

	it('has aria-live="polite" when streaming', () => {
		const { container } = render(ChatBubble, {
			props: {
				role: 'assistant',
				content: '',
				streaming: true,
				timestamp: new Date()
			}
		});

		const log = container.querySelector('[aria-live="polite"]');
		expect(log).toBeTruthy();
	});
});
