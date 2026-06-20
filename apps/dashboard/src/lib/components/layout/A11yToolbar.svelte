<script lang="ts">
	/**
	 * A11yToolbar.svelte — Expandable dropdown for accessibility preferences.
	 *
	 * Persists user choices to localStorage and toggles CSS classes on <html>
	 * (see the .a11y-* blocks in app.css). Renders as a compact trigger that
	 * opens a panel with the 4 toggle options and a reset action.
	 */
	import Wheelchair from 'phosphor-svelte/lib/Wheelchair';
	import CaretDown from 'phosphor-svelte/lib/CaretDown';
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
	let isOpen = $state(false);
	let dropdownEl: HTMLElement | null = $state(null);

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

	function toggleOpen() {
		isOpen = !isOpen;
	}

	function closePanel() {
		isOpen = false;
	}

	function handleWindowClick(event: MouseEvent) {
		if (isOpen && dropdownEl && !dropdownEl.contains(event.target as Node)) {
			isOpen = false;
		}
	}

	function handleKeydown(event: KeyboardEvent) {
		if (isOpen && event.key === 'Escape') {
			isOpen = false;
		}
	}

	// Apply on mount
	$effect(() => {
		applyPrefs();
	});

	const items: { key: keyof A11yPrefs; label: string; icon: typeof Eye; title: string }[] = [
		{ key: 'highContrast', label: 'Alto contraste', icon: Eye, title: 'Alto contraste' },
		{ key: 'grayscale', label: 'Escala de grises', icon: SunHorizon, title: 'Escala de grises' },
		{ key: 'largeFont', label: 'Fuente grande', icon: TextAa, title: 'Fuente grande' },
		{ key: 'underlineLinks', label: 'Subrayar enlaces', icon: LinkSimpleHorizontal, title: 'Subrayar enlaces' }
	];
</script>

<svelte:window onclick={handleWindowClick} onkeydown={handleKeydown} />

<div class="a11y-dropdown" class:open={isOpen} bind:this={dropdownEl}>
	<button
		class="a11y-trigger"
		onclick={toggleOpen}
		aria-label="Opciones de accesibilidad"
		aria-expanded={isOpen}
		aria-haspopup="menu"
		type="button"
	>
		<Wheelchair size={20} />
		<span class="caret" class:rotated={isOpen}>
			<CaretDown size={12} />
		</span>
	</button>

	{#if isOpen}
		<div class="a11y-panel" role="menu" aria-label="Opciones de accesibilidad">
			{#each items as item}
				<button
					class="a11y-item"
					class:active={prefs[item.key]}
					onclick={() => toggle(item.key)}
					role="menuitemcheckbox"
					aria-checked={prefs[item.key]}
					aria-label={item.title}
					type="button"
				>
					<span class="a11y-item-icon">
						{#if item.icon === Eye}
							<Eye size={18} weight={prefs[item.key] ? 'fill' : 'regular'} />
						{:else if item.icon === TextAa}
							<TextAa size={18} weight={prefs[item.key] ? 'fill' : 'regular'} />
						{:else if item.icon === LinkSimpleHorizontal}
							<LinkSimpleHorizontal size={18} weight={prefs[item.key] ? 'fill' : 'regular'} />
						{:else if item.icon === SunHorizon}
							<SunHorizon size={18} weight={prefs[item.key] ? 'fill' : 'regular'} />
						{/if}
					</span>
					<span class="a11y-item-label">{item.label}</span>
					<span class="a11y-toggle" class:on={prefs[item.key]} aria-hidden="true">
						<span class="a11y-toggle-knob"></span>
					</span>
				</button>
			{/each}

			<div class="a11y-separator" aria-hidden="true"></div>

			<button
				class="a11y-item a11y-reset"
				onclick={() => { resetPrefs(); closePanel(); }}
				role="menuitem"
				aria-label="Restablecer opciones de accesibilidad"
				type="button"
			>
				<span class="a11y-item-icon">
					<ArrowCounterClockwise size={18} />
				</span>
				<span class="a11y-item-label">Restablecer</span>
			</button>
		</div>
	{/if}
</div>

<style>
	.a11y-dropdown {
		position: relative;
		display: inline-flex;
	}

	.a11y-trigger {
		display: inline-flex;
		align-items: center;
		gap: 4px;
		height: 36px;
		padding: 0 8px;
		border: 1px solid var(--color-outline-variant);
		background: var(--color-surface-container-lowest);
		color: var(--color-on-surface);
		border-radius: var(--radius-md);
		cursor: pointer;
		transition: all var(--transition-fast);
	}

	.a11y-trigger:hover {
		background: var(--color-surface-container-high);
		border-color: var(--color-secondary);
	}

	.a11y-trigger:focus-visible {
		outline: 2px solid var(--color-secondary);
		outline-offset: 2px;
	}

	.a11y-trigger .caret {
		display: inline-flex;
		transition: transform var(--transition-fast);
		color: var(--color-on-surface-variant);
	}

	.a11y-trigger .caret.rotated {
		transform: rotate(180deg);
	}

	.a11y-panel {
		position: absolute;
		top: calc(100% + 8px);
		right: 0;
		z-index: 200;
		min-width: 248px;
		padding: 8px;
		background: var(--color-surface-container-lowest);
		border: 1px solid var(--color-outline-variant);
		border-radius: var(--radius-md);
		box-shadow: var(--shadow-lg);
		display: flex;
		flex-direction: column;
		gap: 2px;
	}

	.a11y-item {
		display: flex;
		align-items: center;
		gap: 12px;
		width: 100%;
		padding: 10px 12px;
		border: none;
		background: transparent;
		color: var(--color-on-surface);
		border-radius: var(--radius-sm);
		cursor: pointer;
		font-size: var(--font-size-body-md);
		text-align: left;
		transition: background var(--transition-fast);
	}

	.a11y-item:hover {
		background: var(--color-surface-container-high);
	}

	.a11y-item:focus-visible {
		outline: 2px solid var(--color-secondary);
		outline-offset: -2px;
	}

	.a11y-item.active {
		color: var(--color-secondary);
	}

	.a11y-item-icon {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		flex-shrink: 0;
		color: var(--color-on-surface-variant);
	}

	.a11y-item.active .a11y-item-icon {
		color: var(--color-secondary);
	}

	.a11y-item-label {
		flex: 1;
	}

	/* Toggle switch indicator */
	.a11y-toggle {
		position: relative;
		width: 34px;
		height: 18px;
		border-radius: var(--radius-full);
		background: var(--color-outline-variant);
		flex-shrink: 0;
		transition: background var(--transition-fast);
	}

	.a11y-toggle.on {
		background: var(--color-secondary);
	}

	.a11y-toggle-knob {
		position: absolute;
		top: 2px;
		left: 2px;
		width: 14px;
		height: 14px;
		border-radius: 50%;
		background: var(--color-surface-container-lowest);
		transition: transform var(--transition-fast);
	}

	.a11y-toggle.on .a11y-toggle-knob {
		transform: translateX(16px);
	}

	.a11y-separator {
		height: 1px;
		background: var(--color-outline-variant);
		margin: 4px 0;
	}

	.a11y-reset {
		color: var(--color-on-surface-variant);
	}

	.a11y-reset:hover {
		color: var(--color-error);
	}

	@media (max-width: 768px) {
		.a11y-panel {
			right: auto;
			left: 0;
		}
	}
</style>
