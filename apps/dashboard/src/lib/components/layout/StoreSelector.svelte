<script lang="ts">
	import { ui } from '$lib/stores/ui.svelte';
	import Storefront from 'phosphor-svelte/lib/Storefront';
	import CaretDown from 'phosphor-svelte/lib/CaretDown';

	const stores = [
		{ id: 'store1', name: 'Sucursal Centro' },
		{ id: 'store2', name: 'Sucursal Norte' },
		{ id: 'store3', name: 'Depósito Central' }
	];

	let isOpen = $state(false);

	const activeStore = $derived(stores.find(s => s.id === ui.activeStoreId) || stores[0]);

	function selectStore(id: string) {
		ui.activeStoreId = id;
		isOpen = false;
	}
</script>

<div class="store-selector" role="none">
	<button
		class="selector-btn"
		onclick={() => isOpen = !isOpen}
		type="button"
		aria-expanded={isOpen}
		aria-label="Seleccionar sucursal"
	>
		<Storefront size={18} weight="regular" />
		<span class="store-name">{activeStore.name}</span>
		<CaretDown size={14} weight="bold" class="arrow-icon {isOpen ? 'arrow-icon--open' : ''}" />
	</button>

	{#if isOpen}
		<ul class="dropdown-menu" role="listbox">
			{#each stores as store}
				<li>
					<button
						class="dropdown-item"
						class:active={store.id === ui.activeStoreId}
						onclick={() => selectStore(store.id)}
						type="button"
						role="option"
						aria-selected={store.id === ui.activeStoreId}
					>
						{store.name}
					</button>
				</li>
			{/each}
		</ul>
	{/if}
</div>

<style>
	.store-selector {
		position: relative;
		display: inline-block;
	}

	.selector-btn {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 6px 12px;
		background: var(--color-bg-alt);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-md);
		color: var(--color-text);
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		transition: all var(--transition-fast);
	}

	.selector-btn:hover {
		background: var(--color-border-light);
		border-color: var(--color-text-muted);
	}

	.store-name {
		max-width: 140px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.arrow-icon {
		color: var(--color-text-secondary);
		transition: transform var(--transition-fast);
	}

	.arrow-icon--open {
		transform: rotate(180deg);
	}

	.dropdown-menu {
		position: absolute;
		top: calc(100% + 4px);
		left: 0;
		min-width: 180px;
		background: var(--color-surface);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-md);
		box-shadow: var(--shadow-md);
		z-index: 200;
		padding: 4px;
		display: flex;
		flex-direction: column;
		gap: 2px;
	}

	.dropdown-item {
		width: 100%;
		text-align: left;
		padding: 8px 12px;
		background: transparent;
		border: none;
		border-radius: var(--radius-sm);
		color: var(--color-text-secondary);
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		cursor: pointer;
		transition: all var(--transition-fast);
	}

	.dropdown-item:hover {
		background: var(--color-bg-alt);
		color: var(--color-text);
	}

	.dropdown-item.active {
		background: var(--color-accent-light);
		color: var(--color-accent);
	}
</style>
