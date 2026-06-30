<script lang="ts">
	import { goto } from '$app/navigation';
	import type { PageData, ActionData } from './$types';
	import CrudForm from '$lib/components/crud/CrudForm.svelte';
	import type { FieldDef } from '$lib/components/crud/types';
	import Card from '@siga/ui-kit/Card.svelte';
	import Button from '@siga/ui-kit/Button.svelte';
	import ArrowLeft from 'phosphor-svelte/lib/ArrowLeft';

	let { data, form }: { data: PageData; form: ActionData } = $props();

	const userFields: FieldDef<any>[] = [
		{ key: 'name', label: 'Nombre', type: 'text', required: true },
		{ key: 'email', label: 'Email', type: 'email', required: true },
		{ key: 'password', label: 'Contraseña', type: 'password', required: true },
		{
			key: 'rol',
			label: 'Rol',
			type: 'select',
			required: true,
			options: [
				{ value: 'ADMINISTRATOR', label: 'Administrador' },
				{ value: 'OPERATOR', label: 'Operador' },
				{ value: 'CASHIER', label: 'Cajero' }
			]
		}
	];
</script>

<div class="page-header">
	<Button variant="ghost" onclick={() => goto('/users')}>
		<ArrowLeft size={18} weight="bold" /> Volver
	</Button>
	<h1>Nuevo Usuario</h1>
</div>

<Card variant="default" padding="lg">
	{#snippet children()}
		<CrudForm fields={userFields} mode="create" form={form} />
	{/snippet}
</Card>

<style>
	.page-header { display: flex; align-items: center; gap: var(--spacing-md); margin-bottom: var(--spacing-lg); }
	.page-header h1 { font-size: var(--font-size-xl); font-weight: var(--font-weight-bold); color: var(--color-text); }
</style>
