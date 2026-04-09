<template>
  <div class="flex flex-col h-full">
    <n-spin :show="loading" class="h-full">
      <template v-if="!loading && !resume">
        <n-card class="text-center py-20">
          <p class="text-red-500 mb-4">加载失败，请返回重试</p>
          <n-button type="primary" @click="onBack">返回列表</n-button>
        </n-card>
      </template>

      <template v-if="resume">
        <div class="flex flex-wrap justify-between items-center mb-6 gap-4">
          <div class="flex items-center gap-3 min-w-0 flex-1">
            <n-button
              quaternary
              circle
              @click="handleBackClick"
            >
              <template #icon>
                <span class="i-carbon-arrow-left text-lg"></span>
              </template>
            </n-button>
            <div class="min-w-0">
              <h2 class="text-xl font-bold text-slate-900 truncate">
                {{ detailView === 'interviewDetail' ? `面试详情 #${selectedInterview?.sessionId?.slice(-6) || ''}` : resume.filename }}
              </h2>
              <p class="text-sm text-slate-500 flex items-center gap-1.5">
                <span class="i-carbon-time text-sm"></span>
                {{ detailView === 'interviewDetail'
                  ? `完成于 ${formatDateOnly(selectedInterview?.completedAt || selectedInterview?.createdAt || '')}`
                  : `上传于 ${formatDateOnly(resume.uploadedAt)}`
                }}
              </p>
            </div>
          </div>

          <div class="flex flex-wrap gap-2">
            <template v-if="detailView === 'interviewDetail' && selectedInterview">
              <n-button
                secondary
                :loading="exporting === selectedInterview.sessionId"
                :disabled="exporting === selectedInterview.sessionId"
                @click="handleExportInterviewPdf(selectedInterview.sessionId)"
              >
                <template #icon>
                  <span class="i-carbon-download"></span>
                </template>
                {{ exporting === selectedInterview.sessionId ? '导出中...' : '导出 PDF' }}
              </n-button>
            </template>
            <template v-if="detailView !== 'interviewDetail'">
              <n-button
                type="primary"
                @click="handleStartInterview"
              >
                <template #icon>
                  <span class="i-carbon-microphone"></span>
                </template>
                开始模拟面试
              </n-button>
            </template>
          </div>
        </div>

        <template v-if="detailView !== 'interviewDetail'">
          <div class="flex flex-wrap gap-2 mb-6">
            <n-button
              :type="activeTab === 'analysis' ? 'primary' : 'default'"
              @click="handleTabChange('analysis')"
            >
              <template #icon>
                <span class="i-carbon-checkmark"></span>
              </template>
              简历分析
            </n-button>
            <n-button
              :type="activeTab === 'interview' ? 'primary' : 'default'"
              @click="handleTabChange('interview')"
            >
              <template #icon>
                <span class="i-carbon-chat"></span>
              </template>
              笔试记录
              <template v-if="resume.interviews?.length > 0">
                <n-badge
                  :value="resume.interviews.length"
                  type="info"
                  :max="99"
                  class="ml-2"
                />
              </template>
            </n-button>
          </div>
        </template>

        <template v-if="detailView === 'interviewDetail' && selectedInterview">
          <InterviewDetailPanel
            :interview="selectedInterview"
            :reevaluating="reevaluating"
            @reevaluate="handleReevaluate"
          />
        </template>
        <template v-else>
          <template v-if="activeTab === 'analysis'">
            <AnalysisPanel
              :analysis="latestAnalysis"
              :analyze-status="resume.analyzeStatus"
              :analyze-error="resume.analyzeError"
              :exporting="exporting === 'analysis'"
              :reanalyzing="reanalyzing"
              @export="handleExportAnalysisPdf"
              @reanalyze="handleReanalyze"
            />
          </template>
          <template v-else>
            <InterviewPanel
              :interviews="resume.interviews || []"
              :on-start-interview="handleStartInterview"
              :on-view-interview="handleViewInterview"
              :on-export-interview="handleExportInterviewPdf"
              :on-delete-interview="handleDeleteInterview"
              :exporting="exporting"
              :loading-interview="loadingInterview"
            />
          </template>
        </template>
      </template>
    </n-spin>
  </div>
</template>

<script setup lang="ts">
/**
 * 简历详情页面
 * - 显示简历分析结果
 * - 显示面试记录
 * - 支持导出PDF
 * - 支持重新分析/重新评估
 */
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { historyApi } from '@/api/history'
import { interviewApi } from '@/api/interview'
import type { ResumeDetail, InterviewDetail, AnalysisItem } from '@/types/resume'
import AnalysisPanel from '@/components/resume/AnalysisPanel.vue'
import InterviewPanel from '@/components/interview/InterviewPanel.vue'
import InterviewDetailPanel from '@/components/interview/InterviewDetailPanel.vue'
import { formatDateOnly } from '@/utils/date'

type TabType = 'analysis' | 'interview'
type DetailViewType = 'list' | 'interviewDetail'

const router = useRouter()
const route = useRoute()

const resume = ref<ResumeDetail | null>(null)
const loading = ref(true)
const activeTab = ref<TabType>('analysis')
const exporting = ref<string | null>(null)
const detailView = ref<DetailViewType>('list')
const selectedInterview = ref<InterviewDetail | null>(null)
const loadingInterview = ref(false)
const reanalyzing = ref(false)
const reevaluating = ref(false)

const resumeId = computed(() => {
  const id = Number(route.params.id)
  return Number.isNaN(id) ? null : id
})

const latestAnalysis = computed(() => {
  if (!resume.value?.analyses?.length) return undefined
  const analysis = resume.value.analyses[0] as AnalysisItem
  return {
    overallScore: analysis.overallScore,
    summary: analysis.summary,
    analyzedAt: analysis.analyzedAt,
    strengths: analysis.strengths as string[] | undefined,
    suggestions: analysis.suggestions as Array<{
      category: string
      priority: string
      issue: string
      recommendation: string
    }> | undefined,
    projectScore: analysis.projectScore,
    skillMatchScore: analysis.skillMatchScore,
    contentScore: analysis.contentScore,
    structureScore: analysis.structureScore,
    expressionScore: analysis.expressionScore
  }
})

const loadResumeDetail = async (silent = false) => {
  if (resumeId.value === null) {
    console.error('无效的简历ID')
    router.push('/resume')
    return
  }
  if (!silent) loading.value = true
  try {
    const data = await historyApi.getResumeDetail(resumeId.value)
    resume.value = data
  } catch (err) {
    console.error('加载简历详情失败', err)
  } finally {
    if (!silent) loading.value = false
  }
}

const handleReanalyze = async () => {
  if (resumeId.value === null) return
  try {
    reanalyzing.value = true
    await historyApi.reanalyze(resumeId.value)
    await loadResumeDetail(true)
  } catch (err) {
    console.error('重新分析失败', err)
    window.$message.error('重新分析失败')
  } finally {
    reanalyzing.value = false
  }
}

const handleReevaluate = async () => {
  if (!selectedInterview.value) return
  try {
    reevaluating.value = true
    await interviewApi.reevaluateInterview(selectedInterview.value.sessionId)
    detailView.value = 'list'
    selectedInterview.value = null
    await loadResumeDetail(true)
  } catch (err) {
    console.error('重新评估失败', err)
    window.$message.error('重新评估失败')
  } finally {
    reevaluating.value = false
  }
}

const handleExportAnalysisPdf = async () => {
  if (resumeId.value === null) return
  exporting.value = 'analysis'
  try {
    const blob = await historyApi.exportAnalysisPdf(resumeId.value)
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `简历分析报告_${resume.value?.filename || resumeId.value}.pdf`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
  } catch (err) {
    window.$message.error('导出失败，请重试')
  } finally {
    exporting.value = null
  }
}

const handleExportInterviewPdf = async (sessionId: string) => {
  exporting.value = sessionId
  try {
    const blob = await historyApi.exportInterviewPdf(sessionId)
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `面试报告_${sessionId}.pdf`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
  } catch (err) {
    window.$message.error('导出失败，请重试')
  } finally {
    exporting.value = null
  }
}

const handleViewInterview = async (sessionId: string) => {
  loadingInterview.value = true
  try {
    const detail = await historyApi.getInterviewDetail(sessionId)
    selectedInterview.value = detail
    detailView.value = 'interviewDetail'
  } catch (err) {
    window.$message.error('加载面试详情失败')
  } finally {
    loadingInterview.value = false
  }
}

const handleBackToInterviewList = () => {
  detailView.value = 'list'
  selectedInterview.value = null
}

const handleDeleteInterview = async (sessionId: string) => {
  await loadResumeDetail()
  if (selectedInterview.value?.sessionId === sessionId) {
    detailView.value = 'list'
    selectedInterview.value = null
  }
}

const handleTabChange = (tab: TabType) => {
  activeTab.value = tab
  detailView.value = 'list'
  selectedInterview.value = null
}

const handleStartInterview = () => {
  if (resume.value?.resumeText) {
    router.push(`/interview/session?resumeId=${resumeId.value}`)
  }
}

const onBack = () => {
  router.push('/resume')
}

const handleBackClick = () => {
  if (detailView.value === 'interviewDetail') {
    handleBackToInterviewList()
  } else {
    onBack()
  }
}

let pollTimer: ReturnType<typeof setInterval> | null = null

const startPolling = () => {
  if (pollTimer) return
  
  pollTimer = setInterval(() => {
    if (resumeId.value !== null) {
      loadResumeDetail(true)
    }
  }, 5000)
}

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

watch(resume, (newResume) => {
  if (!newResume) return
  
  const status = newResume.analyzeStatus
  const isProcessing = status === 'PENDING' || status === 'PROCESSING' ||
    (status === undefined && (!newResume.analyses || newResume.analyses.length === 0))
  
  if (isProcessing) {
    startPolling()
  } else {
    stopPolling()
  }
})

onUnmounted(() => {
  stopPolling()
})

onMounted(async () => {
  await loadResumeDetail()
  
  const viewInterview = route.query.viewInterview as string
  if (viewInterview && resume.value) {
    activeTab.value = 'interview'
    await handleViewInterview(viewInterview)
  }
})
</script>