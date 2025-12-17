import { writable, get } from 'svelte/store';
import { api } from '../services/api';
import { authStore } from './authStore';

// Estado inicial con datos híbridos (Simulados para Analytics, Vacíos para Real)
const initialState = {
    loading: false,
    error: null,
    locales: [], // Real
    productos: [], // Real
    stock: [], // Real

    // --- DATOS SIMULADOS (Analytics) ---
    ventasSemana: [
        { localId: 1, productoId: 107, cantidad: 58 },
        { localId: 1, productoId: 102, cantidad: 52 },
        // ... (resto de datos simulados se mantendrán hardcorneados o movidos a un archivo separado de "constants")
        // Para simplificar, inicializamos con algunos datos dummy para que no rompa la UI
    ],
    mermasMes: [
        { categoria: 'Lácteos', cantidad: 12 },
        { categoria: 'Bebidas', cantidad: 5 },
        { categoria: 'Snacks', cantidad: 4 },
        { categoria: 'Sándwiches', cantidad: 3 }
    ],
    ventasPorDia: [
        { dia: 'Lunes', totalVentas: 145 },
        { dia: 'Martes', totalVentas: 174 },
        { dia: 'Miércoles', totalVentas: 134 },
        { dia: 'Jueves', totalVentas: 188 },
        { dia: 'Viernes', totalVentas: 205 },
        { dia: 'Sábado', totalVentas: 177 },
        { dia: 'Domingo', totalVentas: 150 }
    ],
    ventasPorDiaYLocal: [
        { dia: 'Lunes', local: 1, ventas: 52 },
        { dia: 'Lunes', local: 2, ventas: 35 },
        { dia: 'Lunes', local: 3, ventas: 58 }
        // ...
    ]
};

function createDatosNegocioStore() {
    const { subscribe, set, update } = writable(initialState);

    return {
        subscribe,

        /**
         * Carga los datos reales del backend
         */
        cargarDatos: async () => {
            update(s => ({ ...s, loading: true, error: null }));
            try {
                // Verificar si hay usuario logueado
                const auth = get(authStore);
                if (!auth.isAuthenticated) {
                    throw new Error('No autenticado');
                }

                // Cargar Locales, Productos y Stock de forma resiliente
                const [localesRes, productosRes, stockRes] = await Promise.allSettled([
                    api.get('/api/saas/locales'),
                    api.get('/api/saas/productos'),
                    api.get('/api/saas/stock')
                ]);

                // Helper para extraer datos seguros
                const getData = (res, key) => (res.status === 'fulfilled' && res.value?.success) ? (res.value[key] || []) : [];

                update(s => ({
                    ...s,
                    loading: false,
                    locales: getData(localesRes, 'locales'),
                    productos: getData(productosRes, 'productos'),
                    stock: getData(stockRes, 'stock')
                }));

            } catch (error) {
                console.error('Error cargando datos de negocio:', error);
                update(s => ({ ...s, loading: false, error: error.message }));
            }
        },

        // Métodos para actualizar localmente (optimistic UI) si fuera necesario
        reset: () => set(initialState)
    };
}

export const datosNegocio = createDatosNegocioStore();
