<script lang="ts">
	import Card from '$lib/components/ui/Card.svelte';

	let { data }: { data: { status: string; message: string } } = $props();
</script>

<svelte:head>
	<title>Verificar cuenta — SIGA</title>
</svelte:head>

<Card variant="glass" padding="lg">
	{#snippet header()}
		<div class="verify-header">
			<h1 class="verify-title">Verificar cuenta</h1>
		</div>
	{/snippet}

	{#snippet children()}
		{#if data.status === 'loading'}
			<div class="verify-loading">
				<span class="material-symbols-outlined" style="font-size: 32px;">hourglass_top</span>
				<p>Verificando tu cuenta...</p>
			</div>
		{:else}
			<div class="verify-result" class:verify-error={data.status === 'error'}>
				<span class="material-symbols-outlined" style="font-size: 32px;">
					{data.status === 'error' ? 'error' : 'check_circle'}
				</span>
				<p>{data.message}</p>
				<a href="/login" class="verify-link">Ir a iniciar sesión</a>
			</div>
		{/if}
	{/snippet}
</Card>

<style>
	.verify-header {
		text-align: center;
		padding: var(--spacing-lg) 0;
	}

	.verify-title {
		font-size: var(--font-size-3xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-accent);
	}

	.verify-loading {
		text-align: center;
		padding: var(--spacing-xl);
		color: var(--color-text-secondary);
	}

	.verify-result {
		text-align: center;
		padding: var(--spacing-xl);
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: var(--spacing-md);
		color: var(--color-success-text, #065f46);
	}

	.verify-result.verify-error {
		color: var(--color-error-text);
	}

	.verify-link {
		color: var(--color-accent);
		font-weight: var(--font-weight-medium);
		margin-top: var(--spacing-sm);
	}

	.verify-link:hover {
		text-decoration: underline;
	}
</style>
