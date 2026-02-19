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
        appState: 'READY', // Guests start as READY
        currentGuild: null,
    }),
    getters: {
        isAuthenticated: (state) => !!state.token,
        isReady: (state) => state.appState === 'READY',
    },
    actions: {
        async login(username, password) {
            try {
                const response = await api.post('/auth/login', { username, password });
                this.token = response.data.token;
                this.user = response.data.user; // Assuming we add user info to the response
                localStorage.setItem('token', this.token);
                localStorage.setItem('user', JSON.stringify(this.user));
                await this.checkOnboardingStatus();
                return true;
            } catch (error) {
                console.error('Login failed', error);
                throw error;
            }
        },
        async checkOnboardingStatus() {
            if (!this.token) return;

            try {
                const guildsRes = await api.get('/guilds');
                const guilds = guildsRes.data;

                if (guilds.length === 0) {
                    console.log('Onboarding check results: No guilds found');
                    this.appState = 'NO_GUILD';
                } else {
                    this.currentGuild = guilds[0];
                    console.log('Onboarding check results: Guild found. READY!');
                    this.appState = 'READY';
                }
            } catch (error) {
                console.error('Error checking onboarding status', error);
                this.appState = 'NO_GUILD'; // Fallback
            }
        },
        logout() {
            this.token = null;
            this.user = null;
            this.appState = 'READY';
            this.currentGuild = null;
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        },
    },
});
