<script lang="ts">
	import Modal from '@siga/ui-kit/Modal.svelte';
	import Button from '@siga/ui-kit/Button.svelte';
	import Input from '@siga/ui-kit/Input.svelte';
	import { ui } from '$lib/stores/ui.svelte';
	import { toast } from '$lib/stores/toast.svelte';

	let product = $state('1');
	let store = $state('store1');
	let type = $state('entrada');
	let quantity = $state('10');
	let reason = $state('');
	let loading = $state(false);

	const products = [
		{ id: '1', name: 'Harina de Trigo 1kg', sku: 'HAR-001' },
		{ id: '2', name: 'Azúcar Blanca 1kg', sku: 'AZU-001' },
		{ id: '3', name: 'Aceite de Girasol 1.5L', sku: 'ACE-001' },
		{ id: '4', name: 'Leche Entera 1L', sku: 'LEC-001' }
	];

	const stores = [
		{ id: 'store1', name: 'Sucursal Centro' },
		{ id: 'store2', name: 'Sucursal Norte' },
		{ id: 'store3', name: 'Depósito Central' }
	];

	async function handleSubmit(e: SubmitEvent) {
		e.preventDefault();
		loading = true;

		// Simular delay de API
		await new Promise(resolve => setTimeout(resolve, 800));

		const prod = products.find(p => p.id === product);
		const st = stores.find(s => s.id === store);

		toast.add({
			type: 'success',
			message: `Movimiento de stock registrado: ${type === 'entrada' ? '+' : '-'}${quantity} ${prod?.name} en ${st?.name}`
		});

		loading = false;
		ui.closeNewMovement();
		
		// Resetear campos
		reason = '';
		quantity = '10';
	}
</script>

<Modal open={ui.newMovementOpen} onClose={() => ui.closeNewMovement()} title="Nuevo Movimiento de Inventario">
	<form onsubmit={handleSubmit} class="movement-form">
		<div class="form-row">
			<label class="form-label" for="product-select">Producto</label>
			<select id="product-select" bind:value={product} required class="form-select">
				{#each products as p}
					<option value={p.id}>{p.name} ({p.sku})</option>
				{/each}
			</select>
		</div>

		<div class="form-grid">
			<div class="form-row">
				<label class="form-label" for="store-select">Local / Depósito</label>
				<select id="store-select" bind:value={store} required class="form-select">
					{#each stores as s}
						<option value={s.id}>{s.name}</option>
					{/each}
				</select>
			</div>

			<div class="form-row">
				<label class="form-label" for="type-select">Tipo de Movimiento</label>
				<select id="type-select" bind:value={type} required class="form-select">
					<option value="entrada">Entrada (+)</option>
					<option value="salida">Salida (-)</option>
				</select>
			</div>
		</div>

		<div class="form-row">
			<Input
				type="number"
				label="Cantidad"
				bind:value={quantity}
				required
				min="1"
			/>
		</div>

		<div class="form-row">
			<Input
				type="text"
				label="Motivo / Justificación"
				bind:value={reason}
				placeholder="Ej: Reposición de mercadería, ajuste de inventario"
				required
			/>
		</div>

		<div class="form-actions">
			<Button variant="secondary" onclick={() => ui.closeNewMovement()}>
				Cancelar
			</Button>
			<Button type="submit" variant="primary" {loading}>
				Registrar
			</Button>
		</div>
	</form>
</Modal>

<style>
	.movement-form {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
	}

	.form-row {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-xs);
	}

	.form-grid {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: var(--spacing-md);
	}

	.form-label {
		font-size: var(--font-size-sm);
		font-weight: var(--font-weight-medium);
		color: var(--color-text);
	}

	.form-select {
		width: 100%;
		padding: 10px 14px;
		font-size: var(--font-size-base);
		font-family: var(--font-sans);
		color: var(--color-text);
		background: var(--color-surface);
		border: 1px solid var(--color-border);
		border-radius: var(--radius-md);
		outline: none;
		transition: border-color var(--transition-fast);
	}

	.form-select:focus {
		border-color: var(--color-accent);
	}

	.form-actions {
		display: flex;
		align-items: center;
		justify-content: flex-end;
		gap: var(--spacing-sm);
		margin-top: var(--spacing-md);
	}
</style>
