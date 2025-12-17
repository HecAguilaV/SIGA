import { writable } from 'svelte/store';

function createUiStore() {
    const { subscribe, update, set } = writable({
        isSidebarOpen: true, // Por defecto abierto en desktop
        isMobile: false
    });

    return {
        subscribe,
        toggleSidebar: () => update(s => ({ ...s, isSidebarOpen: !s.isSidebarOpen })),
        closeSidebar: () => update(s => ({ ...s, isSidebarOpen: false })),
        openSidebar: () => update(s => ({ ...s, isSidebarOpen: true })),
        setMobile: (isMobile) => update(s => ({
            ...s,
            isMobile,
            isSidebarOpen: !isMobile // Cerrado por defecto en móvil, abierto en desktop
        }))
    };
}

export const uiStore = createUiStore();
