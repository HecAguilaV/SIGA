<script lang="ts">
	import Eye from 'phosphor-svelte/lib/Eye';
	import TextAa from 'phosphor-svelte/lib/TextAa';
	import LinkSimpleHorizontal from 'phosphor-svelte/lib/LinkSimpleHorizontal';
	import SunHorizon from 'phosphor-svelte/lib/SunHorizon';
	import ArrowCounterClockwise from 'phosphor-svelte/lib/ArrowCounterClockwise';

	const STORAGE_KEY = 'siga-a11y-prefs';

	interface A11yPrefs {
		highContrast: boolean;
		grayscale: boolean;
		largeFont: boolean;
		underlineLinks: boolean;
	}

	let prefs = $state<A11yPrefs>(loadPrefs());

	function loadPrefs(): A11yPrefs {
		if (typeof window === 'undefined') {
			return { highContrast: false, grayscale: false, largeFont: false, underlineLinks: false };
		}
		try {
			const stored = localStorage.getItem(STORAGE_KEY);
			if (stored) return JSON.parse(stored);
		} catch {
			// ignore
		}
		return { highContrast: false, grayscale: false, largeFont: false, underlineLinks: false };
	}

	function savePrefs() {
		if (typeof window === 'undefined') return;
		localStorage.setItem(STORAGE_KEY, JSON.stringify(prefs));
		applyPrefs();
	}

	function applyPrefs() {
		const root = document.documentElement;
		root.classList.toggle('a11y-high-contrast', prefs.highContrast);
		root.classList.toggle('a11y-grayscale', prefs.grayscale);
		root.classList.toggle('a11y-large-font', prefs.largeFont);
		root.classList.toggle('a11y-underline-links', prefs.underlineLinks);
	}

	function toggle(key: keyof A11yPrefs) {
		prefs[key] = !prefs[key];
		savePrefs();
	}

	function resetPrefs() {
		prefs = { highContrast: false, grayscale: false, largeFont: false, underlineLinks: false };
		savePrefs();
	}

	// Apply on mount
	$effect(() => {
		applyPrefs();
	});

	const items: { key: keyof A11yPrefs; label: string; icon: typeof Eye; title: string }[] = [
		{ key: 'highContrast', label: 'HC', icon: Eye, title: 'Alto contraste' },
		{ key: 'grayscale', label: 'G', icon: SunHorizon, title: 'Escala de grises' },
		{ key: 'largeFont', label: 'A', icon: TextAa, title: 'Fuente grande' },
		{ key: 'underlineLinks', label: 'U', icon: LinkSimpleHorizontal, title: 'Subrayar enlaces' }
	];
</script>

<div class="a11y-toolbar" role="toolbar" aria-label="Opciones de accesibilidad">
	{#each items as item}
		<button
			class="a11y-btn"
			class:active={prefs[item.key]}
			onclick={() => toggle(item.key)}
			aria-pressed={prefs[item.key]}
			aria-label={item.title}
			type="button"
		>
			{#if item.icon === Eye}
				<Eye size={16} weight={prefs[item.key] ? 'fill' : 'regular'} />
			{:else if item.icon === TextAa}
				<TextAa size={16} weight={prefs[item.key] ? 'fill' : 'regular'} />
			{:else if item.icon === LinkSimpleHorizontal}
				<LinkSimpleHorizontal size={16} weight={prefs[item.key] ? 'fill' : 'regular'} />
			{:else if item.icon === SunHorizon}
				<SunHorizon size={16} weight={prefs[item.key] ? 'fill' : 'regular'} />
			{/if}
		</button>
	{/each}
	<span class="a11y-separator" aria-hidden="true"></span>
	<button
		class="a11y-btn a11y-reset"
		onclick={resetPrefs}
		aria-label="Restablecer opciones de accesibilidad"
		title="Restablecer"
		type="button"
	>
		<ArrowCounterClockwise size={16} weight="regular" />
	</button>
</div>

<style>
	.a11y-toolbar {
		display: flex;
		align-items: center;
		gap: 2px;
	}

	.a11y-btn {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		width: 32px;
		height: 32px;
		border: 1px solid transparent;
		background: transparent;
		color: var(--color-text-muted);
		border-radius: var(--radius-sm);
		cursor: pointer;
		transition: all var(--transition-fast);
	}

	.a11y-btn:hover {
		background: var(--color-bg-alt);
		color: var(--color-text);
		border-color: var(--color-border);
	}

	.a11y-btn.active {
		background: var(--color-accent-light);
		color: var(--color-accent);
		border-color: var(--color-accent);
	}

	.a11y-separator {
		width: 1px;
		height: 20px;
		background: var(--color-border);
		margin: 0 4px;
	}

	.a11y-reset:hover {
		color: var(--color-error);
		border-color: var(--color-error);
	}
</style>
