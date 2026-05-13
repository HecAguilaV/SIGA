<script lang="ts">
	import Card from '$lib/components/ui/Card.svelte';

	let { data }: { data: { user?: import('$lib/types/auth').UserSession | null } } = $props();
</script>

<div class="dashboard">
	<div class="dashboard-greeting">
		<h1>Bienvenido, {data.user?.name || 'Usuario'}</h1>
		<p class="dashboard-subtitle">Panel principal — SIGA v2</p>
	</div>

	<div class="dashboard-grid">
		<Card variant="glass" padding="lg">
			{#snippet content()}
				<div class="kpi-placeholder">
					<h3>Dashboard</h3>
					<p>Los KPIs y módulos se agregarán en la Fase 2.</p>
				</div>
			{/snippet}
		</Card>
	</div>
</div>

<style>
	.dashboard {
		max-width: 1200px;
		margin: 0 auto;
	}

	.dashboard-greeting {
		margin-bottom: var(--spacing-xl);
	}

	.dashboard-greeting h1 {
		font-size: var(--font-size-2xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-text);
	}

	.dashboard-subtitle {
		color: var(--color-text-secondary);
		font-size: var(--font-size-sm);
		margin-top: var(--spacing-xs);
	}

	.dashboard-grid {
		display: grid;
		gap: var(--spacing-lg);
	}

	.kpi-placeholder {
		text-align: center;
		padding: var(--spacing-2xl);
	}

	.kpi-placeholder h3 {
		font-size: var(--font-size-lg);
		color: var(--color-text-secondary);
		margin-bottom: var(--spacing-sm);
	}

	.kpi-placeholder p {
		color: var(--color-text-muted);
		font-size: var(--font-size-sm);
	}
</style>
