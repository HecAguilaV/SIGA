<script lang="ts">
	import BentoGrid from '$lib/components/layout/BentoGrid.svelte';
	import GlassCard from '$lib/components/layout/GlassCard.svelte';
	import Badge from '@siga/ui-kit/Badge.svelte';
	import { onMount } from 'svelte';
	import { fade, fly, scale } from 'svelte/transition';
	import { cubicOut } from 'svelte/easing';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	// Use real data from server
	const criticalStock = $derived(data.criticalStock ?? []);
	const aiInsights = $derived(data.aiInsights);

	let shimmerOpacity = $state(0.1);
	let mounted = $state(false);

	onMount(() => {
		mounted = true;
		const interval = setInterval(() => {
			shimmerOpacity = Math.random() * 0.15 + 0.05;
		}, 3000);
		return () => clearInterval(interval);
	});

	// Chart heights (still decorative for now until we have a time-series service)
	const historicalHeights = [40, 45, 38, 55, 65, 75];
	const predictiveHeights = [80, 85, 90, 82, 78, 95];
</script>

<svelte:head>
	<title>Análisis Predictivo de Ventas - SIGA</title>
	<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet" />
</svelte:head>

<div class="predictive-page px-gutter max-w-container-max mx-auto" in:fade={{ duration: 400 }}>
	<!-- Header Section -->
	<header class="flex justify-between items-end mb-8" in:fly={{ y: -20, duration: 600, delay: 100 }}>
		<div>
			<h2 class="text-display-lg text-primary">Análisis Predictivo de Ventas</h2>
			<p class="text-on-surface-variant text-body-lg">Proyecciones inteligentes y gestión automatizada de suministros impulsada por la inteligencia de SIGA.</p>
		</div>
		<div class="flex gap-3">
			<button class="flex items-center gap-2 px-4 py-2 rounded-lg bg-surface-container-lowest shadow-sm text-on-surface-variant border border-outline-variant hover:bg-surface-container-low transition-all">
				<span class="material-symbols-outlined">calendar_today</span>
				<span class="text-label-caps">Próximos 30 días</span>
			</button>
			<button class="flex items-center gap-2 px-4 py-2 rounded-lg bg-on-tertiary-container text-on-tertiary shadow-lg hover:opacity-90 transition-all">
				<span class="material-symbols-outlined">refresh</span>
				<span class="text-label-caps">Recalcular Modelos</span>
			</button>
		</div>
	</header>

	<!-- Bento Grid -->
	<BentoGrid>
		<!-- Main Chart -->
		<GlassCard span={8} class="relative overflow-hidden border-outline-variant/30">
			<div class="flex justify-between items-center mb-6">
				<h3 class="text-headline-sm flex items-center gap-2">
					<span class="material-symbols-outlined text-on-tertiary-container">trending_up</span>
					Proyección de Demanda vs. Ventas Históricas
				</h3>
				<div class="flex items-center gap-4 text-label-caps">
					<span class="flex items-center gap-1"><span class="w-3 h-3 rounded-full bg-on-tertiary-container"></span> Histórico</span>
					<span class="flex items-center gap-1"><span class="w-3 h-3 rounded-full bg-on-tertiary-container/30"></span> Predictivo</span>
				</div>
			</div>

			<div class="h-64 w-full flex items-end gap-2 px-4 relative">
				<div class="absolute inset-0 flex flex-col justify-between opacity-5 pointer-events-none">
					<div class="border-b border-on-surface w-full"></div>
					<div class="border-b border-on-surface w-full"></div>
					<div class="border-b border-on-surface w-full"></div>
					<div class="border-b border-on-surface w-full"></div>
				</div>
				
				{#if mounted}
					{#each historicalHeights as height, i}
						<div 
							class="flex-1 bg-on-tertiary-container/30 rounded-t-sm" 
							style="height: {height}%"
							in:scale={{ duration: 800, delay: 300 + (i * 50), start: 0, easing: cubicOut }}
						></div>
					{/each}
					{#each predictiveHeights as height, i}
						<div 
							class="flex-1 bg-on-tertiary-container/10 rounded-t-sm border-t-2 border-dashed border-on-tertiary-container" 
							style="height: {height}%"
							in:scale={{ duration: 800, delay: 600 + (i * 50), start: 0, easing: cubicOut }}
						></div>
					{/each}
				{/if}
			</div>

			<div class="flex justify-between mt-4 px-4 text-label-caps text-on-surface-variant">
				<span>ENE</span><span>FEB</span><span>MAR</span><span>ABR</span><span>MAY</span><span>JUN</span>
				<span class="text-on-tertiary-container font-bold">HOY</span>
				<span>JUL</span><span>AGO</span><span>SEP</span><span>OCT</span><span>NOV</span><span>DIC</span>
			</div>
		</GlassCard>

		<!-- AI Insights -->
		<GlassCard span={4} variant="primary" class="relative overflow-hidden ai-insights" style="--shimmer-op: {shimmerOpacity}">
			<div class="ai-shimmer absolute inset-0 pointer-events-none"></div>
			<div class="relative z-10">
				<div class="flex items-center gap-3 mb-4">
					<img alt="SIGA AI" class="w-8 h-8 object-contain" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCgqmPIXGsiBSrApZZwXxKwKT6KBCx7ELWEYb16yly6jkucaoBQaMhbP44yJZ1wUvtfWknVnO2Y43QEd52uIYMaROxA4fxAhPfD4TFHCtxxPGOeiyMxMXjTLuNvhHwPJyO0Kd30XK0K67sI_RPjV6rASewOt9uz2q1e7sTIYjmBV3J1KLjt9PuU-G3R3_hHV9G6L5gRpImlNfAuQL0qGANJSGHEqufd6hRNPUd0yg_s1Nnop7T4saM9yjSDPWCVmn48E35twRYJaI8F" />
					<h3 class="text-headline-sm">Insights de SIGA</h3>
				</div>

				<div class="space-y-4">
					<div class="bg-white/5 p-3 rounded-lg border border-white/10">
						<p class="text-body-md text-on-primary-container italic font-medium leading-relaxed">
							"{aiInsights.narrative}"
						</p>
					</div>
					
					<div class="flex flex-col gap-3">
						{#each aiInsights.tips as tip}
							<div class="flex items-start gap-3">
								<span class="material-symbols-outlined text-secondary-container text-sm mt-1">{tip.icon}</span>
								<div>
									<p class="text-sm font-bold">{tip.title}</p>
									<p class="text-xs text-on-primary-container">{tip.desc}</p>
								</div>
							</div>
						{/each}
					</div>
				</div>
			</div>
		</GlassCard>

		<!-- Critical Stock Table -->
		<GlassCard span={12} padding="none" class="border-outline-variant/30">
			<div class="p-card-padding flex justify-between items-center border-b border-outline-variant/30">
				<div class="flex items-center gap-3">
					<span class="material-symbols-outlined text-error" style="font-variation-settings: 'FILL' 1;">inventory</span>
					<h3 class="text-headline-sm">Stock Crítico & Sugerencias de Reposición</h3>
				</div>
				<a href="/products" class="text-on-tertiary-container font-bold text-sm hover:underline flex items-center gap-1">
					Ver todo el inventario
					<span class="material-symbols-outlined text-sm">arrow_forward</span>
				</a>
			</div>

			<div class="overflow-x-auto">
				<table class="w-full text-left">
					<thead>
						<tr class="bg-surface-container-low text-label-caps text-on-surface-variant">
							<th class="px-gutter py-4">Producto / SKU</th>
							<th class="px-gutter py-4 text-center">Stock Actual</th>
							<th class="px-gutter py-4 text-center">Tasa de Venta (Sem)</th>
							<th class="px-gutter py-4 text-center">Sugerencia IA</th>
							<th class="px-gutter py-4 text-right">Acción Recomendada</th>
						</tr>
					</thead>
					<tbody class="divide-y divide-outline-variant/20">
						{#each criticalStock as item}
							<tr class="hover:bg-surface-container-low transition-colors group">
								<td class="px-gutter py-row-padding">
									<div class="flex items-center gap-4">
										<div class="w-12 h-12 rounded-lg bg-surface-container overflow-hidden border border-outline-variant/30">
											<img src={item.image} alt={item.name} class="w-full h-full object-cover" />
										</div>
										<div>
											<p class="font-bold text-on-surface">{item.name}</p>
											<p class="text-xs text-on-surface-variant">ID: {item.id}</p>
										</div>
									</div>
								</td>
								<td class="px-gutter py-row-padding text-center">
									<Badge variant={item.current < 5 ? 'danger' : 'warning'}>{item.current} u.</Badge>
								</td>
								<td class="px-gutter py-row-padding text-center text-on-surface-variant">
									{item.rate} u. <span class="trend {item.trend > 0 ? 'text-success' : 'text-error'} text-[10px] font-bold">{item.trend > 0 ? '↑' : '↓'} {Math.abs(item.trend)}%</span>
								</td>
								<td class="px-gutter py-row-padding text-center">
									<span class="text-on-tertiary-container font-bold">+{item.suggestion} u.</span>
								</td>
								<td class="px-gutter py-row-padding text-right">
									<button class="px-4 py-2 bg-on-tertiary-container text-on-tertiary rounded-lg text-xs font-bold hover:opacity-90 active:scale-95 transition-all shadow-sm">Auto-Reponer</button>
								</td>
							</tr>
						{:else}
							<tr>
								<td colspan="5" class="px-gutter py-12 text-center text-on-surface-variant">
									No se detectó stock crítico en este momento.
								</td>
							</tr>
						{/each}
					</tbody>
				</table>
			</div>
		</GlassCard>

		<!-- KPI Cards -->
		<GlassCard span={4} class="border-outline-variant/30">
			<div class="flex justify-between items-start mb-4">
				<div>
					<p class="text-on-surface-variant text-label-caps mb-1">Confianza del Modelo</p>
					<h4 class="text-headline-sm font-bold">98.4%</h4>
				</div>
				<div class="w-10 h-10 bg-on-tertiary-container/10 text-on-tertiary-container rounded-full flex items-center justify-center">
					<span class="material-symbols-outlined">verified</span>
				</div>
			</div>
			<div class="w-full bg-surface-container rounded-full h-2 overflow-hidden">
				<div class="bg-on-tertiary-container h-full" style="width: 98.4%"></div>
			</div>
			<p class="text-[10px] mt-2 text-on-surface-variant italic">Basado en datos de SIGA de los últimos 24 meses.</p>
		</GlassCard>

		<GlassCard span={4} class="border-l-4 border-on-tertiary-container border-outline-variant/30">
			<div class="flex justify-between items-start mb-4">
				<div>
					<p class="text-on-surface-variant text-label-caps mb-1">Impacto Financiero</p>
					<h4 class="text-headline-sm font-bold text-on-tertiary-container">+$12,450</h4>
				</div>
				<div class="w-10 h-10 bg-on-tertiary-container/10 text-on-tertiary-container rounded-full flex items-center justify-center">
					<span class="material-symbols-outlined">payments</span>
				</div>
			</div>
			<p class="text-body-md text-on-surface-variant">Ahorro proyectado por optimización de inventario este mes.</p>
		</GlassCard>

		<GlassCard span={4} class="border-outline-variant/30">
			<div class="flex justify-between items-start mb-4">
				<div>
					<p class="text-on-surface-variant text-label-caps mb-1">Alertas IA activas</p>
					<h4 class="text-headline-sm font-bold text-on-error-container">03</h4>
				</div>
				<div class="w-10 h-10 bg-error-container/20 text-error rounded-full flex items-center justify-center">
					<span class="material-symbols-outlined">notification_important</span>
				</div>
			</div>
			<div class="flex gap-2">
				<span class="w-2 h-2 rounded-full bg-error animate-pulse"></span>
				<span class="w-2 h-2 rounded-full bg-error opacity-50"></span>
				<span class="w-2 h-2 rounded-full bg-error opacity-20"></span>
			</div>
		</GlassCard>
	</BentoGrid>
</div>

<style>
	.ai-insights {
		--shimmer-op: 0.1;
	}

	.text-label-caps { font-size: var(--font-size-label-caps); font-weight: var(--font-weight-medium); line-height: 16px; letter-spacing: 0.05em; text-transform: uppercase; font-family: var(--font-mono); }
</style>

