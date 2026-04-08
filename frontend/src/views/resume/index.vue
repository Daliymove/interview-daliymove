<template>
  <div class="flex flex-col h-full">
    <div class="mb-6">
      <div class="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-semibold text-gray-900 flex items-center gap-2">
            <span class="i-carbon-document text-primary text-2xl"></span>
            简历管理
          </h1>
          <p class="text-gray-500 mt-1">管理和分析您的简历库</p>
        </div>
        <n-button type="primary" @click="handleUpload">
          <template #icon>
            <span class="i-carbon-upload"></span>
          </template>
          上传简历
        </n-button>
      </div>
    </div>

    <div v-if="stats" class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <n-card class="hover:shadow-md transition-shadow">
        <div class="flex items-center gap-3">
          <div class="p-3 rounded-lg bg-primary">
            <span class="i-carbon-document text-white text-2xl"></span>
          </div>
          <div>
            <p class="text-sm text-gray-500">简历总数</p>
            <p class="text-2xl font-semibold text-gray-900">{{ stats.totalCount }}</p>
          </div>
        </div>
      </n-card>

      <n-card class="hover:shadow-md transition-shadow">
        <div class="flex items-center gap-3">
          <div class="p-3 rounded-lg bg-emerald-500">
            <span class="i-carbon-checkmark-filled text-white text-2xl"></span>
          </div>
          <div>
            <p class="text-sm text-gray-500">已分析</p>
            <p class="text-2xl font-semibold text-gray-900">{{ stats.analyzedCount }}</p>
          </div>
        </div>
      </n-card>

      <n-card class="hover:shadow-md transition-shadow">
        <div class="flex items-center gap-3">
          <div class="p-3 rounded-lg bg-indigo-500">
            <span class="i-carbon-time text-white text-2xl"></span>
          </div>
          <div>
            <p class="text-sm text-gray-500">待分析</p>
            <p class="text-2xl font-semibold text-gray-900">{{ stats.pendingCount }}</p>
          </div>
        </div>
      </n-card>
    </div>

    <div class="flex-1 min-h-0">
      <HistoryList @select-resume="handleSelectResume" @resume-deleted="loadStats" />
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 简历库页面
 * - 显示简历列表和统计信息
 * - 支持搜索、删除
 * - 跳转到简历详情
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import HistoryList from '@/components/resume/HistoryList.vue'
import { historyApi } from '@/api/history'
import type { ResumeStats } from '@/types/resume'

const router = useRouter()
const stats = ref<ResumeStats | null>(null)

const handleSelectResume = (id: number) => {
  router.push(`/resume/${id}`)
}

const handleUpload = () => {
  router.push('/resume/upload')
}

const loadStats = async () => {
  try {
    const data = await historyApi.getStatistics()
    stats.value = data
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>