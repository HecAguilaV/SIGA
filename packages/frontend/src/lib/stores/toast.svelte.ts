import { writable } from 'svelte/store';

export interface Toast {
	id: string;
	type: 'success' | 'error' | 'info' | 'warning';
	message: string;
	duration?: number;
	autoDismiss?: boolean;
}

function createToastStore() {
	const { subscribe, update } = writable<Toast[]>([]);

	let counter = 0;

	function add(toast: Omit<Toast, 'id'>): string {
		const id = `toast-${++counter}-${Date.now()}`;
		const newToast: Toast = {
			...toast,
			id,
			autoDismiss: toast.autoDismiss ?? true,
			duration: toast.duration ?? 5000
		};

		update((toasts) => [...toasts, newToast]);

		if (newToast.autoDismiss && newToast.duration && newToast.duration > 0) {
			setTimeout(() => {
				remove(id);
			}, newToast.duration);
		}

		return id;
	}

	function remove(id: string) {
		update((toasts) => toasts.filter((t) => t.id !== id));
	}

	function clear() {
		update(() => []);
	}

	return {
		subscribe,
		add,
		remove,
		clear
	};
}

export const toast = createToastStore();
