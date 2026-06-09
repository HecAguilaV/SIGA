<script lang="ts">
	import { goto } from '$app/navigation';
	import CrudForm from '$lib/components/crud/CrudForm.svelte';
	import type { FieldDef } from '$lib/components/crud/types';
	import Card from '@siga/ui-kit/Card.svelte';
	import Button from '@siga/ui-kit/Button.svelte';
	import ArrowLeft from 'phosphor-svelte/lib/ArrowLeft';

	const categoryFields: FieldDef<any>[] = [
		{ key: 'name', label: 'Nombre', type: 'text', required: true },
		{ key: 'description', label: 'Descripción', type: 'textarea' }
	];

	async function handleSubmit(formData: Record<string, string>) {
		const res = await fetch('/categories/new', {
			method: 'POST',
			headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
			body: new URLSearchParams(formData)
		});
		if (res.redirected) goto(res.url);
	}
</script>

<div class="page-header">
	<Button variant="ghost" onclick={() => goto('/categories')}>
		<ArrowLeft size={18} weight="bold" /> Volver
	</Button>
	<h1>Nueva Categoría</h1>
</div>

<Card variant="default" padding="lg">
	{#snippet children()}
		<CrudForm fields={categoryFields} onSubmit={handleSubmit} mode="create" />
	{/snippet}
</Card>

<style>
	.page-header { display: flex; align-items: center; gap: var(--spacing-md); margin-bottom: var(--spacing-lg); }
	.page-header h1 { font-size: var(--font-size-xl); font-weight: var(--font-weight-bold); color: var(--color-text); }
</style>
