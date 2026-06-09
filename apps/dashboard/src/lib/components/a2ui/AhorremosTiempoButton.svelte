<script lang="ts">
	/**
	 * AhorremosTiempoButton.svelte — Botón de transición al modo agéntico A2UI.
	 *
	 * Cuando está en modo clásico: muestra "Ahorremos tiempo: SIGA"
	 * Cuando está en modo agentivo: muestra "Volver al modo clásico"
	 *
	 * Props:
	 * - currentRoute: ruta actual para enviar contexto al agente
	 * - onActivate: callback opcional al activar modo agentivo
	 */

	import { a2ui } from '$lib/stores/a2ui.svelte';
	import { chat } from '$lib/stores/chat.svelte';

	let {
		currentRoute = '/'
	}: {
		currentRoute?: string;
	} = $props();

	/**
	 * handleClick — Activa o desactiva el modo agentivo.
	 * Al activar, envía un mensaje al agente con el contexto de ruta.
	 */
	function handleClick(): void {
		if (a2ui.isAgentive) {
			a2ui.exitAgentiveMode();
		} else {
			a2ui.enterAgentiveMode({ route: currentRoute });

			// Enviar mensaje al agente con contexto
			const message = `Acabo de activar el modo agéntico. Estoy en ${currentRoute}. Mostrame el panel principal.`;
			chat.send(message, currentRoute, { hidden: true });
		}
	}
</script>

<button
	class="a2ui-toggle"
	class:a2ui-toggle--active={a2ui.isAgentive}
	onclick={handleClick}
	type="button"
	aria-label={a2ui.isAgentive ? 'Volver al modo clásico' : 'Activar modo agéntico'}
>
	<span class="a2ui-toggle-icon">
		{#if a2ui.isAgentive}
			←
		{:else}
			✨
		{/if}
	</span>
	<span class="a2ui-toggle-text">
		{#if a2ui.isAgentive}
			Volver al modo clásico
		{:else}
			Ahorremos tiempo: SIGA
		{/if}
	</span>
</button>

<style>
	.a2ui-toggle {
		display: inline-flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 8px 16px;
		font-family: var(--font-sans);
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		border-radius: var(--radius-md);
		border: 1px solid var(--color-accent);
		background: var(--color-accent);
		color: var(--color-primary);
		cursor: pointer;
		transition: all var(--transition-fast);
		white-space: nowrap;
	}

	.a2ui-toggle:hover {
		background: var(--color-accent-hover);
		border-color: var(--color-accent-hover);
	}

	.a2ui-toggle--active {
		background: var(--color-surface);
		color: var(--color-text);
		border-color: var(--color-border);
	}

	.a2ui-toggle--active:hover {
		background: var(--color-surface-hover);
		border-color: var(--color-accent);
	}

	.a2ui-toggle-icon {
		font-size: var(--font-size-base);
		line-height: 1;
	}

	@media (max-width: 768px) {
		.a2ui-toggle-text {
			display: none;
		}

		.a2ui-toggle {
			padding: 8px;
			min-width: 36px;
			min-height: 36px;
			justify-content: center;
		}

		.a2ui-toggle-icon {
			font-size: var(--font-size-lg);
		}
	}
</style>
