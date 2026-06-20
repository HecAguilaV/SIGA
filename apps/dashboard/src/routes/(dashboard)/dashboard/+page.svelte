<script lang="ts">
	import { fade, fly, scale } from 'svelte/transition';
	import { cubicOut } from 'svelte/easing';
	import { onMount } from 'svelte';
	import Card from '@siga/ui-kit/Card.svelte';
	import Badge from '@siga/ui-kit/Badge.svelte';

	import InsightPanel from '$lib/components/dashboard/InsightPanel.svelte';
	import AnomalyList from '$lib/components/dashboard/AnomalyList.svelte';
	
	// Phosphor icons
	import Gauge from 'phosphor-svelte/lib/Gauge';
	import Package from 'phosphor-svelte/lib/Package';
	import Storefront from 'phosphor-svelte/lib/Storefront';
	import CreditCard from 'phosphor-svelte/lib/CreditCard';
	import ChartBar from 'phosphor-svelte/lib/ChartBar';
	import Gear from 'phosphor-svelte/lib/Gear';
	import ArrowRight from 'phosphor-svelte/lib/ArrowRight';
	import Sparkle from 'phosphor-svelte/lib/Sparkle';
	import Warning from 'phosphor-svelte/lib/Warning';
	import CurrencyDollar from 'phosphor-svelte/lib/CurrencyDollar';
	import ChartLineUp from 'phosphor-svelte/lib/ChartLineUp';
	import TrendUp from 'phosphor-svelte/lib/TrendUp';
	import TrendDown from 'phosphor-svelte/lib/TrendDown';

	let { data }: { data: any } = $props();

	let viewMode = $state<'hub' | 'stats'>('hub');
	let mounted = $state(false);

	onMount(() => { mounted = true; });

	interface Module {
		id: string;
		title: string;
		description: string;
		href: string;
		icon: any;
		color: string;
		size: 'large' | 'medium' | 'small';
		tags?: string[];
	}

	const modules: Module[] = [
		{
			id: 'analytics-predictive',
			title: 'Análisis Predictivo',
			description: 'IA avanzada para proyecciones de ventas y optimización de stock.',
			href: '/analytics/predictive',
			icon: Sparkle,
			color: 'var(--color-accent)',
			size: 'large',
			tags: ['AI Powered', 'Nuevo']
		},
		{
			id: 'inventory',
			title: 'Gestión de Inventario',
			description: 'Control total de productos, SKUs y movimientos de bodega.',
			href: '/products',
			icon: Package,
			color: 'var(--color-primary)',
			size: 'medium'
		},
		{
			id: 'pos',
			title: 'Punto de Venta (POS)',
			description: 'Terminal de ventas rápido e intuitivo para cajas.',
			href: '/pos',
			icon: CreditCard,
			color: 'var(--color-success)',
			size: 'medium'
		},
		{
			id: 'stores',
			title: 'Locales y Sucursales',
			description: 'Administración de sedes físicas y personal.',
			href: '/stores',
			icon: Storefront,
			color: 'var(--color-warning)',
			size: 'small'
		},
		{
			id: 'reports',
			title: 'Reportes y BI',
			description: 'Analíticas históricas y exportación de datos.',
			href: '/analytics',
			icon: ChartBar,
			color: 'var(--color-info)',
			size: 'small'
		},
		{
			id: 'config',
			title: 'Configuración',
			description: 'Ajustes del sistema y control de usuarios.',
			href: '/users',
			icon: Gear,
			color: 'var(--color-text-muted)',
			size: 'small'
		}
	];

	const insights = $derived((data.insights ?? []) as any[]);
	const anomalies = $derived((data.anomalies ?? []) as any[]);
</script>

<svelte:head>
	<title>SIGA — Dashboard</title>
</svelte:head>

<div class="dashboard-container" in:fade={{ duration: 400 }}>


	<header class="hub-header" in:fly={{ y: -20, duration: 600 }}>
		<div class="header-content">
			<h1>SIGA Core Hub</h1>
			<p>Bienvenido, <strong>{data.user?.name ?? 'Usuario'}</strong>. ¿Qué gestionamos hoy?</p>
		</div>
		<div class="header-actions-hub">
			<div class="view-toggle">
				<button class:active={viewMode === 'hub'} onclick={() => viewMode = 'hub'}>Acceso Rápido</button>
				<button class:active={viewMode === 'stats'} onclick={() => viewMode = 'stats'}>Estado Global</button>
			</div>
			<div class="header-status">
				<Badge variant="success">Sistema Online</Badge>
				<span class="version">v3.1.2-agentive</span>
			</div>
		</div>
	</header>

	{#if viewMode === 'hub'}
		<div class="hub-grid" in:fade={{ duration: 300 }}>
			{#if mounted}
				{#each modules as module, i (module.id)}
					<a 
						href={module.href} 
						class="module-link {module.size}" 
						style="--module-color: {module.color}"
						aria-label="Entrar a {module.title}"
						in:scale={{ duration: 600, delay: i * 80, start: 0.9, easing: cubicOut }}
					>
						<Card variant="glass" padding="none">
							{#snippet children()}
								<div class="card-inner">
									<div class="card-glow"></div>
									<header class="card-header">
										<div class="icon-box">
											<module.icon size={module.size === 'large' ? 32 : 24} weight="duotone" />
										</div>
										<div class="tags">
											{#each module.tags ?? [] as tag}
												<span class="tag">{tag}</span>
											{/each}
										</div>
									</header>
									<div class="card-body">
										<h2>{module.title}</h2>
										<p class="description">{module.description}</p>
									</div>
									<footer class="card-footer">
										<span class="action-text">Ingresar</span>
										<ArrowRight size={16} weight="bold" />
									</footer>
								</div>
							{/snippet}
						</Card>
					</a>
				{/each}
			{/if}
		</div>
	{:else}
		<!-- MODO CLÁSICO: ESTADÍSTICAS -->
		<div class="stats-view" in:fly={{ y: 20, duration: 500 }}>
			<div class="kpi-grid">
				{#each insights as insight (insight.id)}
					<Card variant="glass" padding="lg">
						{#snippet children()}
							<div class="kpi-card">
								<div class="kpi-header-stats">
									<span class="kpi-icon">
										{#if insight.variant === 'primary'}
											<ChartBar size={28} weight="duotone" aria-hidden="true" />
										{:else if insight.variant === 'info'}
											<Storefront size={28} weight="duotone" aria-hidden="true" />
										{:else if insight.variant === 'danger'}
											<Warning size={28} weight="duotone" aria-hidden="true" />
										{:else if insight.variant === 'success'}
											<CurrencyDollar size={28} weight="duotone" aria-hidden="true" />
										{:else}
											<ChartLineUp size={28} weight="duotone" aria-hidden="true" />
										{/if}
									</span>
									{#if insight.trend}
										<Badge variant={insight.trend === 'up' ? 'success' : insight.trend === 'down' ? 'danger' : 'info'}>
											{#if insight.trend === 'up'}
												<TrendUp size={14} weight="bold" aria-hidden="true" />
											{:else if insight.trend === 'down'}
												<TrendDown size={14} weight="bold" aria-hidden="true" />
											{:else}
												<ArrowRight size={14} weight="bold" aria-hidden="true" />
											{/if}
											{insight.trendValue ?? ''}%
										</Badge>
									{/if}
								</div>
								<div class="kpi-body">
									<p class="kpi-value">{insight.value}</p>
								</div>
								<p class="kpi-title">{insight.title}</p>
							</div>
						{/snippet}
					</Card>
				{/each}
			</div>

			<div class="dashboard-grid">
				<Card variant="default" padding="md">
					{#snippet header()}
						<h2 class="panel-title">Hallazgos y Sugerencias</h2>
					{/snippet}
					{#snippet children()}
						<InsightPanel insights={[]} title="" />
					{/snippet}
				</Card>

				<Card variant="default" padding="md">
					{#snippet header()}
						<h2 class="panel-title">Anomalías</h2>
					{/snippet}
					{#snippet children()}
						<AnomalyList anomalies={anomalies} title="" />
					{/snippet}
				</Card>
			</div>
		</div>
	{/if}
</div>

<style>
	.dashboard-container {
		max-width: 1200px;
		margin: 0 auto;
		display: flex;
		flex-direction: column;
		gap: var(--spacing-2xl);
		padding-bottom: var(--spacing-2xl);
	}

	.hub-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.header-content h1 {
		font-size: var(--font-size-3xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-primary);
		margin-bottom: var(--spacing-xs);
	}

	.header-content p {
		color: var(--color-text-secondary);
		font-size: var(--font-size-lg);
	}

	.header-actions-hub {
		display: flex;
		align-items: center;
		gap: var(--spacing-lg);
	}

	.view-toggle {
		display: flex;
		background: var(--color-bg-alt);
		padding: 4px;
		border-radius: var(--radius-lg);
		border: 1px solid var(--color-border);
	}

	.view-toggle button {
		padding: 6px 14px;
		border-radius: var(--radius-md);
		border: none;
		background: transparent;
		color: var(--color-text-secondary);
		font-size: var(--font-size-xs);
		font-weight: var(--font-weight-bold);
		cursor: pointer;
		transition: all var(--transition-fast);
	}

	.view-toggle button.active {
		background: var(--color-surface);
		color: var(--color-primary);
		box-shadow: var(--shadow-sm);
	}

	.header-status {
		display: flex;
		flex-direction: column;
		align-items: flex-end;
		gap: var(--spacing-xs);
	}

	.version {
		font-family: var(--font-mono);
		font-size: 10px;
		color: var(--color-text-muted);
	}

	.hub-grid {
		display: grid;
		grid-template-columns: repeat(12, 1fr);
		grid-auto-rows: minmax(180px, auto);
		gap: var(--spacing-lg);
	}

	/* Bento Sizes */
	.large { grid-column: span 8; grid-row: span 2; }
	.medium { grid-column: span 4; grid-row: span 2; }
	.small { grid-column: span 4; grid-row: span 1; }

	.module-link {
		text-decoration: none;
		display: flex;
		flex-direction: column;
		transition: transform var(--transition-base), filter var(--transition-base);
		color: inherit;
		border-radius: var(--radius-xl);
		outline: none;
	}

	.module-link:hover {
		transform: translateY(-6px);
		filter: brightness(1.08);
	}

	.module-link:focus-visible {
		box-shadow: 0 0 0 3px var(--color-accent);
	}

	:global(.module-link .card) {
		height: 100%;
		display: flex;
		flex-direction: column;
		pointer-events: none;
	}

	.card-inner {
		height: 100%;
		display: flex;
		flex-direction: column;
		padding: var(--spacing-xl);
		position: relative;
		overflow: hidden;
		background: linear-gradient(135deg, transparent, rgba(255,255,255,0.02));
	}

	.card-glow {
		position: absolute;
		top: -50%;
		left: -50%;
		width: 200%;
		height: 200%;
		background: radial-gradient(circle at center, var(--module-color), transparent 70%);
		opacity: 0.05;
		pointer-events: none;
		transition: opacity var(--transition-base);
	}

	.module-link:hover .card-glow { opacity: 0.15; }

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		margin-bottom: var(--spacing-lg);
	}

	.icon-box {
		width: 48px;
		height: 48px;
		background: var(--color-bg-alt);
		border-radius: var(--radius-lg);
		display: flex;
		align-items: center;
		justify-content: center;
		color: var(--module-color);
		box-shadow: var(--shadow-sm);
	}

	.large .icon-box { width: 64px; height: 64px; }

	.tags { display: flex; gap: var(--spacing-xs); }
	.tag {
		font-size: 10px;
		font-family: var(--font-mono);
		padding: 2px 8px;
		background: var(--module-color);
		color: var(--color-text-inverse);
		border-radius: var(--radius-full);
		font-weight: bold;
		opacity: 0.9;
	}

	.card-body { flex: 1; }
	.card-body h2 {
		font-size: var(--font-size-xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-text);
		margin-bottom: var(--spacing-sm);
	}

	.large .card-body h2 { font-size: var(--font-size-2xl); }

	.card-body .description {
		font-size: var(--font-size-sm);
		color: var(--color-text-secondary);
		line-height: 1.6;
		max-width: 90%;
	}

	.card-footer {
		margin-top: var(--spacing-xl);
		display: flex;
		align-items: center;
		gap: var(--spacing-xs);
		color: var(--module-color);
		font-weight: var(--font-weight-bold);
		font-size: var(--font-size-sm);
		opacity: 0.7;
		transform: translateX(-5px);
		transition: all var(--transition-base);
	}

	.module-link:hover .card-footer {
		opacity: 1;
		transform: translateX(0);
	}

	/* Stats View Styles */
	.stats-view {
		display: flex;
		flex-direction: column;
		gap: var(--spacing-xl);
	}

	.kpi-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
		gap: var(--spacing-md);
	}

	.kpi-header-stats {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.kpi-icon {
		font-size: 1.5rem;
	}

	.kpi-value {
		font-size: var(--font-size-3xl);
		font-weight: var(--font-weight-bold);
		color: var(--color-text);
		line-height: 1;
	}

	.kpi-title {
		font-size: var(--font-size-sm);
		color: var(--color-text-muted);
	}

	.dashboard-grid {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: var(--spacing-lg);
	}

	.panel-title {
		font-size: var(--font-size-base);
		font-weight: var(--font-weight-semibold);
		color: var(--color-text);
	}

	@media (max-width: 1024px) {
		.large { grid-column: span 12; }
		.medium { grid-column: span 6; }
		.small { grid-column: span 6; }
	}

	@media (max-width: 768px) {
		.hub-grid { grid-template-columns: 1fr; }
		.large, .medium, .small { grid-column: span 1; grid-row: span 1; }
		.dashboard-grid { grid-template-columns: 1fr; }
		.hub-header { flex-direction: column; align-items: flex-start; gap: var(--spacing-md); }
		.header-actions-hub { width: 100%; justify-content: space-between; }
	}
</style>
