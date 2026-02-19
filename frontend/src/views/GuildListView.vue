<template>
  <div class="space-y-8 relative">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-3xl font-bold text-white tracking-tight">Guilds</h2>
        <p class="text-slate-400 mt-1">Select a guild to explore its roster and metrics.</p>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-12">
      <Loader2 class="w-8 h-8 text-indigo-500 animate-spin" />
    </div>

    <div v-else-if="guilds.length === 0" class="bg-slate-900/50 border border-slate-800 rounded-2xl p-12 text-center">
      <Users class="w-12 h-12 text-slate-700 mx-auto mb-4" />
      <h3 class="text-lg font-medium text-white">No Guilds Found</h3>
      <p class="text-slate-500 mt-2">There are currently no guilds available to browse.</p>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div 
        v-for="guild in guilds" 
        :key="guild.id"
        @click="handleGuildClick(guild)"
        class="bg-slate-900 border border-slate-800 rounded-2xl p-6 hover:border-indigo-500/50 hover:bg-slate-800/50 transition-all cursor-pointer group shadow-lg shadow-slate-950/50 relative overflow-hidden"
      >
        <div class="flex items-start justify-between mb-4">
          <div class="h-14 w-14 rounded-2xl overflow-hidden bg-slate-950 border border-slate-800 group-hover:bg-indigo-600 transition-colors flex-shrink-0 relative">
            <img 
              v-if="guild.imageUrl" 
              :src="guild.imageUrl" 
              class="w-full h-full object-cover absolute inset-0" 
              :alt="guild.name"
              @error="e => e.target.style.display = 'none'"
            />
            <div class="bg-indigo-600/10 w-full h-full flex items-center justify-center">
              <Shield class="w-7 h-7 text-indigo-400 group-hover:text-white" />
            </div>
          </div>
          <ChevronRight class="w-5 h-5 text-slate-600 group-hover:text-indigo-400 transition-colors" />
        </div>
        
        <h3 class="text-xl font-bold text-white mb-1 group-hover:text-indigo-400 transition-colors">{{ guild.name }}</h3>
        <div class="flex items-center gap-4 text-sm font-medium">
          <span class="text-slate-500">{{ guild.server }}</span>
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
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/api/axios'
import { Loader2, Shield, Users, ChevronRight } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()

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

const handleGuildClick = (guild) => {
  authStore.currentGuild = guild
  router.push({ name: 'guild-players', params: { id: guild.id } })
}

onMounted(fetchGuilds)
</script>

