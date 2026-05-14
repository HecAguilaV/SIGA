<script lang="ts">
	/**
	 * ChatInput.svelte — Área de entrada de texto para el chat A2UI.
	 *
	 * Props:
	 * - disabled: boolean — deshabilita el input (durante streaming/reconexión)
	 * - placeholder: string — texto placeholder
	 *
	 * Eventos:
	 * - onsend: (text: string) => void — se dispara al enviar un mensaje
	 *
	 * Características:
	 * - Enter envía, Shift+Enter nueva línea
	 * - Debounce anti-spam: ignora mensajes idénticos dentro de 2s
	 * - Auto-focus al montar
	 */

	let {
		disabled = false,
		placeholder = 'Escribe un mensaje...',
		onsend = (_text: string) => {}
	}: {
		disabled?: boolean;
		placeholder?: string;
		onsend?: (text: string) => void;
	} = $props();

	let inputValue = $state('');
	let textareaEl: HTMLTextAreaElement | undefined = $state();
	let lastSentText = $state('');
	let lastSentTime = $state(0);

	const spamDebounceMs = 2000;

	function handleKeydown(event: KeyboardEvent) {
		if (event.key === 'Enter' && !event.shiftKey) {
			event.preventDefault();
			sendMessage();
		}
	}

	function sendMessage() {
		const text = inputValue.trim();
		if (!text || disabled) return;

		// Anti-spam: ignorar mensajes idénticos dentro de 2s
		const now = Date.now();
		if (text === lastSentText && now - lastSentTime < spamDebounceMs) {
			return;
		}

		lastSentText = text;
		lastSentTime = now;
		onsend(text);
		inputValue = '';
	}

	function handleInput() {
		// Auto-resize textarea
		if (textareaEl) {
			textareaEl.style.height = 'auto';
			textareaEl.style.height = Math.min(textareaEl.scrollHeight, 120) + 'px';
		}
	}
</script>

<div class="chat-input" role="form" aria-label="Entrada de chat">
	<textarea
		bind:this={textareaEl}
		class="input-field"
		bind:value={inputValue}
		{placeholder}
		{disabled}
		rows="1"
		onkeydown={handleKeydown}
		oninput={handleInput}
		aria-label="Mensaje"
	></textarea>
	<button
		class="send-button"
		onclick={sendMessage}
		disabled={disabled || !inputValue.trim()}
		aria-label="Enviar mensaje"
		type="button"
	>
		{#if disabled}
			<span class="send-spinner" aria-hidden="true"></span>
		{:else}
			<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
				<line x1="22" y1="2" x2="11" y2="13"></line>
				<polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
			</svg>
		{/if}
	</button>
</div>

<style>
	.chat-input {
		display: flex;
		gap: var(--spacing-sm);
		padding: var(--spacing-sm) var(--spacing-md);
		border-top: 1px solid var(--color-border);
		background: var(--color-surface);
		align-items: flex-end;
	}

	.input-field {
		flex: 1;
		padding: var(--spacing-sm) var(--spacing-md);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-lg);
		background: var(--color-bg);
		color: var(--color-text);
		font-size: var(--font-size-sm);
		font-family: var(--font-sans);
		resize: none;
		outline: none;
		min-height: 38px;
		max-height: 120px;
		line-height: 1.5;
		transition: border-color var(--transition-fast);
	}

	.input-field:focus {
		border-color: var(--color-accent);
	}

	.input-field::placeholder {
		color: var(--color-text-muted);
	}

	.input-field:disabled {
		opacity: 0.5;
		cursor: not-allowed;
	}

	.send-button {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 38px;
		height: 38px;
		border: none;
		border-radius: var(--radius-full);
		background: var(--color-accent);
		color: #fff;
		cursor: pointer;
		flex-shrink: 0;
		transition: background var(--transition-fast), opacity var(--transition-fast);
	}

	.send-button:hover:not(:disabled) {
		background: var(--color-accent-hover);
	}

	.send-button:disabled {
		opacity: 0.4;
		cursor: not-allowed;
	}

	.send-spinner {
		width: 16px;
		height: 16px;
		border: 2px solid rgba(255, 255, 255, 0.3);
		border-top-color: #fff;
		border-radius: 50%;
		animation: spin 0.6s linear infinite;
	}

	@keyframes spin {
		to {
			transform: rotate(360deg);
		}
	}
</style>
