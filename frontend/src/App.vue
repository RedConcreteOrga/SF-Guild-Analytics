<template>
  <div v-if="authStore.appState === 'LOADING' && authStore.isAuthenticated" class="min-h-screen bg-slate-950 flex flex-col items-center justify-center">
    <Loader2 class="w-12 h-12 text-indigo-500 animate-spin mb-4" />
    <p class="text-slate-400 font-medium animate-pulse">Initializing SF Guild Analytics...</p>
  </div>
  <RouterView v-else />
</template>

<script setup>
import { RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Loader2 } from 'lucide-vue-next'

const authStore = useAuthStore()
</script>
