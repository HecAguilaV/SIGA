import { get } from 'svelte/store';
import { authStore, logoutUser } from '../stores/authStore';
// En desarrollo usamos el proxy de vite, en producción debería ser la URL real
const API_BASE_URL = '';

/**
 * Wrapper for fetch requests with automatic auth token injection
 */
async function request(endpoint, options = {}) {
    const store = get(authStore);
    const token = store.token;

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers
    };

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        // Manejar 401 Unauthorized (Token expirado o inválido)
        if (response.status === 401) {
            // Si no estamos ya en el login, hacer logout
            if (!window.location.pathname.includes('/login')) {
                logoutUser();
            }
            throw new Error('Sesión expirada');
        }

        const text = await response.text();
        let data;
        try {
            data = text ? JSON.parse(text) : {};
        } catch (e) {
            console.error('Error parsing JSON:', e);
            data = { error: 'Error processing response' };
        }

        if (!response.ok) {
            throw new Error(data.message || data.error || 'Error en la petición');
        }

        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

export const api = {
    get: (endpoint) => request(endpoint, { method: 'GET' }),
    post: (endpoint, body) => request(endpoint, { method: 'POST', body: JSON.stringify(body) }),
    put: (endpoint, body) => request(endpoint, { method: 'PUT', body: JSON.stringify(body) }),
    delete: (endpoint) => request(endpoint, { method: 'DELETE' })
};
