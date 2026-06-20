<script lang="ts">
	import './predictive.css';
	import { fade, fly, scale } from 'svelte/transition';
	import { cubicOut } from 'svelte/easing';
	import Badge from '@siga/ui-kit/Badge.svelte';
	import Button from '@siga/ui-kit/Button.svelte';
	import Spinner from '@siga/ui-kit/Spinner.svelte';

	// Icons
	import Sparkle from 'phosphor-svelte/lib/Sparkle';
	import TrendUp from 'phosphor-svelte/lib/TrendUp';
	import TrendDown from 'phosphor-svelte/lib/TrendDown';
	import Inventory from 'phosphor-svelte/lib/Package';
	import Lightning from 'phosphor-svelte/lib/Lightning';
	import Warning from 'phosphor-svelte/lib/Warning';
	import ArrowRight from 'phosphor-svelte/lib/ArrowRight';
	import Calendar from 'phosphor-svelte/lib/Calendar';
	import ArrowsCounterClockwise from 'phosphor-svelte/lib/ArrowsCounterClockwise';
	import Verified from 'phosphor-svelte/lib/SealCheck';
	import CurrencyDollar from 'phosphor-svelte/lib/CurrencyDollar';
	import BellSimple from 'phosphor-svelte/lib/BellSimple';

	let { data }: { data: any } = $props();

	const criticalStock = $derived(data.criticalStock || []);
	const historicalSales = $derived(data.historicalSales || []);
	const aiInsights = $derived(data.aiInsights || { narrative: '', tips: [] });

	let isRecalculating = $state(false);

	async function recalculateModels() {
		isRecalculating = true;
		// Simular recalculo real llamando al agente
		await new Promise(r => setTimeout(r, 2000));
		isRecalculating = false;
	}
</script>

<svelte:head>
	<title>SIGA — Análisis Predictivo</title>
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@300;400;500;700;800&display=swap" rel="stylesheet">
</svelte:head>

<div class="predictive-scope" in:fade={{ duration: 400 }}>
	<!-- Header Section -->
	<div class="flex flex-col md:flex-row justify-between items-start md:items-end mb-8 gap-4">
		<div>
			<h2 class="text-4xl font-extrabold text-black mb-2">Análisis Predictivo de Ventas</h2>
			<p class="text-slate-500 text-lg">Proyecciones inteligentes y gestión automatizada de suministros impulsada por la inteligencia de SIGA.</p>
		</div>
		<div class="flex gap-3">
			<button class="flex items-center gap-2 px-4 py-2 rounded-lg bg-white shadow-sm text-slate-600 border border-slate-200 hover:bg-slate-50 transition-all">
				<Calendar size={20} />
				<span>Próximos 30 días</span>
			</button>
			<button 
				class="flex items-center gap-2 px-4 py-2 rounded-lg bg-[#009579] text-white shadow-lg hover:opacity-90 transition-all disabled:opacity-50"
				onclick={recalculateModels}
				disabled={isRecalculating}
			>
				<ArrowsCounterClockwise size={20} class={isRecalculating ? 'animate-spin' : ''} />
				<span>{isRecalculating ? 'Recalculando...' : 'Recalcular Modelos'}</span>
			</button>
		</div>
	</div>

	<!-- Bento Grid Layout -->
	<div class="grid grid-cols-12 gap-6">
		<!-- Time Series Chart (Main) -->
		<div class="col-span-12 lg:col-span-8 bg-white rounded-2xl p-6 shadow-sm relative overflow-hidden border border-slate-100">
			<div class="flex justify-between items-center mb-6">
				<h3 class="text-xl font-bold flex items-center gap-2 text-slate-800">
					<TrendUp size={24} class="text-[#009579]" />
					Proyección de Demanda vs. Ventas Históricas
				</h3>
				<div class="flex items-center gap-4 text-xs font-bold text-slate-500">
					<span class="flex items-center gap-1"><span class="w-3 h-3 rounded-full bg-[#009579]"></span> Histórico</span>
					<span class="flex items-center gap-1"><span class="w-3 h-3 rounded-full bg-[#009579]/30"></span> Predictivo</span>
				</div>
			</div>
			
			<!-- Visual Graph Representation -->
			<div class="h-64 w-full flex items-end gap-2 px-4 relative">
				<div class="absolute inset-0 flex flex-col justify-between opacity-5 pointer-events-none">
					<div class="border-b border-black w-full"></div>
					<div class="border-b border-black w-full"></div>
					<div class="border-b border-black w-full"></div>
					<div class="border-b border-black w-full"></div>
				</div>
				
				{#each historicalSales.slice(-6) as sale}
					<div 
						class="flex-1 bg-[#009579]/40 rounded-t-sm" 
						style="height: {Math.min(90, (sale.total / 1000) * 100)}%"
					></div>
				{/each}
				
				<!-- Transition to Prediction (Dashed) -->
				{#each Array(6) as _, i}
					<div 
						class="flex-1 bg-[#009579]/10 rounded-t-sm border-t-2 border-dashed border-[#009579]" 
						style="height: {60 + Math.sin(i) * 20}%"
					></div>
				{/each}
			</div>
			<div class="flex justify-between mt-4 px-4 text-[10px] font-bold text-slate-400">
				<span>ENE</span><span>FEB</span><span>MAR</span><span>ABR</span><span>MAY</span><span>JUN</span>
				<span class="text-[#009579] font-black">HOY</span>
				<span>JUL</span><span>AGO</span><span>SEP</span><span>OCT</span><span>NOV</span><span>DIC</span>
			</div>
		</div>

		<!-- Agent Insights (Side) -->
		<div class="col-span-12 lg:col-span-4 flex flex-col gap-6">
			<div class="bg-[#070a61] text-white rounded-2xl p-6 shadow-md flex-1 relative overflow-hidden">
				<div class="ai-shimmer absolute inset-0 pointer-events-none"></div>
				<div class="relative z-10">
					<div class="flex items-center gap-3 mb-4">
						<img alt="SIGA AI" class="w-8 h-8 object-contain" src="/S.png" />
						<h3 class="text-xl font-bold">Insights de SIGA</h3>
					</div>
					<div class="space-y-4">
						<div class="bg-white/5 p-3 rounded-lg border border-white/10">
							<p class="text-sm italic font-medium leading-relaxed text-[#bfc2ff]">
								"{aiInsights.narrative || 'He detectado un patrón de demanda atípico en la categoría \'Electrónica\'. Se recomienda aumentar el stock en un 15% para el próximo trimestre.'}"
							</p>
						</div>
						<div class="flex flex-col gap-4">
							{#each aiInsights.tips as tip}
								<div class="flex items-start gap-3">
									<div class="mt-1 text-[#e0e0ff]">
										{#if tip.icon === 'bolt'} <Lightning size={18} /> {:else} <Warning size={18} /> {/if}
									</div>
									<div>
										<p class="text-sm font-bold">{tip.title}</p>
										<p class="text-xs text-[#777dcf]">{tip.desc}</p>
									</div>
								</div>
							{/each}
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- Stock Crítico Section -->
		<div class="col-span-12 bg-white rounded-2xl shadow-sm overflow-hidden flex flex-col border border-slate-100">
			<div class="p-6 flex justify-between items-center border-b border-slate-50">
				<div class="flex items-center gap-3">
					<Inventory size={24} weight="fill" class="text-[#ba1a1a]" />
					<h3 class="text-xl font-bold text-slate-800">Stock Crítico & Sugerencias de Reposición</h3>
				</div>
				<a href="/products" class="text-[#009579] font-bold text-sm hover:underline flex items-center gap-1">
					Ver todo el inventario
					<ArrowRight size={16} />
				</a>
			</div>
			<div class="overflow-x-auto">
				<table class="w-full text-left">
					<thead>
						<tr class="bg-slate-50 text-[10px] font-black text-slate-400 uppercase tracking-widest">
							<th class="px-6 py-4">Producto / SKU</th>
							<th class="px-6 py-4 text-center">Stock Actual</th>
							<th class="px-6 py-4 text-center">Tasa de Venta (Sem)</th>
							<th class="px-6 py-4 text-center">Sugerencia IA</th>
							<th class="px-6 py-4 text-right">Acción Recomendada</th>
						</tr>
					</thead>
					<tbody class="divide-y divide-slate-100">
						{#each criticalStock as item}
							<tr class="hover:bg-slate-50 transition-colors group">
								<td class="px-6 py-4">
									<div class="flex items-center gap-4">
										<div class="w-12 h-12 rounded-lg bg-slate-50 overflow-hidden border border-slate-200">
											<img class="w-full h-full object-cover" src={item.image || '/S.png'} alt={item.name} />
										</div>
										<div>
											<p class="font-bold text-slate-800">{item.name}</p>
											<p class="text-xs text-slate-400">ID: {item.id}</p>
										</div>
									</div>
								</td>
								<td class="px-6 py-4 text-center">
									<span class="px-3 py-1 rounded-full bg-[#ffdad6] text-[#93000a] text-xs font-bold">{item.current} u.</span>
								</td>
								<td class="px-6 py-4 text-center text-slate-500 font-medium">
									{item.rate} u. <span class="text-[#009579] text-[10px] font-bold">{#if item.trend > 0}<TrendUp size={12} weight="bold" aria-hidden="true" />{:else}<TrendDown size={12} weight="bold" aria-hidden="true" />{/if} {Math.abs(item.trend)}%</span>
								</td>
								<td class="px-6 py-4 text-center">
									<span class="text-[#009579] font-bold">+{item.suggestion} u.</span>
								</td>
								<td class="px-6 py-4 text-right">
									<button class="px-4 py-2 bg-[#009579] text-white rounded-lg text-xs font-bold hover:opacity-90 active:scale-95 transition-all shadow-sm">
										Auto-Reponer
									</button>
								</td>
							</tr>
						{/each}
					</tbody>
				</table>
			</div>
		</div>

		<!-- Secondary Analysis Cards -->
		<div class="col-span-12 md:col-span-6 lg:col-span-4 bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
			<div class="flex justify-between items-start mb-4">
				<div>
					<p class="text-slate-400 text-[10px] font-black uppercase mb-1">Confianza del Modelo</p>
					<h4 class="text-2xl font-black text-slate-800">98.4%</h4>
				</div>
				<div class="w-10 h-10 bg-[#009579]/10 text-[#009579] rounded-full flex items-center justify-center">
					<Verified size={24} weight="duotone" />
				</div>
			</div>
			<div class="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
				<div class="bg-[#009579] h-full" style="width: 98.4%"></div>
			</div>
			<p class="text-[10px] mt-2 text-slate-400 italic">Basado en datos de SIGA de los últimos 24 meses.</p>
		</div>

		<div class="col-span-12 md:col-span-6 lg:col-span-4 bg-white rounded-2xl p-6 shadow-sm border-l-4 border-[#009579] border border-slate-100">
			<div class="flex justify-between items-start mb-4">
				<div>
					<p class="text-slate-400 text-[10px] font-black uppercase mb-1">Impacto Financiero</p>
					<h4 class="text-2xl font-black text-[#009579]">+$12,450</h4>
				</div>
				<div class="w-10 h-10 bg-[#009579]/10 text-[#009579] rounded-full flex items-center justify-center">
					<CurrencyDollar size={24} weight="duotone" />
				</div>
			</div>
			<p class="text-sm text-slate-500">Ahorro proyectado por optimización de inventario este mes.</p>
		</div>

		<div class="col-span-12 md:col-span-6 lg:col-span-4 bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
			<div class="flex justify-between items-start mb-4">
				<div>
					<p class="text-slate-400 text-[10px] font-black uppercase mb-1">Alertas IA activas</p>
					<h4 class="text-2xl font-black text-[#ba1a1a]">03</h4>
				</div>
				<div class="w-10 h-10 bg-red-50 text-[#ba1a1a] rounded-full flex items-center justify-center">
					<BellSimple size={24} weight="duotone" />
				</div>
			</div>
			<div class="flex gap-2">
				<span class="w-2 h-2 rounded-full bg-red-500 animate-pulse"></span>
				<span class="w-2 h-2 rounded-full bg-red-500 opacity-50"></span>
				<span class="w-2 h-2 rounded-full bg-red-500 opacity-20"></span>
			</div>
		</div>
	</div>
</div>

<style>
	:global(.predictive-scope) {
		max-width: 1440px;
		margin: 0 auto;
		padding: 32px;
	}
</style>
