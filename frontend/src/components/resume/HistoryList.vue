<template>
  <div class="w-full">
    <n-card class="mb-4">
      <div class="flex items-center gap-4">
        <n-input
          v-model:value="searchTerm"
          placeholder="搜索简历名称..."
          clearable
          class="flex-1"
        >
          <template #prefix>
            <span class="i-carbon-search text-gray-400"></span>
          </template>
        </n-input>
      </div>
    </n-card>

    <n-spin :show="loading">
      <template v-if="!loading && filteredResumes.length === 0">
        <n-card class="text-center py-20">
          <span class="i-carbon-document text-5xl text-gray-300 mb-4 block"></span>
          <h3 class="text-xl font-semibold text-gray-700 mb-2">暂无简历记录</h3>
          <p class="text-gray-500">点击上方"上传简历"按钮开始</p>
        </n-card>
      </template>

      <template v-if="!loading && filteredResumes.length > 0">
        <n-card>
          <n-data-table
            :columns="columns"
            :data="filteredResumes"
            :row-key="(row: ResumeListItem) => row.id"
            :bordered="false"
            :row-props="rowProps"
          />
        </n-card>
      </template>
    </n-spin>

    <DeleteConfirmDialog
      v-model:open="deleteDialogVisible"
      :item="deleteItem ? { id: deleteItem.id, name: deleteItem.filename } : null"
      item-type="简历"
      :loading="deletingId !== null"
      @confirm="handleDeleteConfirm"
      @cancel="deleteDialogVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * 简历历史列表组件
 * - 显示简历列表
 * - 统计信息
 * - 搜索、下载、删除功能
 */
import { ref, computed, h, onMounted } from 'vue'
import { NButton, NTag, NSpace, NProgress } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { historyApi } from '@/api/history'
import type { ResumeListItem, AnalyzeStatus } from '@/types/resume'
import DeleteConfirmDialog from '../common/DeleteConfirmDialog.vue'
import { formatDate } from '@/utils/date'
import { getScoreColor } from '@/utils/score'

const emit = defineEmits<{
  selectResume: [id: number]
  resumeDeleted: []
}>()

const resumes = ref<ResumeListItem[]>([])
const loading = ref(true)
const searchTerm = ref('')
const deletingId = ref<number | null>(null)
const deleteItem = ref<ResumeListItem | null>(null)
const deleteDialogVisible = ref(false)
const reanalyzingId = ref<number | null>(null)

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

const getStatusIcon = (status?: AnalyzeStatus, hasScore?: boolean) => {
  if (status === undefined) {
    return hasScore ? 'i-carbon-checkmark-filled text-emerald-500' : 'i-carbon-time text-yellow-500'
  }
  switch (status) {
    case 'COMPLETED':
      return 'i-carbon-checkmark-filled text-emerald-500'
    case 'PROCESSING':
      return 'i-carbon-renew text-blue-500 animate-spin'
    case 'PENDING':
      return 'i-carbon-time text-yellow-500'
    case 'FAILED':
      return 'i-carbon-warning text-red-500'
    default:
      return 'i-carbon-checkmark-filled text-emerald-500'
  }
}

const getStatusText = (status?: AnalyzeStatus, hasScore?: boolean): string => {
  if (status === undefined) {
    return hasScore ? '已完成' : '待分析'
  }
  switch (status) {
    case 'COMPLETED':
      return '已完成'
    case 'PROCESSING':
      return '分析中'
    case 'PENDING':
      return '待分析'
    case 'FAILED':
      return '失败'
    default:
      return '未知'
  }
}

const columns: DataTableColumns<ResumeListItem> = [
  {
    title: '名称',
    key: 'filename',
    render(row) {
      return h('div', { class: 'flex items-center gap-3' }, [
        h('span', { class: 'i-carbon-document text-slate-400' }),
        h('span', { class: 'font-medium text-slate-800' }, row.filename)
      ])
    }
  },
  {
    title: '大小',
    key: 'fileSize',
    width: 90,
    render(row) {
      return formatFileSize(row.fileSize)
    }
  },
  {
    title: '分析状态',
    key: 'analyzeStatus',
    width: 110,
    render(row) {
      return h('div', { class: 'flex items-center gap-2' }, [
        h('span', { class: getStatusIcon(row.analyzeStatus, row.latestScore !== undefined) }),
        h('span', { class: 'text-sm text-slate-600' }, getStatusText(row.analyzeStatus, row.latestScore !== undefined))
      ])
    }
  },
  {
    title: 'AI 评分',
    key: 'latestScore',
    width: 160,
    render(row) {
      if (row.latestScore !== undefined) {
        return h('div', { class: 'flex items-center gap-3' }, [
          h('div', { class: 'flex-1' }, [
            h(NProgress, {
              type: 'line',
              percentage: row.latestScore,
              showIndicator: false,
              height: 6,
              borderRadius: 3
            })
          ]),
          h('span', { class: `font-bold w-8 text-right ${getScoreColor(row.latestScore)}` }, row.latestScore)
        ])
      }
      return h('span', { class: 'text-slate-400' }, '-')
    }
  },
  {
    title: '面试',
    key: 'interviewCount',
    width: 90,
    render(row) {
      if (row.interviewCount > 0) {
        return h(NTag, { type: 'success', round: true }, { default: () => `${row.interviewCount} 次` })
      }
      return h(NTag, { type: 'default', round: true }, { default: () => '待面试' })
    }
  },
  {
    title: '上传时间',
    key: 'uploadedAt',
    width: 110,
    render(row) {
      return formatDate(row.uploadedAt)
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 180,
    render(row) {
      return h(NSpace, { size: 'small' }, {
        default: () => [
          h(NButton, {
            size: 'small',
            quaternary: true,
            type: 'primary',
            onClick: (e: MouseEvent) => {
              e.stopPropagation()
              emit('selectResume', row.id)
            }
          }, { default: () => '查看' }),
          row.storageUrl ? h(NButton, {
            size: 'small',
            quaternary: true,
            type: 'primary',
            onClick: (e: MouseEvent) => {
              e.stopPropagation()
              handleDownload(row)
            }
          }, { default: () => '下载' }) : null,
          (row.analyzeStatus === 'FAILED' || row.analyzeStatus === 'PROCESSING') ? h(NButton, {
            size: 'small',
            quaternary: true,
            type: 'info',
            loading: reanalyzingId.value === row.id,
            onClick: (e: MouseEvent) => {
              e.stopPropagation()
              handleReanalyze(row.id)
            }
          }, { default: () => '重新分析' }) : null,
          h(NButton, {
            size: 'small',
            quaternary: true,
            type: 'error',
            loading: deletingId.value === row.id,
            onClick: (e: MouseEvent) => {
              e.stopPropagation()
              handleDeleteClick(row)
            }
          }, { default: () => '删除' })
        ]
      })
    }
  }
]

const filteredResumes = computed(() => {
  if (!resumes.value || !Array.isArray(resumes.value)) {
    return []
  }
  return resumes.value.filter(resume =>
    resume.filename.toLowerCase().includes(searchTerm.value.toLowerCase())
  )
})

const loadData = async () => {
  loading.value = true
  try {
    const resumeData = await historyApi.getResumes()
    resumes.value = Array.isArray(resumeData) ? resumeData : []
  } catch (err) {
    console.error('加载数据失败', err)
    resumes.value = []
  } finally {
    loading.value = false
  }
}

const handleDownload = (resume: ResumeListItem) => {
  if (resume.storageUrl) {
    const link = document.createElement('a')
    link.href = resume.storageUrl
    link.download = resume.filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

const handleReanalyze = async (id: number) => {
  try {
    reanalyzingId.value = id
    await historyApi.reanalyze(id)
    await loadData()
  } catch (err) {
    console.error('重新分析失败', err)
  } finally {
    reanalyzingId.value = null
  }
}

const handleDeleteClick = (resume: ResumeListItem) => {
  deleteItem.value = resume
  deleteDialogVisible.value = true
}

const handleDeleteConfirm = async () => {
  if (!deleteItem.value) return

  deletingId.value = deleteItem.value.id
  try {
    await historyApi.deleteResume(deleteItem.value.id)
    await loadData()
    emit('resumeDeleted')
    deleteDialogVisible.value = false
    deleteItem.value = null
  } catch (err) {
    window.$message.error(err instanceof Error ? err.message : '删除失败，请稍后重试')
  } finally {
    deletingId.value = null
  }
}

const rowProps = (row: ResumeListItem) => {
  return {
    style: 'cursor: pointer;',
    onClick: () => emit('selectResume', row.id)
  }
}

onMounted(() => {
  loadData()
})
</script>