import { writable } from 'svelte/store';
import { browser } from '$app/environment';

// Definir interfase de usuario para referencia
/**
 * @typedef {Object} User
 * @property {number} id
 * @property {string} email
 * @property {string} nombre
 * @property {string} apellido
 * @property {string} rol
 * @property {string} [nombreEmpresa]
 */

// Estado inicial vacío
const initialState = {
    isAuthenticated: false,
    user: null,
    token: null,
    refreshToken: null
};

// Recuperar estado de localStorage si existe
const storedState = browser ? JSON.parse(localStorage.getItem('siga_auth') || 'null') : null;

export const authStore = writable(storedState || initialState);

// Suscribirse a cambios para persistir en localStorage
if (browser) {
    authStore.subscribe(value => {
        if (value.isAuthenticated) {
            localStorage.setItem('siga_auth', JSON.stringify(value));
        } else {
            localStorage.removeItem('siga_auth');
        }
    });
}

export const loginUser = (data) => {
    authStore.set({
        isAuthenticated: true,
        user: data.user,
        token: data.accessToken,
        refreshToken: data.refreshToken
    });
};

export const logoutUser = () => {
    authStore.set(initialState);
    if (browser) {
        localStorage.removeItem('siga_auth');
        window.location.href = '/login';
    }
};
