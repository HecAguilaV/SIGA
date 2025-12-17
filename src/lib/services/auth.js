import { api } from './api';
import { loginUser, logoutUser } from '../stores/authStore';

export const authService = {
    /**
     * Inicia sesión con credenciales operativas
     * @param {string} email 
     * @param {string} password 
     */
    async login(email, password) {
        try {
            const response = await api.post('/api/auth/login', { email, password });
            if (response.success) {
                loginUser(response);
                return { success: true, user: response.user };
            }
            return { success: false, message: 'Error desconocido' };
        } catch (error) {
            return { success: false, message: error.message };
        }
    },

    /**
     * Valida un token recibido vía SSO
     * @param {string} token - Token operativo
     */
    async validateSsoToken(token) {
        try {
            // Usamos /me para validar el token y obtener datos del usuario
            // Hack: Seteamos el token temporalmente en el store o headers manuales
            // Como api.js lee del store, necesitamos un mecanismo para "probar" un token
            // Usamos /me para validar el token y obtener datos del usuario
            // Hack: Seteamos el token temporalmente en el store o headers manuales
            // Usamos path relativo para aprovechar el proxy de Vite en desarrollo
            const response = await fetch('/api/auth/me', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.ok) {
                const data = await response.json();
                // Construimos objeto de sesión manual
                const sessionData = {
                    accessToken: token,
                    refreshToken: null, // SSO no siempre devuelve refresh token de inmediato
                    user: data.user
                };
                loginUser(sessionData);
                return { success: true };
            }
            return { success: false };
        } catch (error) {
            return { success: false, message: error.message };
        }
    },

    logout() {
        logoutUser();
    }
};
