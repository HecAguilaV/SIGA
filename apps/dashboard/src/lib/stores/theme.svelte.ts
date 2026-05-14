import { writable } from 'svelte/store';

export type Theme = 'light' | 'dark';

function createThemeStore() {
	const stored = getStoredTheme();
	const prefersDark = typeof window !== 'undefined' && window.matchMedia('(prefers-color-scheme: dark)').matches;
	const initial: Theme = stored ?? (prefersDark ? 'dark' : 'light');

	const { subscribe, set, update } = writable<Theme>(initial);

	applyTheme(initial);

	if (typeof window !== 'undefined') {
		const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
		mediaQuery.addEventListener('change', (e) => {
			const stored = getStoredTheme();
			if (!stored) {
				const newTheme: Theme = e.matches ? 'dark' : 'light';
				set(newTheme);
				applyTheme(newTheme);
			}
		});
	}

	function getStoredTheme(): Theme | null {
		try {
			const val = localStorage.getItem('siga-theme');
			if (val === 'light' || val === 'dark') return val;
			return null;
		} catch {
			return null;
		}
	}

	function storeTheme(theme: Theme) {
		try {
			localStorage.setItem('siga-theme', theme);
		} catch {
			// localStorage not available (incognito, blocked policy)
		}
	}

	function applyTheme(theme: Theme) {
		if (typeof document !== 'undefined') {
			document.documentElement.setAttribute('data-theme', theme);
		}
	}

	return {
		subscribe,
		set(value: Theme) {
			set(value);
			storeTheme(value);
			applyTheme(value);
		},
		update(updater: (t: Theme) => Theme) {
			update((current) => {
				const next = updater(current);
				storeTheme(next);
				applyTheme(next);
				return next;
			});
		},
		toggle() {
			update((current) => {
				const next: Theme = current === 'light' ? 'dark' : 'light';
				storeTheme(next);
				applyTheme(next);
				return next;
			});
		},
		init() {
			const t = getStoredTheme();
			const prefersDark = typeof window !== 'undefined' && window.matchMedia('(prefers-color-scheme: dark)').matches;
			const theme: Theme = t ?? (prefersDark ? 'dark' : 'light');
			set(theme);
			storeTheme(theme);
			applyTheme(theme);
		}
	};
}

export const theme = createThemeStore();
