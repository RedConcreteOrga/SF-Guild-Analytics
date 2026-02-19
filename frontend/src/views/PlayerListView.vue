<template>
  <div class="space-y-6">
    <div class="flex items-center gap-4">
      <button 
        @click="$router.push({ name: 'guilds' })"
        class="bg-slate-900 border border-slate-800 p-2 rounded-xl text-slate-400 hover:text-white hover:border-slate-700 transition-all shadow-lg"
      >
        <ArrowLeft class="w-5 h-5" />
      </button>
      <div>
        <h2 class="text-2xl font-bold text-white tracking-tight">{{ guildName || 'Loading Guild...' }}</h2>
        <p class="text-slate-400 text-sm">Player roster and statistics</p>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-12">
      <Loader2 class="w-8 h-8 text-indigo-500 animate-spin" />
    </div>

    <div v-else class="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-xl shadow-slate-950/50">
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
                  {{ player.className || player.class }}
                </span>
              </td>
              <td class="px-6 py-4 text-center">
                <span class="text-indigo-400 font-mono font-bold">{{ player.level || '-' }}</span>
              </td>
              <td class="px-6 py-4 text-slate-400 text-sm">
                {{ formatDate(player.joinedDate) }}
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/api/axios'
import { Loader2, ArrowLeft, User, Users, ExternalLink } from 'lucide-vue-next'

const route = useRoute()
const players = ref([])
const guildName = ref('')
const loading = ref(true)

const fetchGuildData = async () => {
  try {
    const [guildRes, playersRes] = await Promise.all([
      api.get(`/guilds/${route.params.id}`),
      api.get(`/players/guild/${route.params.id}`)
    ])
    guildName.value = guildRes.data.name
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
