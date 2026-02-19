import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/LoginView.vue'),
            meta: { requiresAuth: false },
        },
        {
            path: '/onboarding',
            component: () => import('@/layouts/AppLayout.vue'),
            meta: { requiresAuth: true },
            children: [
                {
                    path: 'guild',
                    name: 'first-guild',
                    component: () => import('@/views/Onboarding/FirstGuildView.vue'),
                },
            ],
        },
        {
            path: '/',
            component: () => import('@/layouts/AppLayout.vue'),
            meta: { requiresAuth: false },
            children: [
                {
                    path: '',
                    name: 'guilds',
                    component: () => import('@/views/GuildListView.vue'),
                },
                {
                    path: 'guild/:id',
                    name: 'guild-players',
                    component: () => import('@/views/PlayerListView.vue'),
                },
                {
                    path: 'guild/:guildId/add-player',
                    name: 'add-player',
                    component: () => import('@/views/AddPlayerView.vue'),
                    meta: { requiresAuth: true }
                },
                {
                    path: 'player/:id',
                    name: 'player-detail',
                    component: () => import('@/views/PlayerDetailView.vue'),
                },
            ],
        },
    ],
});

router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore();
    const isAuth = authStore.isAuthenticated;

    if (to.meta.requiresAuth && !isAuth) {
        return next({ name: 'login' });
    }

    if (to.name === 'login' && isAuth) {
        return next({ name: 'guilds' });
    }

    if (isAuth) {
        if (authStore.appState === 'READY' && !authStore.currentGuild) {
            await authStore.checkOnboardingStatus();
        }

        const state = authStore.appState;

        // Only force onboarding if it's an private route OR they are on the landing page
        const isPublicBrowse = to.name === 'guilds' || to.name === 'guild-players' || to.name === 'player-detail';

        if (!isPublicBrowse || (to.name === 'guilds' && state !== 'READY')) {
            if (state === 'NO_GUILD' && to.name !== 'first-guild') {
                return next({ name: 'first-guild' });
            }
        }

        if (state === 'READY' && to.name === 'first-guild') {
            return next({ name: 'guilds' });
        }
    }

    next();
});

export default router;
