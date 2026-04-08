<template>
  <div class="flex flex-col h-full">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-900 flex items-center gap-2">
        <span class="i-carbon-user-multiple text-primary text-2xl"></span>
        面试记录
      </h1>
      <p class="text-gray-500 mt-1">查看和管理所有模拟面试记录</p>
    </div>

    <div v-if="stats" class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <n-card class="hover:shadow-md transition-shadow">
        <div class="flex items-center gap-3">
          <div class="p-3 rounded-lg bg-primary">
            <span class="i-carbon-user-multiple text-white text-2xl"></span>
          </div>
          <div>
            <p class="text-sm text-gray-500">面试总数</p>
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
            <p class="text-sm text-gray-500">已完成</p>
            <p class="text-2xl font-semibold text-gray-900">{{ stats.completedCount }}</p>
          </div>
        </div>
      </n-card>

      <n-card class="hover:shadow-md transition-shadow">
        <div class="flex items-center gap-3">
          <div class="p-3 rounded-lg bg-indigo-500">
            <span class="i-carbon-chart-line text-white text-2xl"></span>
          </div>
          <div>
            <p class="text-sm text-gray-500">平均分数</p>
            <p class="text-2xl font-semibold text-gray-900">
              {{ stats.averageScore }}<span class="text-base font-normal text-gray-400 ml-1">分</span>
            </p>
          </div>
        </div>
      </n-card>
    </div>

    <n-card class="mb-4">
      <div class="flex flex-wrap items-center gap-3">
        <div class="flex items-center gap-2 flex-1 min-w-0">
          <span class="i-carbon-search text-gray-400"></span>
          <n-input
            v-model:value="searchTerm"
            placeholder="搜索简历名称..."
            :bordered="false"
            size="small"
            class="flex-1"
          />
        </div>
      </div>
    </n-card>

    <div class="flex-1 min-h-0">
      <n-spin :show="loading" class="h-full">
        <template v-if="!loading && filteredInterviews.length === 0">
          <n-card class="text-center py-20">
            <span class="i-carbon-user-multiple text-gray-300 text-5xl mb-4 block"></span>
            <h3 class="text-xl font-semibold text-gray-700 mb-2">暂无面试记录</h3>
            <p class="text-gray-500">开始一次模拟面试后，记录将显示在这里</p>
          </n-card>
        </template>

        <template v-if="!loading && filteredInterviews.length > 0">
          <n-card>
            <n-data-table
              :columns="columns"
              :data="filteredInterviews"
              :bordered="false"
              :row-key="(row: InterviewWithResume) => row.sessionId"
              @update:checked-row-keys="handleCheck"
            />
          </n-card>
        </template>
      </n-spin>
    </div>

    <n-modal
      v-model:show="showDeleteConfirm"
      preset="dialog"
      title="确认删除"
      content="确定要删除这条面试记录吗？删除后无法恢复。"
      positive-text="确定"
      negative-text="取消"
      type="warning"
      :loading="deletingSessionId !== null"
      @positive-click="handleDeleteConfirm"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * 面试历史列表页面
 * - 显示所有面试记录
 * - 统计信息展示
 * - 搜索和筛选
 * - 导出PDF
 * - 删除记录
 * - 跳转到面试详情
 */
import { ref, computed, h, onMounted, onUnmounted, watch, type VNode } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NTag, NProgress, NSpace } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import type { ProgressStatus } from 'naive-ui'
import { historyApi } from '@/api/history'
import { formatFullDateTime } from '@/utils/date'
import type { InterviewItem } from '@/types/resume'

const router = useRouter()

interface InterviewWithResume extends InterviewItem {
  resumeId: number
  resumeFilename: string
}

interface InterviewStats {
  totalCount: number
  completedCount: number
  averageScore: number
}

const interviews = ref<InterviewWithResume[]>([])
const stats = ref<InterviewStats | null>(null)
const loading = ref(true)
const searchTerm = ref('')
const deletingSessionId = ref<string | null>(null)
const deleteItem = ref<InterviewWithResume | null>(null)
const showDeleteConfirm = ref(false)
const exporting = ref<string | null>(null)
let pollingTimer: number | null = null

const isCompletedStatus = (status: string): boolean => {
  return status === 'COMPLETED' || status === 'EVALUATED'
}

const isEvaluateCompleted = (interview: InterviewWithResume): boolean => {
  if (interview.evaluateStatus === 'COMPLETED') return true
  if (interview.status === 'EVALUATED') return true
  return false
}

const isEvaluating = (interview: InterviewWithResume): boolean => {
  return interview.evaluateStatus === 'PENDING' || interview.evaluateStatus === 'PROCESSING'
}

const isEvaluateFailed = (interview: InterviewWithResume): boolean => {
  return interview.evaluateStatus === 'FAILED'
}

const getStatusIcon = (interview: InterviewWithResume) => {
  if (isEvaluateFailed(interview)) {
    return { icon: 'i-carbon-warning-alt', color: 'text-red-500' }
  }
  if (isEvaluating(interview)) {
    return { icon: 'i-carbon-renew animate-spin', color: 'text-blue-500' }
  }
  if (isEvaluateCompleted(interview)) {
    return { icon: 'i-carbon-checkmark-filled', color: 'text-green-500' }
  }
  if (interview.status === 'IN_PROGRESS') {
    return { icon: 'i-carbon-play-filled', color: 'text-blue-500' }
  }
  return { icon: 'i-carbon-time', color: 'text-yellow-500' }
}

const getStatusText = (interview: InterviewWithResume): string => {
  if (isEvaluateFailed(interview)) return '评估失败'
  if (isEvaluating(interview)) {
    return interview.evaluateStatus === 'PROCESSING' ? '评估中' : '等待评估'
  }
  if (isEvaluateCompleted(interview)) return '已完成'
  if (interview.status === 'IN_PROGRESS') return '进行中'
  if (isCompletedStatus(interview.status)) return '已提交'
  return '已创建'
}

const getScoreColor = (score: number): ProgressStatus => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'error'
}

const filteredInterviews = computed(() => {
  return interviews.value.filter(interview =>
    interview.resumeFilename.toLowerCase().includes(searchTerm.value.toLowerCase())
  )
})

const columns: DataTableColumns<InterviewWithResume> = [
  {
    title: '关联简历',
    key: 'resumeFilename',
    render(row) {
      return h('div', { class: 'flex items-center gap-3' }, [
        h('span', { class: 'i-carbon-document text-gray-400' }),
        h('div', {}, [
          h('p', { class: 'font-medium text-gray-900' }, row.resumeFilename),
          h('p', { class: 'text-xs text-gray-400' }, `#${row.sessionId.slice(-8)}`)
        ])
      ])
    }
  },
  {
    title: '题目数',
    key: 'totalQuestions',
    width: 100,
    render(row) {
      return h(NTag, { type: 'default', bordered: false }, {
        default: () => `${row.totalQuestions} 题`
      })
    }
  },
  {
    title: '状态',
    key: 'status',
    width: 120,
    render(row) {
      const { icon, color } = getStatusIcon(row)
      return h('div', { class: 'flex items-center gap-2' }, [
        h('span', { class: `${icon} ${color}` }),
        h('span', { class: 'text-sm text-gray-600' }, getStatusText(row))
      ])
    }
  },
  {
    title: '得分',
    key: 'overallScore',
    width: 150,
    render(row) {
      if (isEvaluateCompleted(row) && row.overallScore !== null) {
        return h('div', { class: 'flex items-center gap-3' }, [
          h(NProgress, {
            type: 'line',
            percentage: row.overallScore,
            showIndicator: false,
            height: 8,
            status: getScoreColor(row.overallScore),
            class: 'w-16'
          }),
          h('span', { class: 'font-bold text-gray-900' }, row.overallScore)
        ])
      }
      if (isEvaluating(row)) {
        return h('span', { class: 'text-blue-500 text-sm' }, '生成中...')
      }
      if (isEvaluateFailed(row)) {
        return h('span', { class: 'text-red-500 text-sm', title: row.evaluateError || '' }, '失败')
      }
      return h('span', { class: 'text-gray-400' }, '-')
    }
  },
  {
    title: '创建时间',
    key: 'createdAt',
    width: 180,
    render(row) {
      return h('span', { class: 'text-sm text-gray-500' }, formatFullDateTime(row.createdAt))
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 120,
    align: 'right',
    render(row) {
      const buttons: VNode[] = []
      
      if (isEvaluateCompleted(row)) {
        buttons.push(
          h(NButton, {
            size: 'small',
            quaternary: true,
            type: 'primary',
            loading: exporting.value === row.sessionId,
            disabled: exporting.value === row.sessionId,
            onClick: (e: Event) => {
              e.stopPropagation()
              handleExport(row.sessionId)
            }
          }, {
            icon: () => h('span', { class: 'i-carbon-download' }),
            default: () => '导出'
          })
        )
      }
      
      buttons.push(
        h(NButton, {
          size: 'small',
          quaternary: true,
          type: 'error',
          disabled: deletingSessionId.value === row.sessionId,
          onClick: (e: Event) => {
            e.stopPropagation()
            handleDeleteClick(row)
          }
        }, {
          icon: () => h('span', { class: 'i-carbon-trash-can' }),
          default: () => '删除'
        })
      )
      
      buttons.push(
        h(NButton, {
          size: 'small',
          quaternary: true,
          type: 'primary',
          onClick: () => viewInterview(row.sessionId, row.resumeId)
        }, {
          icon: () => h('span', { class: 'i-carbon-arrow-right' }),
          default: () => '查看'
        })
      )
      
      return h(NSpace, { size: 'small' }, { default: () => buttons })
    }
  }
]

const loadAllInterviews = async (isPolling = false) => {
  if (!isPolling) {
    loading.value = true
  }
  
  try {
    const resumes = await historyApi.getResumes()
    const allInterviews: InterviewWithResume[] = []

    for (const resume of resumes) {
      const detail = await historyApi.getResumeDetail(resume.id)
      if (detail.interviews && detail.interviews.length > 0) {
        detail.interviews.forEach(interview => {
          allInterviews.push({
            ...interview,
            resumeId: resume.id,
            resumeFilename: resume.filename
          })
        })
      }
    }

    allInterviews.sort((a, b) =>
      new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    )

    interviews.value = allInterviews

    const evaluated = allInterviews.filter(i => isEvaluateCompleted(i))
    const totalScore = evaluated.reduce((sum, i) => sum + (i.overallScore || 0), 0)
    stats.value = {
      totalCount: allInterviews.length,
      completedCount: evaluated.length,
      averageScore: evaluated.length > 0 ? Math.round(totalScore / evaluated.length) : 0,
    }
  } catch (err) {
    console.error('加载面试记录失败', err)
  } finally {
    if (!isPolling) {
      loading.value = false
    }
  }
}

const startPollingIfNeeded = () => {
  const hasEvaluating = interviews.value.some(i => isEvaluating(i))
  
  if (hasEvaluating) {
    if (!pollingTimer) {
      pollingTimer = window.setInterval(() => {
        loadAllInterviews(true)
      }, 3000)
    }
  } else {
    if (pollingTimer) {
      clearInterval(pollingTimer)
      pollingTimer = null
    }
  }
}

const handleDeleteClick = (interview: InterviewWithResume) => {
  deleteItem.value = interview
  showDeleteConfirm.value = true
}

const handleDeleteConfirm = async () => {
  if (!deleteItem.value) return
  
  deletingSessionId.value = deleteItem.value.sessionId
  try {
    await historyApi.deleteInterview(deleteItem.value.sessionId)
    await loadAllInterviews()
    showDeleteConfirm.value = false
    deleteItem.value = null
  } catch (err) {
    window.$message.error('删除失败，请稍后重试')
  } finally {
    deletingSessionId.value = null
  }
}

const handleExport = async (sessionId: string) => {
  exporting.value = sessionId
  try {
    const blob = await historyApi.exportInterviewPdf(sessionId)
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `面试报告_${sessionId.slice(-8)}.pdf`
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

const viewInterview = (sessionId: string, resumeId?: number) => {
  router.push({
    path: `/interview/session`,
    query: { sessionId, resumeId }
  })
}

const handleCheck = () => {
}

onMounted(() => {
  loadAllInterviews()
})

onUnmounted(() => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
})

watch(interviews, () => {
  startPollingIfNeeded()
})
</script>