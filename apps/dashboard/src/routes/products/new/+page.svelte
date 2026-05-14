<script lang="ts">
	import { goto } from '$app/navigation';
	import CrudForm from '$lib/components/crud/CrudForm.svelte';
	import type { FieldDef } from '$lib/components/crud/CrudForm.svelte';
	import Card from '@siga/ui-kit/Card.svelte';
	import Button from '@siga/ui-kit/Button.svelte';
	import ArrowLeft from 'phosphor-svelte/lib/ArrowLeft';

	const productFields: FieldDef<any>[] = [
		{ key: 'name', label: 'Nombre', type: 'text', required: true },
		{ key: 'sku', label: 'SKU', type: 'text', required: true },
		{ key: 'description', label: 'Descripción', type: 'textarea' },
		{ key: 'price', label: 'Precio', type: 'number', required: true },
		{ key: 'stock', label: 'Stock', type: 'number', required: true },
		{ key: 'stockMin', label: 'Stock Mínimo', type: 'number', required: true }
	];

	async function handleSubmit(formData: Record<string, string>) {
		const response = await fetch('/products/new', {
			method: 'POST',
			headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
			body: new URLSearchParams(formData)
		});
		if (response.redirected) {
			goto(response.url);
		}
	}
</script>

<div class="page-header">
	<Button variant="ghost" onclick={() => goto('/products')}>
		<ArrowLeft size={18} weight="bold" />
		Volver
	</Button>
	<h1>Nuevo Producto</h1>
</div>

<Card variant="default" padding="lg">
	{#snippet children()}
		<CrudForm
			fields={productFields}
			onSubmit={handleSubmit}
			mode="create"
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
