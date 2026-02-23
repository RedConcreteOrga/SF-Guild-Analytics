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
                    path: 'guild/create',
                    name: 'guild-create',
                    component: () => import('@/views/CreateGuildView.vue'),
                    meta: { requiresAuth: true }
                },
                {
                    path: 'admin/bulk-update',
                    name: 'admin-bulk-update',
                    component: () => import('@/views/AdminBulkUpdateView.vue'),
                    meta: { requiresAuth: true }
                },
                {
                    path: 'admin/users',
                    name: 'admin-users',
                    component: () => import('@/views/AdminUsersView.vue'),
                    meta: { requiresAuth: true }
                },
                {
                    path: 'player/:id',
                    name: 'player-detail',
                    component: () => import('@/views/PlayerDetailView.vue'),
                },
                {
                    path: 'impressum',
                    name: 'impressum',
                    component: () => import('@/views/ImpressumView.vue'),
                    meta: { requiresAuth: false },
                },
            ],
        },
    ],
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        return next({ name: 'login' });
    }

    if (to.name === 'login' && authStore.isAuthenticated) {
        return next({ name: 'guilds' });
    }

    next();
});

export default router;
