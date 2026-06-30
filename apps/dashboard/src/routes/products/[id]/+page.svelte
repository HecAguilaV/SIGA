<script lang="ts">
	import { goto } from '$app/navigation';
	import type { PageData, ActionData } from './$types';
	import CrudForm from '$lib/components/crud/CrudForm.svelte';
	import type { FieldDef } from '$lib/components/crud/types';
	import Card from '@siga/ui-kit/Card.svelte';
	import Button from '@siga/ui-kit/Button.svelte';
	import ArrowLeft from 'phosphor-svelte/lib/ArrowLeft';

	let { data, form }: { data: PageData; form: ActionData } = $props();

	const product = $derived(data.product as Record<string, unknown> ?? {});

	const productFields: FieldDef<any>[] = [
		{ key: 'name', label: 'Nombre', type: 'text', required: true },
		{ key: 'sku', label: 'SKU', type: 'text', required: true },
		{ key: 'description', label: 'Descripción', type: 'textarea' },
		{ key: 'price', label: 'Precio', type: 'number', required: true },
		{ key: 'stock', label: 'Stock', type: 'number', required: true },
		{ key: 'stockMin', label: 'Stock Mínimo', type: 'number', required: true }
	];
</script>

<div class="page-header">
	<Button variant="ghost" onclick={() => goto('/products')}>
		<ArrowLeft size={18} weight="bold" />
		Volver
	</Button>
	<h1>Editar Producto</h1>
</div>

<Card variant="default" padding="lg">
	{#snippet children()}
		<CrudForm
			fields={productFields}
			initialValues={product}
			mode="edit"
			form={form}
		/>
	{/snippet}
</Card>

<style>
	.page-header {
		display: flex;
		align-items: center;
		gap: var(--spacing-md);
		margin-bottom: var(--spacing-lg);
	}

	.page-header h1 {
		font-size: var(--font-size-xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-text);
	}
</style>
