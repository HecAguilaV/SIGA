<script lang="ts">
	import { page } from '$app/stores';
	import { goto } from '$app/navigation';
	import MagnifyingGlass from 'phosphor-svelte/lib/MagnifyingGlass';
	import X from 'phosphor-svelte/lib/X';

	let {
		placeholder = 'Buscar...',
		debounceMs = 300,
		basePath = ''
	}: {
		placeholder?: string;
		debounceMs?: number;
		basePath?: string;
	} = $props();

	let searchValue = $state($page.url.searchParams.get('search') || '');
	let debounceTimer: ReturnType<typeof setTimeout> | undefined;

	function handleInput(e: Event) {
		const target = e.currentTarget as HTMLInputElement;
		searchValue = target.value;

		clearTimeout(debounceTimer);
		debounceTimer = setTimeout(() => {
			updateUrl(searchValue);
		}, debounceMs);
	}

	function updateUrl(value: string) {
		const url = new URL($page.url);
		if (value) {
			url.searchParams.set('search', value);
		} else {
			url.searchParams.delete('search');
		}
		url.searchParams.set('page', '1');
		goto(url.toString(), { replaceState: true, keepFocus: true });
	}

	function clearSearch() {
		searchValue = '';
		updateUrl('');
	}

	function handleKeydown(e: KeyboardEvent) {
		if (e.key === 'Escape') {
			clearSearch();
			(e.target as HTMLInputElement).blur();
		}
		if (e.key === 'Enter') {
			clearTimeout(debounceTimer);
			updateUrl(searchValue);
		}
	}
</script>

<div class="search-bar" role="search">
	<MagnifyingGlass size={18} weight="regular" class="search-icon" />
	<input
		type="search"
		class="search-input"
		{placeholder}
		value={searchValue}
		oninput={handleInput}
		onkeydown={handleKeydown}
		aria-label={placeholder}
	/>
	{#if searchValue}
		<button
			class="search-clear"
			onclick={clearSearch}
			aria-label="Limpiar búsqueda"
			type="button"
		>
			<X size={16} weight="bold" />
		</button>
	{/if}
</div>

<style>
	.search-bar {
		display: flex;
		align-items: center;
		gap: var(--spacing-sm);
		padding: 10px 16px;
		background: #f8fafc; /* bg-slate-50 */
		border: 1px solid #e2e8f0; /* slate-200 */
		border-radius: var(--radius-xl);
		transition: all var(--transition-fast);
		max-width: 440px;
	}

	.search-bar:focus-within {
		background: var(--color-surface);
		border-color: #009579; /* st-tertiary-container */
		box-shadow: 0 4px 12px rgba(0, 149, 121, 0.08);
	}

	.search-icon {
		color: var(--color-text-muted);
		flex-shrink: 0;
	}

	.search-input {
		flex: 1;
		border: none;
		background: transparent;
		font-size: var(--font-size-sm);
		color: var(--color-text);
		outline: none;
		min-width: 0;
	}

	.search-input::placeholder {
		color: var(--color-text-muted);
	}

	.search-clear {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 20px;
		height: 20px;
		border: none;
		background: transparent;
		color: var(--color-text-muted);
		border-radius: var(--radius-sm);
		cursor: pointer;
		flex-shrink: 0;
		transition: color var(--transition-fast);
	}

	.search-clear:hover {
		color: var(--color-text);
	}
</style>
