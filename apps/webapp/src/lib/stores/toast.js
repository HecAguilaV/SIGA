import { writable } from 'svelte/store';

function createToastStore() {
    const { subscribe, update } = writable([]);

    return {
        subscribe,
        /**
         * @param {string} message - El mensaje a mostrar
         * @param {'success'|'warning'|'error'|'info'} type - Tipo de notificación
         * @param {number} duration - Duración en ms (default 3000)
         */
        add: (message, type = 'info', duration = 3000) => {
            const id = crypto.randomUUID();
            const toast = { id, message, type, duration };
            update(toasts => [...toasts, toast]);

            if (duration > 0) {
                setTimeout(() => {
                    update(toasts => toasts.filter(t => t.id !== id));
                }, duration);
            }
        },
        remove: (id) => {
            update(toasts => toasts.filter(t => t.id !== id));
        }
    };
}

export const toast = createToastStore();
