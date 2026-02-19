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
            meta: { requiresAuth: true },
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
                    path: 'player/:id',
                    name: 'player-detail',
                    component: () => import('@/views/PlayerDetailView.vue'),
                },
            ],
        },
    ],
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next({ name: 'login' });
    } else if (to.name === 'login' && authStore.isAuthenticated) {
        next({ name: 'guilds' });
    } else {
        next();
    }
});

export default router;
