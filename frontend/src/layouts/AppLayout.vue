<template>
  <div class="min-h-screen bg-slate-950 flex flex-col">
    <!-- Navbar -->
    <nav class="bg-slate-900 border-b border-slate-800 sticky top-0 z-50 px-4 sm:px-6">
      <div class="max-w-7xl mx-auto flex items-center justify-between h-16">
        <div 
          class="flex items-center gap-2 group cursor-pointer" 
          @click="$router.push({ name: 'guilds' })"
        >
          <div class="bg-indigo-600 p-1.5 rounded-lg group-hover:shadow-lg group-hover:shadow-indigo-500/20 transition-all">
            <LayoutDashboard class="w-5 h-5 text-white" />
          </div>
          <span class="text-lg font-bold text-white tracking-tight">SF Guild Analytics</span>
        </div>

        <div v-if="!authStore.isReady && authStore.isAuthenticated" class="hidden md:flex items-center gap-2 text-indigo-400 text-xs font-bold uppercase tracking-widest">
          <span class="inline-block w-2 h-2 rounded-full bg-indigo-500 animate-pulse"></span>
          Initial Setup
        </div>

        <div class="flex items-center gap-4">
          <template v-if="authStore.isAuthenticated">
            <button 
              v-if="authStore.user?.role === 'ADMIN'"
              class="flex items-center gap-2 text-indigo-400 hover:text-indigo-300 transition-colors text-sm font-bold uppercase tracking-wider px-3 py-2 rounded-lg bg-indigo-500/10 border border-indigo-500/20"
            >
              <Shield class="w-4 h-4" />
              <span class="hidden sm:inline">Admin</span>
            </button>
            <button 
              v-if="authStore.user?.playerId"
              @click="$router.push({ name: 'player-detail', params: { id: authStore.user.playerId } })"
              class="flex items-center gap-2 text-indigo-400 hover:text-indigo-300 transition-colors text-sm font-bold uppercase tracking-wider px-3 py-2 rounded-lg bg-indigo-500/10 border border-indigo-500/20"
            >
              <History class="w-4 h-4" />
              <span class="hidden sm:inline">My Stats</span>
            </button>
            <button 
              @click="handleLogout"
              class="flex items-center gap-2 text-slate-400 hover:text-white transition-colors text-sm font-medium px-3 py-2 rounded-lg hover:bg-slate-800"
            >
              <LogOut class="w-4 h-4" />
              <span class="hidden sm:inline">Logout</span>
            </button>
          </template>
          <button 
            v-else
            @click="$router.push({ name: 'login' })"
            class="flex items-center gap-2 text-white bg-indigo-600 hover:bg-indigo-500 transition-all text-sm font-bold px-4 py-2 rounded-xl shadow-lg shadow-indigo-600/20"
          >
            <LogIn class="w-4 h-4" />
            <span>Login</span>
          </button>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="flex-1 max-w-7xl mx-auto w-full p-4 sm:p-6 lg:p-8">
      <router-view v-slot="{ Component }">
        <transition 
          name="fade" 
          mode="out-in"
        >
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- Footer -->
    <footer class="bg-slate-900 border-t border-slate-800 py-6 px-4">
      <div class="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4 text-slate-500 text-sm">
        <p>© 2026 SF Guild Analytics</p>
        <div class="flex items-center gap-6">
          <a href="#" class="hover:text-slate-300">Docs</a>
          <router-link v-if="!authStore.isAuthenticated" :to="{ name: 'login' }" class="text-indigo-400 hover:text-indigo-300 font-bold">Sign In</router-link>
          <a href="#" class="hover:text-slate-300">Support</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { LayoutDashboard, LogOut, Shield, LogIn, History } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

const handleLogout = () => {
  authStore.logout()
  router.push({ name: 'login' })
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
