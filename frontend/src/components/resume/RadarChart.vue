<template>
  <div class="radar-chart" :style="{ height: `${height}px` }">
    <svg viewBox="0 0 200 200" class="w-full h-full">
      <g transform="translate(100, 100)">
        <polygon
          v-for="level in levels"
          :key="level"
          :points="getPolygonPoints(level)"
          class="fill-none stroke-slate-200 dark:stroke-slate-700"
          stroke-width="1"
        />
        <line
          v-for="(point, i) in normalizedData"
          :key="`axis-${i}`"
          :x1="0"
          :y1="0"
          :x2="point.x"
          :y2="point.y"
          class="stroke-slate-200 dark:stroke-slate-700"
          stroke-width="1"
        />
        <polygon
          :points="dataPolygonPoints"
          class="fill-primary/30 stroke-primary"
          stroke-width="2"
        />
        <circle
          v-for="(point, i) in normalizedData"
          :key="`dot-${i}`"
          :cx="point.x"
          :cy="point.y"
          r="4"
          class="fill-primary"
        />
        <text
          v-for="(item, i) in normalizedData"
          :key="`label-${i}`"
          :x="getLabelPosition(i).x"
          :y="getLabelPosition(i).y"
          text-anchor="middle"
          class="text-xs fill-slate-600 dark:fill-slate-400"
          font-size="10"
        >
          {{ item.subject }}
        </text>
      </g>
    </svg>
  </div>
</template>

<script setup lang="ts">
/**
 * 雷达图组件
 * - 纯SVG实现，无需额外依赖
 * - 自动归一化数据
 * - 支持深色模式
 */
import { computed } from 'vue'
import { normalizeScore } from '@/utils/score'

interface RadarDataItem {
  subject: string
  score: number
  fullMark: number
}

interface Props {
  data: RadarDataItem[]
  height?: number
}

const props = withDefaults(defineProps<Props>(), {
  height: 320
})

const levels = [20, 40, 60, 80, 100]
const angleStep = (2 * Math.PI) / 5

const normalizedData = computed(() => {
  if (!props.data || props.data.length === 0) return []
  
  const maxFullMark = Math.max(...props.data.map(item => item.fullMark))
  
  return props.data.map((item, index) => {
    const normalizedScore = normalizeScore(item.score, item.fullMark, maxFullMark)
    const angle = index * angleStep - Math.PI / 2
    const radius = normalizedScore
    return {
      subject: item.subject,
      score: normalizedScore,
      fullMark: maxFullMark,
      originalScore: item.score,
      originalFullMark: item.fullMark,
      x: Math.cos(angle) * radius,
      y: Math.sin(angle) * radius
    }
  })
})

const getPolygonPoints = (level: number): string => {
  const points: string[] = []
  for (let i = 0; i < 5; i++) {
    const angle = i * angleStep - Math.PI / 2
    const x = Math.cos(angle) * level
    const y = Math.sin(angle) * level
    points.push(`${x},${y}`)
  }
  return points.join(' ')
}

const dataPolygonPoints = computed(() => {
  return normalizedData.value.map(p => `${p.x},${p.y}`).join(' ')
})

const getLabelPosition = (index: number) => {
  const angle = index * angleStep - Math.PI / 2
  const radius = 115
  return {
    x: Math.cos(angle) * radius,
    y: Math.sin(angle) * radius
  }
}
</script>

<style scoped>
.radar-chart {
  position: relative;
}
</style>