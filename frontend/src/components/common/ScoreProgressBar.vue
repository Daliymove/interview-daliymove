<template>
  <div class="bg-slate-50 rounded-lg p-3" :class="className">
    <div class="text-xs text-slate-500 mb-1">{{ label }}</div>
    <div class="flex items-center gap-2">
      <div class="flex-1 h-2 bg-slate-200 rounded-full overflow-hidden">
        <div
          class="h-full rounded-full transition-all duration-500"
          :class="color"
          :style="{ width: `${animatedPercentage}%` }"
        />
      </div>
      <span class="text-sm font-semibold text-slate-700 w-12 text-right">
        {{ score }}/{{ maxScore }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 分数进度条组件
 * - 显示分数和满分
 * - 动态进度条动画
 * - 自定义颜色
 */
import { ref, onMounted, computed } from 'vue'
import { calculatePercentage } from '@/utils/score'

interface Props {
  label: string
  score: number
  maxScore: number
  color?: string
  delay?: number
  className?: string
}

const props = withDefaults(defineProps<Props>(), {
  color: 'bg-primary',
  delay: 0,
  className: ''
})

const animatedPercentage = ref(0)
const percentage = computed(() => calculatePercentage(props.score, props.maxScore))

onMounted(() => {
  setTimeout(() => {
    animatedPercentage.value = percentage.value
  }, props.delay * 1000)
})
</script>