<script lang="ts">
	import Spinner from './Spinner.svelte';

	type Variant = 'primary' | 'secondary' | 'ghost' | 'danger';
	type Size = 'sm' | 'md' | 'lg';

	let {
		variant = 'primary',
		size = 'md',
		loading = false,
		disabled = false,
		type = 'button',
		children,
		...rest
	}: {
		variant?: Variant;
		size?: Size;
		loading?: boolean;
		disabled?: boolean;
		type?: 'button' | 'submit' | 'reset';
		children?: import('svelte').Snippet;
	} & Record<string, unknown> = $props();

	const isDisabled = $derived(disabled || loading);
</script>

<button
	{type}
	class="btn btn-{variant} btn-{size}"
	disabled={isDisabled}
	aria-busy={loading}
	{...rest}
>
	{#if loading}
		<Spinner size="sm" variant={variant === 'primary' ? 'light' : 'default'} />
	{/if}
	{#if children}
		{@render children()}
	{/if}
</button>

<style>
	.btn {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		gap: var(--spacing-sm);
		font-family: var(--font-sans);
		font-weight: var(--font-weight-medium);
		border-radius: var(--radius-md);
		border: 1px solid transparent;
		cursor: pointer;
		transition: all var(--transition-fast);
		white-space: nowrap;
		line-height: 1;
	}

	.btn:disabled {
		opacity: 0.5;
		cursor: not-allowed;
		pointer-events: none;
	}

	/* Sizes */
	.btn-sm {
		padding: 6px 12px;
		font-size: var(--font-size-sm);
	}

	.btn-md {
		padding: 10px 20px;
		font-size: var(--font-size-base);
	}

	.btn-lg {
		padding: 14px 28px;
		font-size: var(--font-size-lg);
	}

	/* Variants */
	.btn-primary {
		background: var(--color-accent);
		color: #fff;
		border-color: var(--color-accent);
	}

	.btn-primary:hover:not(:disabled) {
		background: var(--color-accent-hover);
		border-color: var(--color-accent-hover);
	}

	.btn-primary:active:not(:disabled) {
		background: var(--color-accent-dark);
	}

	.btn-secondary {
		background: var(--color-surface);
		color: var(--color-text);
		border-color: var(--color-border);
	}

	.btn-secondary:hover:not(:disabled) {
		background: var(--color-surface-hover);
		border-color: var(--color-accent);
	}

	.btn-ghost {
		background: transparent;
		color: var(--color-text-secondary);
		border-color: transparent;
	}

	.btn-ghost:hover:not(:disabled) {
		background: var(--color-bg-alt);
		color: var(--color-text);
	}

	.btn-danger {
		background: var(--color-error);
		color: #fff;
		border-color: var(--color-error);
	}

	.btn-danger:hover:not(:disabled) {
		background: #dc2626;
		border-color: #dc2626;
	}
</style>
