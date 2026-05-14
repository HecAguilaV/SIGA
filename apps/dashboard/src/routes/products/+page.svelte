<script lang="ts">
	import { page } from '$app/stores';
	import { goto } from '$app/navigation';
	import type { PageData } from './$types';
	import type { ProductListItem } from '$lib/types/inventory';
	import CrudTable from '$lib/components/crud/CrudTable.svelte';
	import type { ColumnDef, ActionDef } from '$lib/components/crud/CrudTable.svelte';
	import SearchBar from '$lib/components/crud/SearchBar.svelte';
	import ConfirmDelete from '$lib/components/crud/ConfirmDelete.svelte';
	import Button from '@siga/ui-kit/Button.svelte';
	import Plus from 'phosphor-svelte/lib/Plus';

	let { data }: { data: PageData } = $props();

	const productColumns: ColumnDef<ProductListItem>[] = [
		{ key: 'name', label: 'Nombre', sortable: true },
		{ key: 'sku', label: 'SKU' },
		{ key: 'categoryName', label: 'Categoría' },
		{
			key: 'price',
			label: 'Precio',
			render: (item) => `$${item.price.toLocaleString('es-AR')}`
		},
		{
			key: 'stock',
			label: 'Stock',
			class: 'col-stock',
			render: (item) => `${item.stock}`
		}
	];

	const productActions: ActionDef[] = [
		{
			label: 'Editar',
			onClick: (item) => goto(`/products/${item.id}`)
		},
		{
			label: 'Eliminar',
			variant: 'danger',
			onClick: (item) => {
				deleteTarget = { id: item.id, name: item.name };
			}
		}
	];

	function handlePageChange(newPage: number) {
		const url = new URL($page.url);
		url.searchParams.set('page', String(newPage));
		goto(url.toString(), { replaceState: true, keepFocus: true });
	}

	let deleteTarget = $state<{ id: string; name: string } | null>(null);

	const products = $derived(data.products as ProductListItem[] ?? []);
	const total = $derived(data.total as number ?? 0);
	const currentPage = $derived(data.page as number ?? 1);

	function onDeleteConfirm() {
		if (!deleteTarget) return;
		goto(`/products?delete=${deleteTarget.id}`);
		deleteTarget = null;
	}

	function onDeleteCancel() {
		deleteTarget = null;
	}
</script>

<div class="page-header">
	<div>
		<h1>Productos</h1>
		<p class="page-subtitle">Gestioná tu catálogo de productos</p>
	</div>
	<Button variant="primary" onclick={() => goto('/products/new')}>
		<Plus size={18} weight="bold" />
		Nuevo producto
	</Button>
</div>

<div class="page-toolbar">
	<SearchBar placeholder="Buscar productos..." basePath="/products" />
</div>

<CrudTable
	columns={productColumns}
	data={products}
	{total}
	page={currentPage}
	pageSize={20}
	actions={productActions}
	onPageChange={handlePageChange}
>
	{#snippet children()}
		<p>No se encontraron productos</p>
	{/snippet}
</CrudTable>

<ConfirmDelete
	open={deleteTarget !== null}
	itemName={deleteTarget?.name ?? ''}
	onConfirm={onDeleteConfirm}
	onCancel={onDeleteCancel}
/>

<style>
	.page-header {
		display: flex;
		align-items: flex-start;
		justify-content: space-between;
		margin-bottom: var(--spacing-lg);
	}

	.page-header h1 {
		font-size: var(--font-size-2xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-text);
	}

	.page-subtitle {
		font-size: var(--font-size-sm);
		color: var(--color-text-muted);
		margin-top: 2px;
	}

	.page-toolbar {
		margin-bottom: var(--spacing-md);
	}
</style>
