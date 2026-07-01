import '@testing-library/jest-dom';

// Mock window.matchMedia for jsdom (used by theme store)
if (typeof window !== 'undefined' && !window.matchMedia) {
	Object.defineProperty(window, 'matchMedia', {
		writable: true,
		value: (query: string) => ({
			matches: false,
			media: query,
			onchange: null,
			addListener: () => {},
			removeListener: () => {},
			addEventListener: () => {},
			removeEventListener: () => {},
			dispatchEvent: () => false
		})
	});
}

// Node 26 removed the global `localStorage` from the default runtime unless
// --localstorage-file is passed. JSDOM keeps its own `window.localStorage`,
// but tests reference the global identifier, so we mirror it explicitly.
if (typeof window !== 'undefined' && typeof localStorage === 'undefined') {
	const storage = (() => {
		const map = new Map<string, string>();
		return {
			getItem: (k: string) => (map.has(k) ? (map.get(k) as string) : null),
			setItem: (k: string, v: string) => {
				map.set(k, String(v));
			},
			removeItem: (k: string) => {
				map.delete(k);
			},
			clear: () => {
				map.clear();
			},
			key: (i: number) => Array.from(map.keys())[i] ?? null,
			get length() {
				return map.size;
			}
		};
	})();
	Object.defineProperty(globalThis, 'localStorage', {
		value: storage,
		writable: true,
		configurable: true
	});
}
