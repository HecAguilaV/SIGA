<script lang="ts">
	import { onMount } from 'svelte';

	type Size = 'sm' | 'md' | 'lg';

	let {
		open = false,
		title = '',
		onClose,
		size = 'md',
		children,
		...rest
	}: {
		open?: boolean;
		title?: string;
		onClose?: () => void;
		size?: Size;
		children?: import('svelte').Snippet;
	} & Record<string, unknown> = $props();

	let dialogEl: HTMLDivElement | undefined = $state();
	let previousActiveElement: HTMLElement | null = null;

	function getFocusableElements(): HTMLElement[] {
		if (!dialogEl) return [];
		const selectors = [
			'a[href]',
			'button',
			'input',
			'textarea',
			'select',
			'[tabindex]:not([tabindex="-1"])'
		];
		return Array.from(dialogEl.querySelectorAll<HTMLElement>(selectors.join(',')));
	}

	function handleKeydown(e: KeyboardEvent) {
		if (!open) return;

		if (e.key === 'Escape') {
			e.preventDefault();
			onClose?.();
			return;
		}

		if (e.key === 'Tab') {
			const focusable = getFocusableElements();
			if (focusable.length === 0) return;

			const first = focusable[0];
			const last = focusable[focusable.length - 1];

			if (e.shiftKey) {
				if (document.activeElement === first) {
					e.preventDefault();
					last.focus();
				}
			} else {
				if (document.activeElement === last) {
					e.preventDefault();
					first.focus();
				}
			}
		}
	}

	function handleBackdropClick(e: MouseEvent) {
		if (e.target === e.currentTarget) {
			onClose?.();
		}
	}

	$effect(() => {
		if (open) {
			previousActiveElement = document.activeElement as HTMLElement;
			$effect(() => {
				requestAnimationFrame(() => {
					const focusable = getFocusableElements();
					if (focusable.length > 0) {
						focusable[0].focus();
					}
				});
			});
		} else if (previousActiveElement) {
			previousActiveElement.focus();
			previousActiveElement = null;
		}
	});
</script>

<svelte:window onkeydown={handleKeydown} />

{#if open}
	<div
		class="modal-backdrop"
		role="dialog"
		aria-modal="true"
		aria-label={title || 'Diálogo'}
		onclick={handleBackdropClick}
		{...rest}
	>
		<div class="modal-panel modal-{size}" bind:this={dialogEl}>
			<div class="modal-header">
				<h2 class="modal-title">{title}</h2>
				{#if onClose}
					<button
						class="modal-close"
						onclick={onClose}
						aria-label="Cerrar"
						type="button"
					>
						&times;
					</button>
				{/if}
			</div>
			<div class="modal-body">
				{#if children}
					{@render children()}
				{/if}
			</div>
		</div>
	</div>
{/if}

<style>
	.modal-backdrop {
		position: fixed;
		inset: 0;
		z-index: 1000;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(0, 0, 0, 0.5);
		backdrop-filter: blur(4px);
		padding: var(--spacing-md);
	}

	.modal-panel {
		background: var(--color-surface);
		border-radius: var(--radius-lg);
		box-shadow: var(--shadow-lg);
		width: 100%;
		max-height: 85vh;
		display: flex;
		flex-direction: column;
		animation: modal-enter 200ms ease-out;
	}

	.modal-sm {
		max-width: 400px;
	}

	.modal-md {
		max-width: 560px;
	}

	.modal-lg {
		max-width: 720px;
	}

	.modal-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: var(--spacing-lg);
		border-bottom: 1px solid var(--color-border-light);
	}

	.modal-title {
		font-size: var(--font-size-lg);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
	}

	.modal-close {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 32px;
		height: 32px;
		border: none;
		background: transparent;
		color: var(--color-text-muted);
		font-size: 1.25rem;
		border-radius: var(--radius-sm);
		cursor: pointer;
		transition: background var(--transition-fast), color var(--transition-fast);
	}

	.modal-close:hover {
		background: var(--color-bg-alt);
		color: var(--color-text);
	}

	.modal-body {
		padding: var(--spacing-lg);
		overflow-y: auto;
		flex: 1;
	}

	@keyframes modal-enter {
		from {
			opacity: 0;
			transform: scale(0.95) translateY(-10px);
		}
		to {
			opacity: 1;
			transform: scale(1) translateY(0);
		}
	}
</style>
