<template>
  <div class="space-y-6">
    <template v-if="chartData.length > 0">
      <n-card>
        <div class="flex items-center justify-between mb-6">
          <div class="flex items-center gap-2">
            <span class="i-carbon-chart-line text-primary text-lg"></span>
            <span class="font-semibold text-slate-800">面试表现趋势</span>
          </div>
          <span class="text-sm text-slate-500">共 {{ chartData.length }} 场练习</span>
        </div>

        <div class="h-48">
          <svg class="w-full h-full" viewBox="0 0 400 150">
            <defs>
              <linearGradient id="lineGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                <stop offset="0%" style="stop-color: #6366f1" />
                <stop offset="100%" style="stop-color: #8b5cf6" />
              </linearGradient>
            </defs>
            
            <line x1="40" y1="130" x2="380" y2="130" stroke="#e2e8f0" stroke-width="1" />
            <line x1="40" y1="10" x2="40" y2="130" stroke="#e2e8f0" stroke-width="1" />
            
            <template v-for="i in [25, 50, 75, 100]" :key="i">
              <line :x1="40" :y1="130 - i" x2="380" :y2="130 - i" stroke="#f1f5f9" stroke-width="0.5" />
              <text :x="30" :y="130 - i + 4" text-anchor="end" class="text-xs fill-slate-400" font-size="10">
                {{ i }}
              </text>
            </template>
            
            <polyline
              :points="linePoints"
              fill="none"
              stroke="url(#lineGradient)"
              stroke-width="3"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
            
            <circle
              v-for="(point, i) in chartPoints"
              :key="i"
              :cx="point.x"
              :cy="point.y"
              r="5"
              fill="#6366f1"
              stroke="white"
              stroke-width="2"
            />
            
            <text
              v-for="(point, i) in chartPoints"
              :key="`label-${i}`"
              :x="point.x"
              :y="145"
              text-anchor="middle"
              class="text-xs fill-slate-400"
              font-size="10"
            >
              {{ point.label }}
            </text>
          </svg>
        </div>
      </n-card>
    </template>

    <n-card>
      <div class="flex items-center justify-between mb-6">
        <span class="font-semibold text-slate-800">历史面试场次</span>
      </div>

      <n-space vertical size="large">
        <InterviewItemCard
          v-for="(interview, index) in interviews"
          :key="interview.id"
          :interview="interview"
          :index="index"
          :total="interviews.length"
          :exporting="exporting === interview.sessionId"
          :deleting="deletingSessionId === interview.sessionId"
          @view="onViewInterview(interview.sessionId)"
          @export="onExportInterview(interview.sessionId)"
          @delete="handleDeleteClick(interview.sessionId)"
        />
      </n-space>

      <template v-if="loadingInterview">
        <div class="fixed inset-0 bg-black/20 flex items-center justify-center z-50">
          <n-card class="w-64">
            <n-spin size="large" />
            <span class="ml-4 text-slate-600">加载面试详情...</span>
          </n-card>
        </div>
      </template>
    </n-card>

    <ConfirmDialog
      v-model:open="deleteConfirmVisible"
      title="删除面试记录"
      message="确定要删除这条面试记录吗？删除后无法恢复。"
      confirm-text="确定删除"
      confirm-variant="danger"
      :loading="deletingSessionId !== null"
      @confirm="handleDeleteConfirm"
      @cancel="deleteConfirmVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * 面试记录面板组件
 * - 面试表现趋势图
 * - 历史面试场次列表
 */
import { ref, computed } from 'vue'
import { historyApi } from '@/api/history'
import type { InterviewItem } from '@/types/resume'
import ConfirmDialog from '../common/ConfirmDialog.vue'
import InterviewItemCard from './InterviewItemCard.vue'
import { formatDateOnly } from '@/utils/date'

const props = defineProps<{
  interviews: InterviewItem[]
  onStartInterview: () => void
  onViewInterview: (sessionId: string) => void
  onExportInterview: (sessionId: string) => void
  onDeleteInterview: (sessionId: string) => void
  exporting: string | null
  loadingInterview: boolean
}>()

const deletingSessionId = ref<string | null>(null)
const deleteConfirmVisible = ref(false)
const deleteSessionId = ref<string | null>(null)

const chartData = computed(() => {
  return props.interviews
    .filter(i => i.overallScore !== null)
    .map((interview, idx) => ({
      name: formatDateOnly(interview.createdAt),
      score: interview.overallScore || 0,
      index: props.interviews.length - idx
    }))
    .reverse()
})

const chartPoints = computed(() => {
  if (chartData.value.length === 0) return []
  
  const width = 340
  const height = 120
  const startX = 40
  
  return chartData.value.map((item, i) => {
    const x = startX + (i / (chartData.value.length - 1 || 1)) * width
    const y = height - (item.score / 100) * height
    return { x, y, label: item.name }
  })
})

const linePoints = computed(() => {
  return chartPoints.value.map(p => `${p.x},${p.y}`).join(' ')
})

const handleDeleteClick = (sessionId: string) => {
  deleteSessionId.value = sessionId
  deleteConfirmVisible.value = true
}

const handleDeleteConfirm = async () => {
  if (!deleteSessionId.value) return

  deletingSessionId.value = deleteSessionId.value
  try {
    await historyApi.deleteInterview(deleteSessionId.value)
    props.onDeleteInterview(deleteSessionId.value)
    deleteConfirmVisible.value = false
    deleteSessionId.value = null
  } catch (err) {
    window.$message.error(err instanceof Error ? err.message : '删除失败，请稍后重试')
  } finally {
    deletingSessionId.value = null
  }
}
</script>