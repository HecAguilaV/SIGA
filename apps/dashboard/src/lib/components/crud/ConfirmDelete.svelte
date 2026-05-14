<script lang="ts">
	import Modal from '@siga/ui-kit/Modal.svelte';
	import Button from '@siga/ui-kit/Button.svelte';

	let {
		open = false,
		itemName = '',
		message = '¿Estás seguro de eliminar este elemento?',
		loading = false,
		onConfirm,
		onCancel
	}: {
		open?: boolean;
		itemName?: string;
		message?: string;
		loading?: boolean;
		onConfirm: () => void;
		onCancel: () => void;
	} = $props();
</script>

<Modal {open} title="Confirmar eliminación" size="sm" onClose={onCancel}>
	{#snippet children()}
		<div class="confirm-content">
			<p class="confirm-message">{message}</p>
			{#if itemName}
				<p class="confirm-item">
					<strong>"{itemName}"</strong>
				</p>
			{/if}
			<div class="confirm-actions">
				<Button variant="ghost" onclick={onCancel} disabled={loading}>
					Cancelar
				</Button>
				<Button variant="danger" onclick={onConfirm} loading={loading} disabled={loading}>
					Eliminar
				</Button>
			</div>
		</div>
	{/snippet}
</Modal>

<style>
	.confirm-content {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-md);
	}

	.confirm-message {
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
		line-height: 1.5;
	}

	.confirm-item {
		font-size: var(--font-size-base);
		color: var(--color-text);
		text-align: center;
		padding: var(--spacing-sm);
		background: var(--color-bg-alt);
		border-radius: var(--radius-md);
	}

	.confirm-actions {
		display: flex;
		justify-content: flex-end;
		gap: var(--spacing-sm);
		padding-top: var(--spacing-sm);
	}
</style>
