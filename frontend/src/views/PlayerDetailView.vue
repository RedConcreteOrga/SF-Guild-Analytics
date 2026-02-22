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
          <h3 class="text-lg font-bold text-white">
            {{ editingSnapshotId ? 'Snapshot bearbeiten' : 'Neuen Snapshot erstellen' }}
          </h3>
          <button @click="showEditModal = false" class="text-slate-400 hover:text-white transition-colors">
            <X class="w-5 h-5" />
          </button>
        </div>

        <form @submit.prevent="handleUpdateStats" class="p-6 space-y-4">
          <div>
            <label class="block text-xs font-bold text-slate-500 uppercase mb-1">Datum & Uhrzeit</label>
            <input v-model="editForm.timestamp" type="datetime-local" class="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-white focus:border-indigo-500 focus:outline-none transition-all" />
          </div>
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
        <!-- Stat Trend Chart -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl shadow-slate-950/50">
          <div class="flex items-center justify-between mb-4">
            <h3 class="font-bold text-white">Statistik-Verlauf</h3>
            <span class="text-xs font-semibold text-slate-500 uppercase tracking-wider">Historical Trend</span>
          </div>

          <!-- Stat Toggles -->
          <div class="flex flex-wrap gap-2 mb-4">
            <button
              v-for="stat in STAT_CONFIG"
              :key="stat.key"
              @click="toggleStat(stat.key)"
              class="px-2.5 py-1 rounded-lg text-xs font-bold border transition-all"
              :style="activeStats.includes(stat.key)
                ? { backgroundColor: stat.color + '22', borderColor: stat.color, color: stat.color }
                : { borderColor: '#334155', color: '#64748b' }"
            >
              {{ stat.label }}
            </button>
          </div>

          <div v-if="snapshots.length > 1" class="h-48 w-full">
            <svg class="w-full h-full" viewBox="0 0 100 100" preserveAspectRatio="none">
              <line v-for="i in 5" :key="i" x1="0" :y1="i*20" x2="100" :y2="i*20" stroke="#1e293b" stroke-width="0.5" />
              <template v-for="stat in STAT_CONFIG.filter(s => activeStats.includes(s.key))" :key="stat.key">
                <path
                  v-if="chartData[stat.key]"
                  :d="getLinePath(chartData[stat.key])"
                  fill="none"
                  :stroke="stat.color"
                  stroke-width="1.5"
                  stroke-linejoin="round"
                />
                <circle
                  v-for="pt in (chartData[stat.key] || [])"
                  :key="pt.x + stat.key"
                  :cx="pt.x"
                  :cy="pt.y"
                  r="1.2"
                  :fill="stat.color"
                />
              </template>
            </svg>
            <div class="flex justify-between mt-2 text-[10px] text-slate-600 font-bold uppercase tracking-tighter">
              <span>{{ formatDate(snapshots[snapshots.length-1].timestamp) }}</span>
              <span>{{ formatDate(snapshots[0].timestamp) }}</span>
            </div>
          </div>
          <div v-else class="h-48 flex flex-col items-center justify-center text-slate-600 border border-dashed border-slate-800 rounded-xl">
            <History class="w-8 h-8 mb-2 opacity-50" />
            <p class="text-sm">Mehr Snapshots für Trend benötigt</p>
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
                  <th v-if="authStore.isAuthenticated" class="px-4 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider text-center"></th>
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
                  <td v-if="authStore.isAuthenticated" class="px-4 py-4 text-center">
                    <div class="flex items-center justify-center gap-1">
                      <button
                        @click="openEditSnapshotModal(snapshot)"
                        class="text-slate-500 hover:text-indigo-400 transition-colors p-1 rounded"
                        title="Snapshot bearbeiten"
                      >
                        <Edit2 class="w-4 h-4" />
                      </button>
                      <button
                        @click="handleDeleteSnapshot(snapshot.id)"
                        class="text-slate-500 hover:text-red-400 transition-colors p-1 rounded"
                        title="Snapshot löschen"
                      >
                        <Trash2 class="w-4 h-4" />
                      </button>
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
import { Loader2, ArrowLeft, User, History, Database, Edit2, X, UserPlus, Trash2 } from 'lucide-vue-next'

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
const editingSnapshotId = ref(null)
const editForm = reactive({
  timestamp: '',
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

const toDatetimeLocal = (isoOrDate) => {
  const d = isoOrDate ? new Date(isoOrDate) : new Date()
  const offset = d.getTimezoneOffset() * 60000
  return new Date(d - offset).toISOString().slice(0, 16)
}

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
  editingSnapshotId.value = null
  const latest = snapshots.value[0] || {}
  editForm.timestamp = toDatetimeLocal()
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

const openEditSnapshotModal = (snapshot) => {
  editingSnapshotId.value = snapshot.id
  editForm.timestamp = toDatetimeLocal(snapshot.timestamp)
  editForm.level = snapshot.level
  editForm.strength = snapshot.strength
  editForm.dexterity = snapshot.dexterity
  editForm.intelligence = snapshot.intelligence
  editForm.constitution = snapshot.constitution
  editForm.honor = snapshot.honor
  editForm.hp = snapshot.hp
  editForm.dungeonProgress = snapshot.dungeonProgress
  editForm.fortressLevel = snapshot.fortressLevel
  showEditModal.value = true
}

const handleUpdateStats = async () => {
  saving.value = true
  try {
    const payload = {
      ...editForm,
      playerId: player.value.id,
      timestamp: new Date(editForm.timestamp).toISOString()
    }
    if (editingSnapshotId.value) {
      await api.put(`/snapshots/${editingSnapshotId.value}`, payload)
    } else {
      await api.post('/snapshots', payload)
    }
    await fetchPlayerData()
    showEditModal.value = false
  } catch (error) {
    console.error('Error saving snapshot:', error)
    alert('Fehler beim Speichern. Bitte Konsole prüfen.')
  } finally {
    saving.value = false
  }
}

const handleDeleteSnapshot = async (snapshotId) => {
  if (!confirm('Diesen Snapshot wirklich löschen?')) return
  try {
    await api.delete(`/snapshots/${snapshotId}`)
    await fetchPlayerData()
  } catch (error) {
    console.error('Error deleting snapshot:', error)
    alert('Fehler beim Löschen. Bitte Konsole prüfen.')
  }
}

const STAT_CONFIG = [
  { key: 'level',           label: 'Level',      color: '#6366f1' },
  { key: 'strength',        label: 'STR',        color: '#ef4444' },
  { key: 'dexterity',       label: 'DEX',        color: '#22c55e' },
  { key: 'intelligence',    label: 'INT',        color: '#3b82f6' },
  { key: 'constitution',    label: 'CON',        color: '#eab308' },
  { key: 'hp',              label: 'HP',         color: '#ec4899' },
  { key: 'honor',           label: 'Honor',      color: '#f97316' },
  { key: 'dungeonProgress', label: 'Dungeon %',  color: '#14b8a6' },
  { key: 'fortressLevel',   label: 'Festung',    color: '#a855f7' },
]

const activeStats = ref(['level'])

const toggleStat = (key) => {
  const idx = activeStats.value.indexOf(key)
  if (idx === -1) {
    activeStats.value.push(key)
  } else if (activeStats.value.length > 1) {
    activeStats.value.splice(idx, 1)
  }
}

const chartData = computed(() => {
  if (snapshots.value.length < 2) return {}
  const sorted = [...snapshots.value].sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp))
  const result = {}
  for (const stat of activeStats.value) {
    const values = sorted.map(s => Number(s[stat]) || 0)
    const min = Math.min(...values)
    const max = Math.max(...values)
    const range = max - min || 1
    result[stat] = sorted.map((s, i) => ({
      x: (i / (sorted.length - 1)) * 100,
      y: 100 - ((Number(s[stat] || 0) - min) / range) * 80 - 10
    }))
  }
  return result
})

const getLinePath = (points) => {
  if (!points || points.length < 2) return ''
  return 'M ' + points.map(p => `${p.x},${p.y}`).join(' L ')
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString()
}

onMounted(fetchPlayerData)
</script>
