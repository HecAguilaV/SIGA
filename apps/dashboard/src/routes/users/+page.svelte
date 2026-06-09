<script lang="ts">
	import { page } from '$app/stores';
	import { goto } from '$app/navigation';
	import type { PageData } from './$types';
	import CrudTable from '$lib/components/crud/CrudTable.svelte';
	import type { ColumnDef, ActionDef } from '$lib/components/crud/types';
	import SearchBar from '$lib/components/crud/SearchBar.svelte';
	import ConfirmDelete from '$lib/components/crud/ConfirmDelete.svelte';
	import Button from '@siga/ui-kit/Button.svelte';
	import Plus from 'phosphor-svelte/lib/Plus';

	let { data }: { data: PageData } = $props();

	const userColumns: ColumnDef<any>[] = [
		{ key: 'name', label: 'Nombre', sortable: true },
		{ key: 'email', label: 'Email' },
		{ key: 'rol', label: 'Rol' },
		{
			key: 'active',
			label: 'Estado',
			render: (item: any) => item.active ? 'Activo' : 'Inactivo'
		}
	];

	const userActions: ActionDef<any>[] = [
		{
			label: 'Editar',
			onClick: (item: any) => goto(`/users/${item.id}`)
		},
		{
			label: 'Eliminar',
			variant: 'danger',
			onClick: (item: any) => {
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

	const users = $derived(data.users ?? []);
	const total = $derived(data.total ?? 0);
	const currentPage = $derived(data.page ?? 1);

	function onDeleteConfirm() {
		if (!deleteTarget) return;
		goto(`/users?delete=${deleteTarget.id}`);
		deleteTarget = null;
	}

	function onDeleteCancel() {
		deleteTarget = null;
	}
</script>

<div class="page-header">
	<div>
		<h1>Usuarios</h1>
		<p class="page-subtitle">Gestioná los usuarios del sistema</p>
	</div>
	<Button variant="primary" onclick={() => goto('/users/new')}>
		<Plus size={18} weight="bold" /> Nuevo usuario
	</Button>
</div>

<div class="page-toolbar">
	<SearchBar placeholder="Buscar usuarios..." basePath="/users" />
</div>

<CrudTable
	columns={userColumns}
	data={users}
	{total}
	page={currentPage}
	pageSize={20}
	actions={userActions}
	onPageChange={handlePageChange}
>
	{#snippet children()}
		<p>No se encontraron usuarios</p>
	{/snippet}
</CrudTable>

<ConfirmDelete
	open={deleteTarget !== null}
	itemName={deleteTarget?.name ?? ''}
	onConfirm={onDeleteConfirm}
	onCancel={onDeleteCancel}
/>

<style>
	.page-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: var(--spacing-lg); }
	.page-header h1 { font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); color: var(--color-text); }
	.page-subtitle { font-size: var(--font-size-sm); color: var(--color-text-muted); margin-top: 2px; }
	.page-toolbar { margin-bottom: var(--spacing-md); }
</style>
