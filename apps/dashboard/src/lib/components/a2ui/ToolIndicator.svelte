<script lang="ts">
	/**
	 * ToolIndicator.svelte — Indicador de llamada a herramienta del agente.
	 *
	 * Props:
	 * - name: string — nombre interno de la herramienta
	 * - status: 'running' | 'done' | 'error' — estado actual
	 * - label: string — etiqueta visible para el usuario
	 *
	 * Muestra una animación de pulso cuando la herramienta está en ejecución.
	 */

	import Gear from 'phosphor-svelte/lib/Gear';
	import CheckCircle from 'phosphor-svelte/lib/CheckCircle';
	import WarningCircle from 'phosphor-svelte/lib/WarningCircle';

	let {
		name = '',
		status = 'running',
		label = ''
	}: {
		name?: string;
		status?: 'running' | 'done' | 'error';
		label?: string;
	} = $props();

	const displayLabel = $derived(label || name);
	const isRunning = $derived(status === 'running');
	const isDone = $derived(status === 'done');
	const isError = $derived(status === 'error');
</script>

<div
	class="tool-indicator"
	class:running={isRunning}
	class:done={isDone}
	class:error={isError}
	role="status"
	aria-label={`Herramienta ${displayLabel}: ${status === 'running' ? 'en ejecución' : status === 'done' ? 'completada' : 'error'}`}
>
	<span class="tool-icon" aria-hidden="true">
		{#if isRunning}
			<span class="pulse-icon">
				<Gear size={14} weight="fill" />
			</span>
		{:else if isDone}
			<CheckCircle size={14} weight="fill" />
		{:else if isError}
			<WarningCircle size={14} weight="fill" />
		{/if}
	</span>
	<span class="tool-label">{displayLabel}</span>
	{#if isRunning}
		<span class="tool-badge running-badge">ejecutando</span>
	{:else if isDone}
		<span class="tool-badge done-badge">completado</span>
	{:else if isError}
		<span class="tool-badge error-badge">error</span>
	{/if}
</div>

<style>
	.tool-indicator {
		display: inline-flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 4px 12px;
		border-radius: var(--radius-full);
		font-size: var(--font-size-xs);
		font-weight: var(--font-weight-medium);
		transition: all var(--transition-fast);
	}

	.tool-indicator.running {
		background: var(--color-info-bg);
		color: var(--color-info-text);
		border: 1px solid var(--color-info);
	}

	.tool-indicator.done {
		background: var(--color-success-bg);
		color: var(--color-success-text);
		border: 1px solid transparent;
	}

	.tool-indicator.error {
		background: var(--color-error-bg);
		color: var(--color-error-text);
		border: 1px solid transparent;
	}

	.tool-icon {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
	}

	/* Pulse animation for running state */
	.pulse-icon {
		display: inline-flex;
		animation: pulse-spin 2s ease-in-out infinite;
	}

	@keyframes pulse-spin {
		0% {
			transform: rotate(0deg);
			opacity: 0.6;
		}
		50% {
			transform: rotate(180deg);
			opacity: 1;
		}
		100% {
			transform: rotate(360deg);
			opacity: 0.6;
		}
	}

	.tool-label {
		white-space: nowrap;
	}

	.tool-badge {
		font-size: 0.625rem;
		font-weight: var(--font-weight-semibold);
		text-transform: uppercase;
		letter-spacing: 0.5px;
		padding: 1px 6px;
		border-radius: var(--radius-full);
	}

	.running-badge {
		background: var(--color-info);
		color: #fff;
	}

	.done-badge {
		background: var(--color-success);
		color: #fff;
	}

	.error-badge {
		background: var(--color-error);
		color: #fff;
	}
</style>
