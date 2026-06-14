<script lang="ts">
	/**
	 * QuickNav.svelte — Panel de navegación rápida para el modo clásico.
	 */
	import { fade, slide } from 'svelte/transition';
	import CaretRight from 'phosphor-svelte/lib/CaretRight';
	import List from 'phosphor-svelte/lib/List';

	let {
		items = []
	}: {
		items: Array<{ label: string; href: string; icon: any; color: string }>;
	} = $props();

	let isOpen = $state(true);
</script>

<div class="quick-nav" class:closed={!isOpen}>
	<button class="toggle-btn" onclick={() => isOpen = !isOpen} aria-label={isOpen ? 'Cerrar navegación' : 'Abrir navegación'}>
		<List size={20} />
		{#if isOpen}
			<span class="toggle-label">Módulos</span>
		{/if}
	</button>

	{#if isOpen}
		<nav class="nav-list" transition:slide={{ axis: 'x', duration: 300 }}>
			{#each items as item}
				<a href={item.href} class="nav-link" style="--item-color: {item.color}">
					<div class="icon-wrapper">
						<item.icon size={18} weight="duotone" />
					</div>
					<span class="label">{item.label}</span>
					<CaretRight size={12} weight="bold" class="arrow" />
				</a>
			{/each}
		</nav>
	{/if}
</div>

<style>
	.quick-nav {
		position: fixed;
		left: calc(var(--w-sidebar-width) + var(--spacing-lg));
		top: 80px;
		background: var(--color-surface-glass);
		backdrop-filter: blur(12px);
		border: 1px solid var(--color-surface-glass-border);
		border-radius: var(--radius-xl);
		box-shadow: var(--shadow-lg);
		z-index: 50;
		display: flex;
		flex-direction: column;
		overflow: hidden;
		transition: all var(--transition-base);
		max-width: 240px;
	}

	.quick-nav.closed {
		max-width: 50px;
	}

	.toggle-btn {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: var(--spacing-md);
		width: 100%;
		background: transparent;
		border: none;
		color: var(--color-text);
		cursor: pointer;
		font-weight: var(--font-weight-bold);
		border-bottom: 1px solid var(--color-border-light);
	}

	.toggle-label {
		font-size: var(--font-size-sm);
		text-transform: uppercase;
		letter-spacing: 0.05em;
	}

	.nav-list {
		display: flex;
		flex-direction: column;
		padding: var(--spacing-sm);
		gap: 4px;
	}

	.nav-link {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 10px 12px;
		border-radius: var(--radius-md);
		color: var(--color-text-secondary);
		text-decoration: none;
		font-size: var(--font-size-sm);
		transition: all var(--transition-fast);
	}

	.nav-link:hover {
		background: var(--color-bg-alt);
		color: var(--color-text);
		transform: translateX(4px);
	}

	.icon-wrapper {
		width: 28px;
		height: 28px;
		border-radius: var(--radius-sm);
		background: var(--color-bg-alt);
		display: flex;
		align-items: center;
		justify-content: center;
		color: var(--item-color);
	}

	.label {
		flex: 1;
		white-space: nowrap;
	}

	.arrow {
		opacity: 0;
		transform: translateX(-5px);
		transition: all var(--transition-fast);
	}

	.nav-link:hover .arrow {
		opacity: 0.5;
		transform: translateX(0);
	}

	@media (max-width: 1200px) {
		.quick-nav {
			display: none; /* Hide on small screens where Bento covers it or sidebar is enough */
		}
	}
</style>
