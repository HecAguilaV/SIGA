import { writable, get } from 'svelte/store';
import { api } from '../services/api';
import { authStore } from './authStore';

// Initial state with hybrid data (Simulated for Analytics, Empty for Real)
const initialState = {
    loading: false,
    error: null,
    stores: [], 
    products: [], 
    stock: [], 
    categories: [], 

    // --- MOCK DATA (Analytics fallback) ---
    weeklySales: [
        { storeId: 1, productId: 107, quantity: 58 },
        { storeId: 1, productId: 102, quantity: 52 }
    ],
    monthlyWastage: [
        { category: 'Dairy', quantity: 12 },
        { category: 'Drinks', quantity: 5 },
        { category: 'Snacks', quantity: 4 },
        { category: 'Sandwiches', quantity: 3 }
    ],
    dailySales: [
        { day: 'Monday', totalSales: 145 },
        { day: 'Tuesday', totalSales: 174 },
        { day: 'Wednesday', totalSales: 134 },
        { day: 'Thursday', totalSales: 188 },
        { day: 'Friday', totalSales: 205 },
        { day: 'Saturday', totalSales: 177 },
        { day: 'Sunday', totalSales: 150 }
    ],
    dailySalesByStore: [
        { day: 'Monday', store: 1, sales: 52 },
        { day: 'Monday', store: 2, sales: 35 },
        { day: 'Monday', store: 3, sales: 58 }
    ]
};

function createBusinessStore() {
    const { subscribe, set, update } = writable(initialState);

    return {
        subscribe,

        /**
         * Loads real data from the backend
         */
        loadData: async () => {
            update(s => ({ ...s, loading: true, error: null }));
            try {
                // Verify logged in user
                const auth = get(authStore);
                if (!auth.isAuthenticated) {
                    throw new Error('Not authenticated');
                }

                // Load Stores, Products, Stock, and Categories resiliently
                const [storesRes, productsRes, stockRes, categoriesRes] = await Promise.allSettled([
                    api.get('/api/stores'),
                    api.get('/api/products'),
                    api.get('/api/stock'),
                    api.get('/api/categories')
                ]);

                // Helper to extract data safely
                const getData = (res) => (res.status === 'fulfilled') ? (res.value || []) : [];

                update(s => ({
                    ...s,
                    loading: false,
                    stores: getData(storesRes),
                    products: getData(productsRes),
                    stock: getData(stockRes),
                    categories: getData(categoriesRes)
                }));

            } catch (error) {
                console.error('Error loading business data:', error);
                update(s => ({ ...s, loading: false, error: error.message }));
            }
        },

        reset: () => set(initialState)
    };
}

export const businessStore = createBusinessStore();
