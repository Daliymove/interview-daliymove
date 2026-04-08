<template>
  <div
    class="flex items-center gap-4 p-4 bg-slate-50 rounded-xl hover:bg-slate-100 cursor-pointer transition-colors group"
    @click="$emit('view')"
  >
    <div
      class="w-14 h-14 rounded-full flex items-center justify-center font-bold text-lg"
      :class="scoreBadgeClass"
    >
      {{ interview.overallScore ?? '-' }}
    </div>

    <div class="flex-1 min-w-0">
      <p class="font-medium text-slate-800 truncate">模拟面试 #{{ total - index }}</p>
      <div class="flex items-center gap-4 text-sm text-slate-500">
        <span class="flex items-center gap-1">
          <span class="i-carbon-calendar"></span>
          {{ formatDateOnly(interview.createdAt) }}
        </span>
        <span class="flex items-center gap-1">
          <span class="i-carbon-chat"></span>
          {{ interview.totalQuestions }} 题
        </span>
      </div>
    </div>

    <div class="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
      <n-button
        quaternary
        circle
        size="small"
        type="primary"
        :loading="exporting"
        @click.stop="$emit('export')"
      >
        <template #icon>
          <span class="i-carbon-download"></span>
        </template>
      </n-button>

      <n-button
        quaternary
        circle
        size="small"
        type="error"
        :loading="deleting"
        @click.stop="$emit('delete')"
      >
        <template #icon>
          <span class="i-carbon-trash-can"></span>
        </template>
      </n-button>
    </div>

    <span
      class="i-carbon-chevron-right text-slate-300 group-hover:text-primary group-hover:translate-x-1 transition-all flex-shrink-0"
    ></span>
  </div>
</template>

<script setup lang="ts">
/**
 * 面试项卡片组件
 * - 显示面试分数、日期、题目数
 * - 导出、删除操作
 */
import { computed } from 'vue'
import type { InterviewItem } from '@/types/resume'
import { formatDateOnly } from '@/utils/date'
import { getScoreBadgeColor } from '@/utils/score'

const props = defineProps<{
  interview: InterviewItem
  index: number
  total: number
  exporting: boolean
  deleting: boolean
}>()

defineEmits<{
  view: []
  export: []
  delete: []
}>()

const scoreBadgeClass = computed(() => {
  if (props.interview.overallScore !== null) {
    return getScoreBadgeColor(props.interview.overallScore, [85, 70])
  }
  return 'bg-slate-100 text-slate-400'
})
</script>