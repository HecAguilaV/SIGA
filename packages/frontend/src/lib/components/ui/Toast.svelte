<script lang="ts">
	import { createEventDispatcher, onMount } from 'svelte';
	import CheckCircle from 'phosphor-svelte/lib/CheckCircle';
	import XCircle from 'phosphor-svelte/lib/XCircle';
	import WarningCircle from 'phosphor-svelte/lib/WarningCircle';
	import Info from 'phosphor-svelte/lib/Info';
	import X from 'phosphor-svelte/lib/X';

	type ToastType = 'success' | 'error' | 'info' | 'warning';

	let {
		type = 'info',
		message = '',
		autoDismiss = true,
		duration = 5000,
		onDismiss,
		id
	}: {
		type?: ToastType;
		message?: string;
		autoDismiss?: boolean;
		duration?: number;
		onDismiss?: (id?: string) => void;
		id?: string;
	} = $props();

	const dispatch = createEventDispatcher<{ dismiss: void }>();

	const icons = {
		success: CheckCircle,
		error: XCircle,
		info: Info,
		warning: WarningCircle
	};

	const Icon = icons[type];

	onMount(() => {
		if (autoDismiss && duration > 0) {
			const timer = setTimeout(() => {
				onDismiss?.(id);
				dispatch('dismiss');
			}, duration);
			return () => clearTimeout(timer);
		}
	});
</script>

<div class="toast toast-{type}" role="alert" aria-live="polite">
	<div class="toast-icon">
		<Icon size={20} weight="fill" />
	</div>
	<p class="toast-message">{message}</p>
	<button
		class="toast-dismiss"
		onclick={() => {
			onDismiss?.(id);
			dispatch('dismiss');
		}}
		aria-label="Cerrar notificación"
		type="button"
	>
		<X size={16} weight="bold" />
	</button>
</div>

<style>
	.toast {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 12px 16px;
		border-radius: var(--radius-md);
		box-shadow: var(--shadow-md);
		min-width: 300px;
		max-width: 450px;
		animation: toast-enter 250ms ease-out;
	}

	.toast-success {
		background: var(--color-success-bg);
		color: var(--color-success-text);
		border: 1px solid var(--color-success);
	}

	.toast-error {
		background: var(--color-error-bg);
		color: var(--color-error-text);
		border: 1px solid var(--color-error);
	}

	.toast-info {
		background: var(--color-info-bg);
		color: var(--color-info-text);
		border: 1px solid var(--color-info);
	}

	.toast-warning {
		background: var(--color-warning-bg);
		color: var(--color-warning-text);
		border: 1px solid var(--color-warning);
	}

	.toast-icon {
		flex-shrink: 0;
		display: flex;
	}

	.toast-message {
		flex: 1;
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		line-height: 1.4;
	}

	.toast-dismiss {
		flex-shrink: 0;
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 24px;
		height: 24px;
		border: none;
		background: transparent;
		color: inherit;
		opacity: 0.7;
		cursor: pointer;
		border-radius: var(--radius-sm);
		transition: opacity var(--transition-fast);
	}

	.toast-dismiss:hover {
		opacity: 1;
	}

	@keyframes toast-enter {
		from {
			opacity: 0;
			transform: translateX(100%);
		}
		to {
			opacity: 1;
			transform: translateX(0);
		}
	}
</style>
