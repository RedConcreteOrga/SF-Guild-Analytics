<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between gap-4 flex-wrap">
      <div>
        <h2 class="text-3xl font-bold text-white tracking-tight">Bulk Stats Update</h2>
        <p class="text-slate-400 mt-1">Stats für alle Spieler deiner Gilden auf einmal eintragen und speichern.</p>
      </div>
      <div class="flex items-center gap-3 flex-wrap">
        <div class="flex flex-col gap-0.5">
          <label class="text-xs font-bold text-slate-500 uppercase tracking-wider">Snapshot-Datum</label>
          <input
            v-model="snapshotDate"
            type="datetime-local"
            class="bg-slate-800 border border-slate-700 rounded-lg px-3 py-2 text-white text-sm focus:border-indigo-500 focus:outline-none transition-all"
          />
        </div>
        <span v-if="totalSaved > 0" class="text-green-400 text-sm font-semibold flex items-center gap-1.5">
          <CheckCircle class="w-4 h-4" />
          {{ totalSaved }} gespeichert
        </span>
        <button
          @click="saveAll"
          :disabled="saving || dirtyCount === 0"
          class="bg-indigo-600 hover:bg-indigo-500 disabled:opacity-40 disabled:cursor-not-allowed text-white font-semibold py-2 px-5 rounded-xl shadow-lg shadow-indigo-600/20 transition-all flex items-center gap-2"
        >
          <Loader2 v-if="saving" class="w-4 h-4 animate-spin" />
          <Save v-else class="w-4 h-4" />
          Alle speichern
          <span v-if="dirtyCount > 0" class="bg-white/20 text-xs px-1.5 py-0.5 rounded-md font-bold">{{ dirtyCount }}</span>
        </button>
      </div>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-16">
      <Loader2 class="w-8 h-8 text-indigo-500 animate-spin" />
    </div>

    <!-- Empty -->
    <div v-else-if="guildData.length === 0" class="bg-slate-900/50 border border-slate-800 rounded-2xl p-12 text-center">
      <Users class="w-12 h-12 text-slate-700 mx-auto mb-4" />
      <p class="text-slate-400">Keine Gilden oder Spieler gefunden.</p>
    </div>

    <!-- Guild sections -->
    <div v-for="guild in guildData" :key="guild.id" class="space-y-3">
      <div class="flex items-center gap-3">
        <Shield class="w-5 h-5 text-indigo-400 flex-shrink-0" />
        <h3 class="text-lg font-bold text-white">{{ guild.name }}</h3>
        <span class="text-slate-600 text-sm">{{ guild.players.length }} Spieler</span>
      </div>

      <div v-if="guild.players.length === 0" class="text-slate-600 text-sm pl-8">
        Keine Spieler in dieser Gilde.
      </div>

      <div v-else class="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-xl shadow-slate-950/50">
        <div class="overflow-x-auto">
          <table class="w-full text-left text-sm">
            <thead>
              <tr class="bg-slate-800/60 border-b border-slate-800">
                <th class="px-4 py-3 text-xs font-bold text-slate-400 uppercase tracking-wider sticky left-0 bg-slate-800/60 min-w-[150px]">Spieler</th>
                <th class="px-2 py-3 text-xs font-bold text-indigo-400 uppercase tracking-wider text-center min-w-[80px]">Level</th>
                <th class="px-2 py-3 text-xs font-bold text-red-400 uppercase tracking-wider text-center min-w-[80px]">STR</th>
                <th class="px-2 py-3 text-xs font-bold text-green-400 uppercase tracking-wider text-center min-w-[80px]">DEX</th>
                <th class="px-2 py-3 text-xs font-bold text-blue-400 uppercase tracking-wider text-center min-w-[80px]">INT</th>
                <th class="px-2 py-3 text-xs font-bold text-yellow-400 uppercase tracking-wider text-center min-w-[80px]">CON</th>
                <th class="px-2 py-3 text-xs font-bold text-pink-400 uppercase tracking-wider text-center min-w-[80px]">HP</th>
                <th class="px-2 py-3 text-xs font-bold text-orange-400 uppercase tracking-wider text-center min-w-[80px]">Honor</th>
                <th class="px-2 py-3 text-xs font-bold text-teal-400 uppercase tracking-wider text-center min-w-[95px]">Dungeon %</th>
                <th class="px-2 py-3 text-xs font-bold text-purple-400 uppercase tracking-wider text-center min-w-[80px]">Festung</th>
                <th class="px-3 py-3 text-xs font-bold text-slate-400 uppercase tracking-wider text-center min-w-[50px]"></th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-800/60">
              <tr
                v-for="player in guild.players"
                :key="player.id"
                class="transition-colors"
                :class="dirty[player.id] ? 'bg-indigo-950/30' : ''"
              >
                <!-- Player name -->
                <td class="px-4 py-2.5 sticky left-0 bg-slate-900 font-semibold text-white whitespace-nowrap" :class="dirty[player.id] ? '!bg-indigo-950/30' : ''">
                  <div class="flex items-center gap-2">
                    <span class="w-1.5 h-1.5 rounded-full flex-shrink-0" :class="dirty[player.id] ? 'bg-indigo-400' : 'bg-slate-700'"></span>
                    {{ player.name }}
                  </div>
                </td>
                <!-- Level -->
                <td class="px-2 py-2">
                  <input v-model.number="forms[player.id].level" @input="markDirty(player.id)" type="number" min="0"
                    class="w-full bg-slate-800 border border-slate-700 focus:border-indigo-500 rounded-lg px-2 py-1.5 text-white text-center text-sm focus:outline-none transition-all" />
                </td>
                <!-- STR -->
                <td class="px-2 py-2">
                  <input v-model.number="forms[player.id].strength" @input="markDirty(player.id)" type="number" min="0"
                    class="w-full bg-slate-800 border border-slate-700 focus:border-red-500 rounded-lg px-2 py-1.5 text-white text-center text-sm focus:outline-none transition-all" />
                </td>
                <!-- DEX -->
                <td class="px-2 py-2">
                  <input v-model.number="forms[player.id].dexterity" @input="markDirty(player.id)" type="number" min="0"
                    class="w-full bg-slate-800 border border-slate-700 focus:border-green-500 rounded-lg px-2 py-1.5 text-white text-center text-sm focus:outline-none transition-all" />
                </td>
                <!-- INT -->
                <td class="px-2 py-2">
                  <input v-model.number="forms[player.id].intelligence" @input="markDirty(player.id)" type="number" min="0"
                    class="w-full bg-slate-800 border border-slate-700 focus:border-blue-500 rounded-lg px-2 py-1.5 text-white text-center text-sm focus:outline-none transition-all" />
                </td>
                <!-- CON -->
                <td class="px-2 py-2">
                  <input v-model.number="forms[player.id].constitution" @input="markDirty(player.id)" type="number" min="0"
                    class="w-full bg-slate-800 border border-slate-700 focus:border-yellow-500 rounded-lg px-2 py-1.5 text-white text-center text-sm focus:outline-none transition-all" />
                </td>
                <!-- HP -->
                <td class="px-2 py-2">
                  <input v-model.number="forms[player.id].hp" @input="markDirty(player.id)" type="number" min="0"
                    class="w-full bg-slate-800 border border-slate-700 focus:border-pink-500 rounded-lg px-2 py-1.5 text-white text-center text-sm focus:outline-none transition-all" />
                </td>
                <!-- Honor -->
                <td class="px-2 py-2">
                  <input v-model.number="forms[player.id].honor" @input="markDirty(player.id)" type="number" min="0"
                    class="w-full bg-slate-800 border border-slate-700 focus:border-orange-500 rounded-lg px-2 py-1.5 text-white text-center text-sm focus:outline-none transition-all" />
                </td>
                <!-- Dungeon % -->
                <td class="px-2 py-2">
                  <input v-model.number="forms[player.id].dungeonProgress" @input="markDirty(player.id)" type="number" min="0" max="100" step="0.1"
                    class="w-full bg-slate-800 border border-slate-700 focus:border-teal-500 rounded-lg px-2 py-1.5 text-white text-center text-sm focus:outline-none transition-all" />
                </td>
                <!-- Festung -->
                <td class="px-2 py-2">
                  <input v-model.number="forms[player.id].fortressLevel" @input="markDirty(player.id)" type="number" min="0"
                    class="w-full bg-slate-800 border border-slate-700 focus:border-purple-500 rounded-lg px-2 py-1.5 text-white text-center text-sm focus:outline-none transition-all" />
                </td>
                <!-- Action -->
                <td class="px-3 py-2 text-center">
                  <button
                    v-if="dirty[player.id]"
                    @click="savePlayer(player.id)"
                    class="text-indigo-400 hover:text-white transition-colors p-1 rounded"
                    title="Einzeln speichern"
                  >
                    <Save class="w-4 h-4" />
                  </button>
                  <CheckCircle v-else-if="saved[player.id]" class="w-4 h-4 text-green-400 mx-auto" />
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import api from '@/api/axios'
import { useAuthStore } from '@/stores/auth'
import { Loader2, Shield, Save, CheckCircle, Users } from 'lucide-vue-next'

const authStore = useAuthStore()

const loading = ref(true)
const saving = ref(false)
const totalSaved = ref(0)
const guildData = ref([])
const forms = reactive({})
const dirty = reactive({})
const saved = reactive({})

const toDatetimeLocal = () => {
  const d = new Date()
  const offset = d.getTimezoneOffset() * 60000
  return new Date(d - offset).toISOString().slice(0, 16)
}
const snapshotDate = ref(toDatetimeLocal())

const dirtyCount = computed(() => Object.values(dirty).filter(Boolean).length)

const markDirty = (playerId) => {
  dirty[playerId] = true
  saved[playerId] = false
}

const buildPayload = (playerId) => ({
  playerId,
  timestamp: new Date(snapshotDate.value).toISOString(),
  level:           forms[playerId].level           || 0,
  strength:        forms[playerId].strength        || 0,
  dexterity:       forms[playerId].dexterity       || 0,
  intelligence:    forms[playerId].intelligence    || 0,
  constitution:    forms[playerId].constitution    || 0,
  hp:              forms[playerId].hp              || 0,
  honor:           forms[playerId].honor           || 0,
  dungeonProgress: forms[playerId].dungeonProgress || 0,
  fortressLevel:   forms[playerId].fortressLevel   || 0,
})

const savePlayer = async (playerId) => {
  await api.post('/snapshots', buildPayload(playerId))
  dirty[playerId] = false
  saved[playerId] = true
  totalSaved.value++
}

const saveAll = async () => {
  saving.value = true
  try {
    const ids = Object.keys(dirty).filter(id => dirty[id])
    await Promise.all(ids.map(id => savePlayer(id)))
  } finally {
    saving.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const guildsRes = await api.get('/guilds')
    const isAdmin = authStore.user?.role === 'ADMIN'
    const ownId = authStore.user?.id
    const visibleGuilds = isAdmin
      ? guildsRes.data
      : guildsRes.data.filter(g => g.ownerId === ownId)

    guildData.value = await Promise.all(
      visibleGuilds.map(async (guild) => {
        const playersRes = await api.get(`/players/guild/${guild.id}`)
        const players = playersRes.data

        await Promise.all(players.map(async (player) => {
          const snapshotsRes = await api.get(`/snapshots/player/${player.id}`)
          const latest = snapshotsRes.data
            .sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp))[0] || {}
          forms[player.id] = {
            level:           latest.level           || 0,
            strength:        latest.strength        || 0,
            dexterity:       latest.dexterity       || 0,
            intelligence:    latest.intelligence    || 0,
            constitution:    latest.constitution    || 0,
            hp:              latest.hp              || 0,
            honor:           latest.honor           || 0,
            dungeonProgress: latest.dungeonProgress || 0,
            fortressLevel:   latest.fortressLevel   || 0,
          }
        }))

        return { ...guild, players }
      })
    )
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
