<script lang="ts">
	type Variant = 'text' | 'card' | 'table-row';

	let {
		variant = 'text',
		lines = 1,
		width
	}: {
		variant?: Variant;
		lines?: number;
		width?: string;
	} = $props();
</script>

{#if variant === 'text'}
	<div class="skeleton-text" style={width ? `width: ${width}` : undefined}>
		{#each Array(lines) as _, i}
			<div
				class="skeleton-line"
				style="width: {i === lines - 1 ? '60%' : '100%'}"
			></div>
		{/each}
	</div>
{:else if variant === 'card'}
	<div class="skeleton-card">
		<div class="skeleton-card-img"></div>
		<div class="skeleton-card-body">
			<div class="skeleton-line" style="width: 70%"></div>
			<div class="skeleton-line" style="width: 40%"></div>
		</div>
	</div>
{:else if variant === 'table-row'}
	<div class="skeleton-table-row">
		{#each Array(lines || 4) as _, i}
			<div class="skeleton-cell" style="width: {['25%', '40%', '15%', '20%'][i] || '20%'}"></div>
		{/each}
	</div>
{/if}

<div role="status" aria-label="Cargando contenido" class="sr-only">Cargando...</div>

<style>
	.skeleton-line {
		height: 12px;
		border-radius: var(--radius-full);
		background: var(--color-border);
		overflow: hidden;
		position: relative;
	}

	.skeleton-line::after {
		content: '';
		position: absolute;
		inset: 0;
		background: linear-gradient(
			90deg,
			transparent,
			var(--color-bg-alt),
			transparent
		);
		animation: shimmer 1.5s infinite;
	}

	.skeleton-text {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-sm);
	}

	.skeleton-card {
		border: 1px solid var(--color-border);
		border-radius: var(--radius-lg);
		overflow: hidden;
	}

	.skeleton-card-img {
		height: 120px;
		background: var(--color-border);
		animation: shimmer 1.5s infinite;
		background-image: linear-gradient(
			90deg,
			var(--color-border) 25%,
			var(--color-bg-alt) 50%,
			var(--color-border) 75%
		);
		background-size: 200% 100%;
	}

	.skeleton-card-body {
		padding: var(--spacing-md);
		display: flex;
		flex-direction: column;
		gap: var(--spacing-sm);
	}

	.skeleton-table-row {
		display: flex;
		gap: var(--spacing-md);
		padding: var(--spacing-md);
		border-bottom: 1px solid var(--color-border-light);
	}

	.skeleton-cell {
		height: 16px;
		border-radius: var(--radius-sm);
		background: var(--color-border);
		animation: shimmer 1.5s infinite;
		background-image: linear-gradient(
			90deg,
			var(--color-border) 25%,
			var(--color-bg-alt) 50%,
			var(--color-border) 75%
		);
		background-size: 200% 100%;
	}

	@keyframes shimmer {
		0% {
			background-position: -200% 0;
		}
		100% {
			background-position: 200% 0;
		}
	}
</style>
