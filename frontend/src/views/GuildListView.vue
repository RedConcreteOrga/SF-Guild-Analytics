<template>
  <div class="space-y-8">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-3xl font-bold text-white tracking-tight">Guilds</h2>
        <p class="text-slate-400 mt-1">Manage and track your guilds.</p>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-12">
      <Loader2 class="w-8 h-8 text-indigo-500 animate-spin" />
    </div>

    <div v-else-if="guilds.length === 0" class="bg-slate-900/50 border border-slate-800 rounded-2xl p-12 text-center">
      <Users class="w-12 h-12 text-slate-700 mx-auto mb-4" />
      <h3 class="text-lg font-medium text-white">No guilds found</h3>
      <p class="text-slate-500 mt-2">Get started by creating your first guild in the backend.</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div 
        v-for="guild in guilds" 
        :key="guild.id"
        @click="$router.push({ name: 'guild-players', params: { id: guild.id } })"
        class="bg-slate-900 border border-slate-800 rounded-2xl p-6 hover:border-indigo-500/50 hover:bg-slate-800/50 transition-all cursor-pointer group shadow-lg shadow-slate-950/50"
      >
        <div class="flex items-start justify-between mb-4">
          <div class="bg-indigo-600/10 p-2.5 rounded-xl group-hover:bg-indigo-600 transition-colors">
            <Shield class="w-6 h-6 text-indigo-400 group-hover:text-white" />
          </div>
          <ChevronRight class="w-5 h-5 text-slate-600 group-hover:text-indigo-400 transition-colors" />
        </div>
        
        <h3 class="text-xl font-bold text-white mb-1 group-hover:text-indigo-400 transition-colors">{{ guild.name }}</h3>
        <div class="flex items-center gap-4 text-sm font-medium">
          <span class="text-slate-500">{{ guild.server }}</span>
          <span class="text-slate-700">•</span>
          <span class="text-slate-300">{{ guild.faction }}</span>
        </div>
        
        <div class="mt-6 pt-6 border-t border-slate-800 flex items-center justify-between">
          <div class="flex flex-col">
            <span class="text-xs font-semibold text-slate-500 uppercase tracking-wider">Players</span>
            <span class="text-lg font-bold text-white">{{ guild.playerCount || 0 }}</span>
          </div>
          <div class="text-right">
            <span class="text-xs font-semibold text-slate-500 uppercase tracking-wider">Avg Level</span>
            <span class="text-lg font-bold text-indigo-400">{{ guild.avgLevel || '-' }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/api/axios'
import { Loader2, Shield, Users, ChevronRight } from 'lucide-vue-next'

const guilds = ref([])
const loading = ref(true)

const fetchGuilds = async () => {
  try {
    const response = await api.get('/guilds')
    guilds.value = response.data
  } catch (error) {
    console.error('Error fetching guilds:', error)
  } finally {
    loading.value = false
  }
}

onMounted(fetchGuilds)
</script>
