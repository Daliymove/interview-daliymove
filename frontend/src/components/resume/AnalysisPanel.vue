<template>
  <div class="space-y-6">
    <template v-if="isProcessing">
      <n-card class="text-center py-12">
        <div class="w-16 h-16 mx-auto mb-6 rounded-full flex items-center justify-center"
             :class="isExplicitProcessing ? 'bg-blue-100' : 'bg-yellow-100'">
          <template v-if="isExplicitProcessing">
            <n-spin size="large" />
          </template>
          <template v-else>
            <span class="i-carbon-time text-3xl text-yellow-500"></span>
          </template>
        </div>
        <h3 class="text-xl font-semibold text-slate-700 mb-2">
          {{ isExplicitProcessing ? 'AI 正在分析中...' : '等待分析' }}
        </h3>
        <p class="text-slate-500 mb-4">
          {{ isExplicitProcessing
            ? '请稍候，AI 正在对您的简历进行深度分析'
            : '简历已上传成功，即将开始 AI 分析'
          }}
        </p>
        <p class="text-sm text-slate-400">页面将自动刷新显示分析结果</p>
      </n-card>
    </template>

    <template v-else-if="isFailed">
      <n-card class="text-center py-12">
        <div class="w-16 h-16 mx-auto mb-6 bg-red-100 rounded-full flex items-center justify-center">
          <span class="i-carbon-warning-alt text-3xl text-red-500"></span>
        </div>
        <h3 class="text-xl font-semibold text-slate-700 mb-2">分析失败</h3>
        <p class="text-slate-500 mb-4">AI 服务暂时不可用，请稍后重试</p>
        <template v-if="analyzeError || analysis?.summary">
          <n-alert type="error" class="mb-4 text-left" :bordered="false">
            {{ analyzeError || analysis?.summary }}
          </n-alert>
        </template>
        <n-button
          type="primary"
          :loading="reanalyzing"
          :disabled="reanalyzing"
          @click="emit('reanalyze')"
        >
          <template #icon>
            <span class="i-carbon-renew"></span>
          </template>
          {{ reanalyzing ? '重新分析中...' : '重新分析' }}
        </n-button>
      </n-card>
    </template>

    <template v-else>
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <n-card>
          <div class="flex items-center justify-between mb-6">
            <div class="flex items-center gap-2 text-slate-500">
              <span class="i-carbon-chart-line text-lg"></span>
              <span class="font-semibold">核心评价</span>
            </div>
            <n-button
              quaternary
              :loading="exporting"
              :disabled="exporting"
              @click="emit('export')"
            >
              <template #icon>
                <span class="i-carbon-download"></span>
              </template>
              {{ exporting ? '导出中...' : '导出分析报告' }}
            </n-button>
          </div>

          <div class="bg-gradient-to-br from-emerald-50 to-green-50 rounded-xl p-6">
            <p class="text-lg text-slate-800 leading-relaxed mb-6">
              {{ analysis?.summary || '候选人具备扎实的技术基础，有大型项目架构经验。' }}
            </p>

            <div class="grid grid-cols-2 gap-4 mb-4">
              <n-card embedded>
                <span class="text-sm font-semibold text-emerald-600 block mb-2">总分</span>
                <span class="text-4xl font-bold text-slate-900">{{ analysis?.overallScore || 0 }}</span>
                <span class="text-sm text-slate-500">/ 100</span>
              </n-card>
              <n-card embedded>
                <span class="text-sm font-semibold text-emerald-600 block mb-2">分析时间</span>
                <span class="text-sm text-slate-700">{{ formatDateTime(analysis?.analyzedAt) }}</span>
              </n-card>
            </div>

            <template v-if="analysis?.strengths && analysis.strengths.length > 0">
              <n-card embedded class="mt-4">
                <span class="text-sm font-semibold text-emerald-600 block mb-3">优势亮点</span>
                <n-space>
                  <n-tag
                    v-for="(s, i) in analysis.strengths"
                    :key="i"
                    type="success"
                    round
                  >
                    {{ s }}
                  </n-tag>
                </n-space>
              </n-card>
            </template>
          </div>
        </n-card>

        <n-card>
          <div class="flex items-center gap-2 text-slate-500 mb-6">
            <span class="i-carbon-target text-lg"></span>
            <span class="font-semibold">多维度评分</span>
          </div>

          <RadarChart :data="radarData" :height="320" />

          <div class="mt-4 grid grid-cols-2 gap-3">
            <ScoreProgressBar
              label="项目经验"
              :score="projectScore"
              :max-score="40"
              color="bg-purple-500"
              :delay="0.3"
              class-name="col-span-2"
            />
            <ScoreProgressBar
              label="技能匹配"
              :score="skillMatchScore"
              :max-score="20"
              color="bg-blue-500"
              :delay="0.4"
            />
            <ScoreProgressBar
              label="内容完整性"
              :score="contentScore"
              :max-score="15"
              color="bg-emerald-500"
              :delay="0.5"
            />
            <ScoreProgressBar
              label="结构清晰度"
              :score="structureScore"
              :max-score="15"
              color="bg-cyan-500"
              :delay="0.6"
            />
            <ScoreProgressBar
              label="表达专业性"
              :score="expressionScore"
              :max-score="10"
              color="bg-orange-500"
              :delay="0.7"
            />
          </div>
        </n-card>
      </div>

      <n-card>
        <div class="flex items-center gap-2 text-slate-500 mb-6">
          <span class="i-carbon-checkmark text-lg"></span>
          <span class="font-semibold">改进建议</span>
          <span class="text-sm text-slate-400">({{ analysis?.suggestions?.length || 0 }} 条)</span>
        </div>

        <n-space vertical size="large">
          <template v-if="suggestionsByPriority.high.length > 0">
            <SuggestionSection
              priority="高"
              :suggestions="suggestionsByPriority.high"
              color-type="error"
            />
          </template>
          <template v-if="suggestionsByPriority.medium.length > 0">
            <SuggestionSection
              priority="中"
              :suggestions="suggestionsByPriority.medium"
              color-type="warning"
            />
          </template>
          <template v-if="suggestionsByPriority.low.length > 0">
            <SuggestionSection
              priority="低"
              :suggestions="suggestionsByPriority.low"
              color-type="info"
            />
          </template>
          <template v-if="!analysis?.suggestions?.length">
            <div class="text-center py-8 text-slate-500">暂无改进建议</div>
          </template>
        </n-space>
      </n-card>
    </template>
  </div>
</template>

<script setup lang="ts">
/**
 * 简历分析面板组件
 * - 显示核心评价
 * - 多维度雷达图
 * - 改进建议列表
 * - 处理分析中/失败状态
 */
import { computed, type PropType } from 'vue'
import type { AnalyzeStatus } from '@/types/resume'
import RadarChart from './RadarChart.vue'
import ScoreProgressBar from '../common/ScoreProgressBar.vue'
import { formatDateTime } from '@/utils/date'
import SuggestionSection from './SuggestionSection.vue'

interface AnalysisData {
  overallScore: number
  summary: string
  analyzedAt: string
  strengths?: string[]
  suggestions?: Array<{
    category: string
    priority: string
    issue: string
    recommendation: string
  }>
  projectScore?: number
  skillMatchScore?: number
  contentScore?: number
  structureScore?: number
  expressionScore?: number
}

const props = defineProps({
  analysis: Object as PropType<AnalysisData>,
  analyzeStatus: String as PropType<AnalyzeStatus>,
  analyzeError: String as PropType<string>,
  exporting: Boolean,
  reanalyzing: Boolean
})

const emit = defineEmits<{
  export: []
  reanalyze: []
}>()

const projectScore = computed(() => props.analysis?.projectScore || 0)
const skillMatchScore = computed(() => props.analysis?.skillMatchScore || 0)
const contentScore = computed(() => props.analysis?.contentScore || 0)
const structureScore = computed(() => props.analysis?.structureScore || 0)
const expressionScore = computed(() => props.analysis?.expressionScore || 0)

const radarData = computed(() => {
  if (!props.analysis) return []
  
  return [
    { subject: '表达专业性', score: expressionScore.value, fullMark: 10 },
    { subject: '技能匹配', score: skillMatchScore.value, fullMark: 20 },
    { subject: '内容完整性', score: contentScore.value, fullMark: 15 },
    { subject: '结构清晰度', score: structureScore.value, fullMark: 15 },
    { subject: '项目经验', score: projectScore.value, fullMark: 40 }
  ]
})

const suggestionsByPriority = computed(() => {
  if (!props.analysis?.suggestions) return { high: [], medium: [], low: [] }
  
  return {
    high: props.analysis.suggestions.filter((s: any) => s.priority === '高'),
    medium: props.analysis.suggestions.filter((s: any) => s.priority === '中'),
    low: props.analysis.suggestions.filter((s: any) => s.priority === '低')
  }
})

const isProcessing = computed(() => {
  const status = props.analyzeStatus
  return status === 'PENDING' || status === 'PROCESSING' ||
    (status === undefined && !props.analysis)
})

const isExplicitProcessing = computed(() => props.analyzeStatus === 'PROCESSING')

const hasErrorKeywords = computed(() => {
  const summary = props.analysis?.summary
  return summary && (
    summary.includes('I/O error') ||
    summary.includes('分析过程中出现错误') ||
    summary.includes('简历分析失败') ||
    summary.includes('Remote host terminated') ||
    summary.includes('handshake')
  )
})

const isAnalysisValid = computed(() => {
  return props.analysis &&
    props.analysis.overallScore >= 10 &&
    props.analysis.summary &&
    !hasErrorKeywords.value
})

const isFailed = computed(() => {
  return props.analyzeStatus === 'FAILED' || !isAnalysisValid.value
})
</script>