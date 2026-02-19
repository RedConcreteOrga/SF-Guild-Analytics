<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between gap-4">
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

      <div class="flex gap-2">
        <button 
          v-if="authStore.isAuthenticated && player && authStore.user?.playerId !== player.id"
          @click="handleLinkPlayer"
          :disabled="linking"
          class="bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold py-2 px-4 rounded-xl border border-slate-700 transition-all flex items-center gap-2"
        >
          <Loader2 v-if="linking" class="w-4 h-4 animate-spin" />
          <UserPlus v-else class="w-4 h-4" />
          This is me
        </button>
        <button 
          v-if="authStore.isAuthenticated && player"
          @click="openEditModal"
          class="bg-indigo-600 hover:bg-indigo-500 text-white font-semibold py-2 px-4 rounded-xl shadow-lg shadow-indigo-600/20 transition-all flex items-center gap-2"
        >
          <Edit2 class="w-4 h-4" />
          Update Stats
        </button>
      </div>
    </div>

    <!-- Edit Stats Modal -->
    <div v-if="showEditModal" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
      <div class="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg overflow-hidden shadow-2xl">
        <div class="px-6 py-4 bg-slate-800/50 border-b border-slate-800 flex items-center justify-between">
          <h3 class="text-lg font-bold text-white">Update Player Stats</h3>
          <button @click="showEditModal = false" class="text-slate-400 hover:text-white transition-colors">
            <X class="w-5 h-5" />
          </button>
        </div>
        
        <form @submit.prevent="handleUpdateStats" class="p-6 space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="col-span-1">
              <label class="block text-xs font-bold text-slate-500 uppercase mb-1">Level</label>
              <input v-model.number="editForm.level" type="number" class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500 focus:outline-none transition-all" />
            </div>
            <div class="col-span-1">
              <label class="block text-xs font-bold text-slate-500 uppercase mb-1">Honor</label>
              <input v-model.number="editForm.honor" type="number" class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500 focus:outline-none transition-all" />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-bold text-slate-500 uppercase mb-1">Strength</label>
              <input v-model.number="editForm.strength" type="number" class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500 focus:outline-none transition-all" />
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-500 uppercase mb-1">Dexterity</label>
              <input v-model.number="editForm.dexterity" type="number" class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500 focus:outline-none transition-all" />
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-500 uppercase mb-1">Intelligence</label>
              <input v-model.number="editForm.intelligence" type="number" class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500 focus:outline-none transition-all" />
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-500 uppercase mb-1">Constitution</label>
              <input v-model.number="editForm.constitution" type="number" class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500 focus:outline-none transition-all" />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-bold text-slate-500 uppercase mb-1">HP</label>
              <input v-model.number="editForm.hp" type="number" class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500 focus:outline-none transition-all" />
            </div>
            <div>
              <label class="block text-xs font-bold text-slate-500 uppercase mb-1">Dungeon %</label>
              <input v-model.number="editForm.dungeonProgress" type="number" step="0.1" class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500 focus:outline-none transition-all" />
            </div>
          </div>

          <div class="grid grid-cols-1 gap-4">
            <div>
              <label class="block text-xs font-bold text-slate-500 uppercase mb-1">Fortress Level</label>
              <input v-model.number="editForm.fortressLevel" type="number" class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500 focus:outline-none transition-all" />
            </div>
          </div>

          <div class="pt-4 flex justify-end gap-3">
            <button 
              type="button" 
              @click="showEditModal = false"
              class="px-4 py-2 text-slate-400 hover:text-white transition-colors"
            >
              Cancel
            </button>
            <button 
              type="submit"
              :disabled="saving"
              class="bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white font-bold py-2 px-6 rounded-xl transition-all flex items-center gap-2"
            >
              <Loader2 v-if="saving" class="w-4 h-4 animate-spin" />
              Save Changes
            </button>
          </div>
        </form>
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
              <span class="text-white font-bold">{{ player.level || snapshots[0]?.level || '-' }}</span>
            </div>
            <div class="flex justify-between items-center py-2 border-b border-slate-800">
              <span class="text-slate-500 text-sm">Joined</span>
              <span class="text-white font-medium">{{ formatDate(player.joinDate) }}</span>
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
        <!-- Level Trend Chart -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl shadow-slate-950/50">
          <div class="flex items-center justify-between mb-6">
            <h3 class="font-bold text-white">Level Progress</h3>
            <span class="text-xs font-semibold text-slate-500 uppercase tracking-wider">Historical Trend</span>
          </div>
          
          <div v-if="snapshots.length > 1" class="h-48 w-full">
            <svg class="w-full h-full" viewBox="0 0 100 100" preserveAspectRatio="none">
              <!-- Y-axis lines -->
              <line v-for="i in 5" :key="i" x1="0" :y1="i*20" x2="100" :y2="i*20" stroke="#1e293b" stroke-width="0.5" />
              <!-- Area -->
              <path :d="chartAreaPath" fill="url(#gradient)" opacity="0.1" />
              <!-- Line -->
              <path :d="chartLinePath" fill="none" stroke="#6366f1" stroke-width="2" stroke-linejoin="round" />
              <!-- Points -->
              <circle v-for="pt in chartPoints" :key="pt.x" :cx="pt.x" :cy="pt.y" r="1" fill="#818cf8" />
              
              <defs>
                <linearGradient id="gradient" x1="0%" y1="0%" x2="0%" y2="100%">
                  <stop offset="0%" style="stop-color:#6366f1;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#6366f1;stop-opacity:0" />
                </linearGradient>
              </defs>
            </svg>
            <div class="flex justify-between mt-2 text-[10px] text-slate-600 font-bold uppercase tracking-tighter">
              <span>{{ formatDate(snapshots[snapshots.length-1].timestamp) }}</span>
              <span>{{ formatDate(snapshots[0].timestamp) }}</span>
            </div>
          </div>
          <div v-else class="h-48 flex flex-col items-center justify-center text-slate-600 border border-dashed border-slate-800 rounded-xl">
             <History class="w-8 h-8 mb-2 opacity-50" />
             <p class="text-sm">More snapshots needed for trend</p>
          </div>
        </div>

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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/api/axios'
import { Loader2, ArrowLeft, User, History, Database, Edit2, X, UserPlus } from 'lucide-vue-next'

const route = useRoute()
const authStore = useAuthStore()
const player = ref(null)
const snapshots = ref([])
const loading = ref(true)
const saving = ref(false)
const linking = ref(false)

const handleLinkPlayer = async () => {
  if (!player.value) return
  linking.value = true
  try {
    const response = await api.post(`/users/link-player/${player.value.id}`)
    // Update local user state
    authStore.user = {
       ...authStore.user,
       playerId: player.value.id
    }
    // Update localStorage
    localStorage.setItem('user', JSON.stringify(authStore.user))
  } catch (error) {
    console.error('Error linking player:', error)
  } finally {
    linking.value = false
  }
}

const showEditModal = ref(false)
const editForm = reactive({
  level: 0,
  strength: 0,
  dexterity: 0,
  intelligence: 0,
  constitution: 0,
  honor: 0,
  hp: 0,
  dungeonProgress: 0,
  fortressLevel: 0
})

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

const openEditModal = () => {
  const latest = snapshots.value[0] || {}
  editForm.level = player.value.level || latest.level || 1
  editForm.strength = latest.strength || 0
  editForm.dexterity = latest.dexterity || 0
  editForm.intelligence = latest.intelligence || 0
  editForm.constitution = latest.constitution || 0
  editForm.honor = latest.honor || 0
  editForm.hp = latest.hp || 0
  editForm.dungeonProgress = latest.dungeonProgress || 0
  editForm.fortressLevel = latest.fortressLevel || 0
  showEditModal.value = true
}

const handleUpdateStats = async () => {
  saving.value = true
  try {
    const payload = {
      ...editForm,
      playerId: player.value.id,
      timestamp: new Date().toISOString()
    }
    await api.post('/snapshots', payload)
    await fetchPlayerData() // Refresh data
    showEditModal.value = false
  } catch (error) {
    console.error('Error updating stats:', error)
    alert('Failed to update stats. Please check console for details.')
  } finally {
    saving.value = false
  }
}

const chartPoints = computed(() => {
  if (snapshots.value.length < 2) return []
  
  const sorted = [...snapshots.value].sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
  const levels = sorted.map(s => s.level)
  const minLvl = Math.min(...levels)
  const maxLvl = Math.max(...levels)
  const range = maxLvl - minLvl || 1
  
  return sorted.map((s, i) => ({
    x: (i / (sorted.length - 1)) * 100,
    y: 100 - ((s.level - minLvl) / range) * 80 - 10 // scale to 10-90
  }))
})

const chartLinePath = computed(() => {
  if (chartPoints.value.length < 2) return ''
  return 'M ' + chartPoints.value.map(p => `${p.x},${p.y}`).join(' L ')
})

const chartAreaPath = computed(() => {
  if (chartPoints.value.length < 2) return ''
  const line = chartLinePath.value
  return `${line} L 100,100 L 0,100 Z`
})

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString()
}

onMounted(fetchPlayerData)
</script>
