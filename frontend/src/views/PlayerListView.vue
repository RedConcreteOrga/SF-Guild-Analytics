<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between gap-4">
      <div class="flex items-center gap-4">
        <button 
          @click="$router.push({ name: 'guilds' })"
          class="bg-slate-900 border border-slate-800 p-2 rounded-xl text-slate-400 hover:text-white hover:border-slate-700 transition-all shadow-lg"
        >
          <ArrowLeft class="w-5 h-5" />
        </button>
        <div class="flex items-center gap-4">
          <div class="h-12 w-12 rounded-xl overflow-hidden bg-slate-900 border border-slate-800 flex-shrink-0 relative">
            <img 
              v-if="guild?.imageUrl" 
              :src="guild.imageUrl" 
              class="w-full h-full object-cover absolute inset-0" 
              :alt="guild.name"
              @error="e => e.target.style.display = 'none'"
            />
            <div class="bg-indigo-600/10 w-full h-full flex items-center justify-center">
              <Shield class="w-6 h-6 text-indigo-400" />
            </div>
          </div>
          <div>
            <h2 class="text-2xl font-bold text-white tracking-tight">{{ guild?.name || 'Loading Guild...' }}</h2>
            <p class="text-slate-400 text-sm">Player roster and statistics</p>
          </div>
        </div>
      </div>

      <button
        v-if="canEdit"
        @click="$router.push({ name: 'add-player', params: { guildId: $route.params.id } })"
        class="bg-indigo-600 hover:bg-indigo-500 text-white font-semibold py-2 px-4 rounded-xl shadow-lg shadow-indigo-600/20 transition-all flex items-center gap-2"
      >
        <Plus class="w-5 h-5" />
        <span class="hidden sm:inline">Add Player</span>
      </button>
    </div>

    <div v-if="loading" class="flex justify-center py-12">
      <Loader2 class="w-8 h-8 text-indigo-500 animate-spin" />
    </div>

    <div v-else class="space-y-6">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-4 shadow-lg shadow-slate-950/20">
          <div class="text-xs font-bold text-slate-500 uppercase tracking-wider mb-1">Total Players</div>
          <div class="text-2xl font-bold text-white">{{ players.length }}</div>
        </div>
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-4 shadow-lg shadow-slate-950/20">
          <div class="text-xs font-bold text-slate-500 uppercase tracking-wider mb-1">Avg Level</div>
          <div class="text-2xl font-bold text-indigo-400">{{ averageLevel }}</div>
        </div>
        <div class="md:col-span-2 bg-slate-900 border border-slate-800 rounded-2xl p-4 shadow-lg shadow-slate-950/20">
          <div class="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Class Distribution</div>
          <div class="flex flex-wrap gap-2">
            <span v-for="(count, cls) in classDistribution" :key="cls" class="px-2 py-1 rounded-lg bg-slate-800 border border-slate-700 text-slate-300 text-xs font-medium">
              <span class="text-indigo-400 font-bold mr-1">{{ count }}</span> {{ cls }}
            </span>
          </div>
        </div>
      </div>

      <div class="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-xl shadow-slate-950/50">
      <div class="overflow-x-auto">
        <table class="w-full text-left">
          <thead>
            <tr class="bg-slate-800/50 border-b border-slate-800">
              <th class="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Player</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Class</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider text-center">Level</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Joined</th>
              <th class="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider text-right">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-800">
            <tr 
              v-for="player in players" 
              :key="player.id"
              class="hover:bg-slate-800/30 transition-colors group cursor-pointer"
              @click="$router.push({ name: 'player-detail', params: { id: player.id } })"
            >
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-slate-400 group-hover:bg-indigo-600/20 group-hover:text-indigo-400 transition-all">
                    <User class="w-4 h-4" />
                  </div>
                  <span class="font-bold text-white group-hover:text-indigo-400 transition-colors">{{ player.name }}</span>
                </div>
              </td>
              <td class="px-6 py-4">
                <span class="px-2.5 py-1 rounded-full bg-slate-800 text-slate-300 text-xs font-semibold border border-slate-700">
                  {{ player.playerClass }}
                </span>
              </td>
              <td class="px-6 py-4 text-center">
                <span class="text-indigo-400 font-mono font-bold">{{ player.level || '-' }}</span>
              </td>
              <td class="px-6 py-4 text-slate-400 text-sm">
                {{ formatDate(player.joinDate) }}
              </td>
              <td class="px-6 py-4 text-right">
                <button class="text-slate-500 hover:text-white transition-colors">
                  <ExternalLink class="w-4 h-4" />
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div v-if="players.length === 0" class="p-12 text-center">
        <Users class="w-12 h-12 text-slate-700 mx-auto mb-4" />
        <p class="text-slate-500 font-medium">This guild has no players registered.</p>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/api/axios'
import { Loader2, ArrowLeft, User, Users, ExternalLink, Plus, Shield } from 'lucide-vue-next'

const route = useRoute()
const authStore = useAuthStore()
const players = ref([])
const guild = ref(null)
const loading = ref(true)

const canEdit = computed(() =>
  authStore.isAuthenticated && (
    authStore.user?.role === 'ADMIN' ||
    guild.value?.ownerId === authStore.user?.id
  )
)

const averageLevel = computed(() => {
  if (players.value.length === 0) return 0
  const total = players.value.reduce((acc, p) => acc + (p.level || 0), 0)
  return Math.round((total / players.value.length) * 10) / 10
})

const classDistribution = computed(() => {
  const dist = {}
  players.value.forEach(p => {
    const cls = p.playerClass || 'Unknown'
    dist[cls] = (dist[cls] || 0) + 1
  })
  return dist
})

const fetchGuildData = async () => {
  try {
    const [guildRes, playersRes] = await Promise.all([
      api.get(`/guilds/${route.params.id}`),
      api.get(`/players/guild/${route.params.id}`)
    ])
    guild.value = guildRes.data
    players.value = playersRes.data
  } catch (error) {
    console.error('Error fetching guild players:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString()
}

onMounted(fetchGuildData)
</script>
