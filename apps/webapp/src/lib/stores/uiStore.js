import { writable } from 'svelte/store';
import { browser } from '$app/environment';

const defaultA11y = {
    theme: 'auto', // 'light', 'dark', 'auto'
    fontSize: 100, // percentage
    grayscale: false,
    highContrast: false,
    negativeContrast: false,
    linksUnderline: false,
    readableFont: false,
    lightBackground: false
};

function createUiStore() {
    // Cargar estado inicial desde localStorage si estamos en el browser
    let initialA11y = defaultA11y;
    if (browser) {
        const stored = localStorage.getItem('siga_a11y');
        if (stored) {
            try {
                initialA11y = { ...defaultA11y, ...JSON.parse(stored) };
            } catch(e) {}
        }
    }

    const { subscribe, update, set } = writable({
        isSidebarOpen: true,
        isMobile: false,
        a11y: initialA11y
    });

    const updateA11y = (updater) => {
        update(s => {
            const nextA11y = typeof updater === 'function' ? updater(s.a11y) : { ...s.a11y, ...updater };
            if (browser) {
                localStorage.setItem('siga_a11y', JSON.stringify(nextA11y));
            }
            return { ...s, a11y: nextA11y };
        });
    };

    return {
        subscribe,
        toggleSidebar: () => update(s => ({ ...s, isSidebarOpen: !s.isSidebarOpen })),
        closeSidebar: () => update(s => ({ ...s, isSidebarOpen: false })),
        openSidebar: () => update(s => ({ ...s, isSidebarOpen: true })),
        setMobile: (isMobile) => update(s => ({
            ...s,
            isMobile,
            isSidebarOpen: !isMobile
        })),
        
        // A11y Actions
        setTheme: (theme) => updateA11y({ theme }),
        setFontSize: (size) => updateA11y({ fontSize: size }),
        toggleGrayscale: () => updateA11y((a) => ({ ...a, grayscale: !a.grayscale })),
        toggleHighContrast: () => updateA11y((a) => ({ ...a, highContrast: !a.highContrast })),
        toggleNegativeContrast: () => updateA11y((a) => ({ ...a, negativeContrast: !a.negativeContrast })),
        toggleLinksUnderline: () => updateA11y((a) => ({ ...a, linksUnderline: !a.linksUnderline })),
        toggleReadableFont: () => updateA11y((a) => ({ ...a, readableFont: !a.readableFont })),
        toggleLightBackground: () => updateA11y((a) => ({ ...a, lightBackground: !a.lightBackground })),
        resetA11y: () => updateA11y(defaultA11y)
    };
}

export const uiStore = createUiStore();
