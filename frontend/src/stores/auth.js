import { defineStore } from 'pinia';
import api from '@/api/axios';

const safeJSONParse = (key) => {
    try {
        const item = localStorage.getItem(key);
        return item ? JSON.parse(item) : null;
    } catch (e) {
        return null;
    }
};

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || null,
        user: safeJSONParse('user'),
    }),
    getters: {
        isAuthenticated: (state) => !!state.token,
    },
    actions: {
        async login(username, password) {
            try {
                const response = await api.post('/auth/login', { username, password });
                this.token = response.data.token;
                this.user = response.data.user;
                localStorage.setItem('token', this.token);
                localStorage.setItem('user', JSON.stringify(this.user));
                return true;
            } catch (error) {
                console.error('Login failed', error);
                throw error;
            }
        },
        logout() {
            this.token = null;
            this.user = null;
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        },
    },
});
