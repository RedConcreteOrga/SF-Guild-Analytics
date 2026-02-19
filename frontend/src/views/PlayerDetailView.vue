<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center gap-4">
      <button 
        @click="$router.push({ name: 'guild-players', params: { id: player?.guildId } })"
        class="bg-slate-900 border border-slate-800 p-2 rounded-xl text-slate-400 hover:text-white hover:border-slate-700 transition-all shadow-lg"
      >
        <ArrowLeft class="w-5 h-5" />
      </button>
      <div>
        <h2 class="text-2xl font-bold text-white tracking-tight">{{ player?.name || 'Loading Player...' }}</h2>
        <p class="text-slate-400 text-sm">Detailed stats and historical snapshots</p>
      </div>
    </div>

    <div v-if="loading" class="flex justify-center py-12">
      <Loader2 class="w-8 h-8 text-indigo-500 animate-spin" />
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- Player Info Card -->
      <div class="lg:col-span-1 space-y-6">
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl shadow-slate-950/50">
          <div class="flex flex-col items-center text-center mb-6">
            <div class="w-20 h-20 rounded-2xl bg-indigo-600/10 border border-indigo-500/20 flex items-center justify-center mb-4">
              <User class="w-10 h-10 text-indigo-400" />
            </div>
            <h3 class="text-xl font-bold text-white">{{ player.name }}</h3>
            <span class="text-indigo-400 font-medium">{{ player.className || player.class }}</span>
          </div>

          <div class="space-y-4">
            <div class="flex justify-between items-center py-2 border-b border-slate-800">
              <span class="text-slate-500 text-sm">Level</span>
              <span class="text-white font-bold">{{ player.level }}</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-slate-800">
              <span class="text-slate-500 text-sm">Joined</span>
              <span class="text-white font-medium">{{ formatDate(player.joinedDate) }}</span>
            </div>
            <div class="flex justify-between items-center py-2">
              <span class="text-slate-500 text-sm">Last Update</span>
              <span class="text-white font-medium">{{ formatDate(snapshots[0]?.timestamp) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Snapshots History -->
      <div class="lg:col-span-2 space-y-6">
        <div class="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-xl shadow-slate-950/50">
          <div class="px-6 py-4 bg-slate-800/50 border-b border-slate-800 flex items-center justify-between">
            <h3 class="font-bold text-white">Snapshot History</h3>
            <History class="w-4 h-4 text-slate-500" />
          </div>
          
          <div class="overflow-x-auto">
            <table class="w-full text-left">
              <thead>
                <tr class="border-b border-slate-800">
                  <th class="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Date</th>
                  <th class="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider text-center">Level</th>
                  <th class="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider text-right">Attributes</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-800">
                <tr v-for="snapshot in snapshots" :key="snapshot.id" class="hover:bg-slate-800/30 transition-colors">
                  <td class="px-6 py-4">
                    <span class="text-white font-medium">{{ formatDate(snapshot.timestamp) }}</span>
                  </td>
                  <td class="px-6 py-4 text-center">
                    <span class="text-indigo-400 font-bold">{{ snapshot.level }}</span>
                  </td>
                  <td class="px-6 py-4">
                    <div class="flex justify-end gap-3 text-xs">
                      <div class="flex flex-col items-end">
                        <span class="text-slate-500 uppercase font-semibold">STR</span>
                        <span class="text-white">{{ snapshot.strength || '-' }}</span>
                      </div>
                      <div class="flex flex-col items-end">
                        <span class="text-slate-500 uppercase font-semibold">DEX</span>
                        <span class="text-white">{{ snapshot.dexterity || '-' }}</span>
                      </div>
                      <div class="flex flex-col items-end">
                        <span class="text-slate-500 uppercase font-semibold">INT</span>
                        <span class="text-white">{{ snapshot.intelligence || '-' }}</span>
                      </div>
                      <div class="flex flex-col items-end">
                        <span class="text-slate-500 uppercase font-semibold">CON</span>
                        <span class="text-white">{{ snapshot.constitution || '-' }}</span>
                      </div>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <div v-if="snapshots.length === 0" class="p-12 text-center">
            <Database class="w-12 h-12 text-slate-700 mx-auto mb-4" />
            <p class="text-slate-500 font-medium">No snapshots recorded for this player.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/api/axios'
import { Loader2, ArrowLeft, User, History, Database } from 'lucide-vue-next'

const route = useRoute()
const player = ref(null)
const snapshots = ref([])
const loading = ref(true)

const fetchPlayerData = async () => {
  try {
    const [playerRes, snapshotsRes] = await Promise.all([
      api.get(`/players/${route.params.id}`),
      api.get(`/snapshots/player/${route.params.id}`)
    ])
    player.value = playerRes.data
    snapshots.value = snapshotsRes.data.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))
  } catch (error) {
    console.error('Error fetching player details:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString()
}

onMounted(fetchPlayerData)
</script>
