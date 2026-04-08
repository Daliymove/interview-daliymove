<template>
  <div class="mb-4">
    <div class="flex items-center gap-2 mb-3">
      <n-tag :type="colorType" round>
        {{ priority }}优先级 ({{ suggestions?.length || 0 }})
      </n-tag>
      <div class="flex-1 h-px bg-slate-200"></div>
    </div>
    <n-space vertical size="small">
      <n-card
        v-for="(suggestion, i) in suggestions"
        :key="i"
        embedded
        :class="cardClass"
      >
        <n-space align="center" class="mb-2">
          <n-tag :type="colorType" size="small" round>{{ priority }}</n-tag>
          <n-tag size="small" round type="default">{{ suggestion.category || '其他' }}</n-tag>
        </n-space>
        <div class="mb-2">
          <p class="font-semibold text-slate-900 mb-1">{{ suggestion.issue || '问题描述' }}</p>
          <p class="text-sm leading-relaxed text-slate-700">{{ suggestion.recommendation || suggestion }}</p>
        </div>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
/**
 * 建议分组组件
 * - 按优先级显示建议
 * - 不同颜色区分
 */
import { computed } from 'vue'

interface Suggestion {
  category: string
  priority: string
  issue: string
  recommendation: string
}

const props = defineProps<{
  priority: string
  suggestions?: Suggestion[]
  colorType?: 'error' | 'warning' | 'info'
}>()

const cardClass = computed(() => {
  switch (props.colorType) {
    case 'error':
      return 'bg-red-50 border border-red-200'
    case 'warning':
      return 'bg-amber-50 border border-amber-200'
    case 'info':
      return 'bg-blue-50 border border-blue-200'
    default:
      return 'bg-slate-50 border border-slate-200'
  }
})
</script>