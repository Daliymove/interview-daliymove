<template>
  <div class="flex flex-col h-full">
    <div class="mb-6">
      <h1 class="text-2xl font-semibold text-gray-900 flex items-center gap-2">
        <span class="i-carbon-database text-primary text-2xl"></span>
        知识库管理
      </h1>
      <p class="text-gray-500 mt-1">管理您的知识库文件，查看使用统计</p>
    </div>

    <div v-if="stats" class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <n-card class="hover:shadow-md transition-shadow">
        <div class="flex items-center gap-3">
          <div class="p-3 rounded-lg bg-primary">
            <span class="i-carbon-database text-white text-2xl"></span>
          </div>
          <div>
            <p class="text-sm text-gray-500">知识库总数</p>
            <p class="text-2xl font-semibold text-gray-900">{{ (stats.totalCount || 0).toLocaleString() }}</p>
          </div>
        </div>
      </n-card>

      <n-card class="hover:shadow-md transition-shadow">
        <div class="flex items-center gap-3">
          <div class="p-3 rounded-lg bg-indigo-500">
            <span class="i-carbon-chat text-white text-2xl"></span>
          </div>
          <div>
            <p class="text-sm text-gray-500">总提问次数</p>
            <p class="text-2xl font-semibold text-gray-900">{{ (stats.totalQuestionCount || 0).toLocaleString() }}</p>
          </div>
        </div>
      </n-card>

      <n-card class="hover:shadow-md transition-shadow">
        <div class="flex items-center gap-3">
          <div class="p-3 rounded-lg bg-emerald-500">
            <span class="i-carbon-view text-white text-2xl"></span>
          </div>
          <div>
            <p class="text-sm text-gray-500">总访问次数</p>
            <p class="text-2xl font-semibold text-gray-900">{{ (stats.totalAccessCount || 0).toLocaleString() }}</p>
          </div>
        </div>
      </n-card>
    </div>

    <n-card class="mb-4">
      <div class="flex flex-wrap items-center gap-4">
        <div class="flex items-center gap-2 flex-1 min-w-[200px]">
          <span class="i-carbon-search text-gray-400"></span>
          <n-input
            v-model:value="searchKeyword"
            placeholder="搜索知识库名称..."
            :bordered="false"
            size="small"
            class="flex-1"
            @keyup.enter="handleSearch"
          />
        </div>
        <n-select
          v-model:value="sortBy"
          :options="sortOptions"
          placeholder="排序方式"
          class="w-[140px]"
          @update:value="handleSortChange"
        />
        <n-select
          v-model:value="selectedCategory"
          :options="categoryOptions"
          placeholder="全部分类"
          clearable
          class="w-[140px]"
          @update:value="handleCategoryChange"
        />
        <n-button type="primary" @click="handleUpload">
          <template #icon>
            <span class="i-carbon-upload"></span>
          </template>
          上传知识库
        </n-button>
        <n-button @click="handleChat">
          <template #icon>
            <span class="i-carbon-chat"></span>
          </template>
          问答助手
        </n-button>
      </div>
    </n-card>

    <div class="flex-1 min-h-0">
      <n-card class="h-full">
        <div v-if="loading" class="flex items-center justify-center py-20">
          <n-spin size="large" />
        </div>
        <div v-else-if="knowledgeBases.length === 0" class="text-center py-20">
          <span class="i-carbon-document text-gray-300 text-5xl block mx-auto mb-4"></span>
          <p class="text-gray-500">暂无知识库</p>
          <n-button type="primary" text class="mt-4" @click="handleUpload">
            上传第一个知识库
          </n-button>
        </div>
        <n-data-table
          v-else
          :columns="columns"
          :data="knowledgeBases"
          :row-key="(row: KnowledgeBaseItem) => row.id"
          :bordered="false"
        />
      </n-card>
    </div>

    <DeleteConfirmDialog
      :open="showDeleteDialog"
      @update:open="showDeleteDialog = $event"
      :item="deleteItem"
      item-type="知识库"
      :loading="deleting"
      :on-confirm="handleDelete"
      :on-cancel="handleCancelDelete"
    />
  </div>
</template>

<script setup lang="ts">
/**
 * 知识库管理页面
 * - 知识库列表展示
 * - 统计信息展示
 * - 搜索和筛选功能
 * - 分类管理功能
 * - 下载和删除功能
 */
import { ref, h, onMounted, onUnmounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NTag, NSpace, NSpin } from 'naive-ui'
import { knowledgeBaseApi } from '@/api/knowledgebase'
import DeleteConfirmDialog from '@/components/common/DeleteConfirmDialog.vue'
import type { KnowledgeBaseItem, KnowledgeBaseStats, SortOption, VectorStatus } from '@/types/knowledge'
import { formatDateTime } from '@/utils/date'

const router = useRouter()

const stats = ref<KnowledgeBaseStats | null>(null)
const knowledgeBases = ref<KnowledgeBaseItem[]>([])
const loading = ref(true)
const searchKeyword = ref('')
const sortBy = ref<SortOption>('time')
const selectedCategory = ref<string>('')
const categories = ref<string[]>([])
const deleteItem = ref<KnowledgeBaseItem | null>(null)
const showDeleteDialog = ref(false)
const deleting = ref(false)
const revectorizing = ref<number | null>(null)

const sortOptions = [
  { label: '按时间排序', value: 'time' },
  { label: '按大小排序', value: 'size' },
  { label: '按访问排序', value: 'access' },
  { label: '按提问排序', value: 'question' }
]

const categoryOptions = computed(() => {
  if (!categories.value || !Array.isArray(categories.value)) {
    return [{ label: '全部分类', value: '' }]
  }
  return [
    { label: '全部分类', value: '' },
    ...categories.value.map(cat => ({ label: cat, value: cat }))
  ]
})

let pollTimer: ReturnType<typeof setInterval> | null = null

const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

const getStatusIcon = (status: VectorStatus): string => {
  switch (status) {
    case 'COMPLETED': return 'i-carbon-checkmark-filled text-green-500'
    case 'PROCESSING': return 'i-carbon-renew text-blue-500 animate-spin'
    case 'PENDING': return 'i-carbon-time text-yellow-500'
    case 'FAILED': return 'i-carbon-warning-alt text-red-500'
    default: return 'i-carbon-checkmark-filled text-green-500'
  }
}

const getStatusText = (status: VectorStatus): string => {
  switch (status) {
    case 'COMPLETED': return '已完成'
    case 'PROCESSING': return '处理中'
    case 'PENDING': return '待处理'
    case 'FAILED': return '失败'
    default: return '未知'
  }
}

const getStatusTagType = (status: VectorStatus): 'success' | 'warning' | 'error' | 'default' => {
  switch (status) {
    case 'COMPLETED': return 'success'
    case 'PROCESSING': return 'warning'
    case 'PENDING': return 'default'
    case 'FAILED': return 'error'
    default: return 'default'
  }
}

const loadStats = async () => {
  try {
    stats.value = await knowledgeBaseApi.getStatistics()
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

const loadKnowledgeBases = async (showLoading = true) => {
  if (showLoading) loading.value = true
  try {
    let list: KnowledgeBaseItem[]
    if (searchKeyword.value) {
      list = await knowledgeBaseApi.search(searchKeyword.value)
    } else if (selectedCategory.value) {
      list = await knowledgeBaseApi.getByCategory(selectedCategory.value)
    } else {
      list = await knowledgeBaseApi.getAllKnowledgeBases(sortBy.value)
    }
    knowledgeBases.value = list
  } catch (error) {
    console.error('加载知识库列表失败', error)
  } finally {
    if (showLoading) loading.value = false
  }
}

const loadCategories = async () => {
  try {
    categories.value = await knowledgeBaseApi.getAllCategories()
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadData = async (showLoading = true) => {
  await Promise.all([
    loadStats(),
    loadKnowledgeBases(showLoading),
    loadCategories()
  ])
}

onMounted(() => {
  loadData()
})

const startPolling = () => {
  const hasPendingItems = knowledgeBases.value.some(
    kb => kb.vectorStatus === 'PENDING' || kb.vectorStatus === 'PROCESSING'
  )
  if (hasPendingItems && !loading.value) {
    pollTimer = setInterval(() => {
      loadData(false)
    }, 5000)
  }
}

const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

watch(knowledgeBases, () => {
  stopPolling()
  startPolling()
})

watch(loading, (newVal) => {
  if (!newVal) {
    startPolling()
  }
})

onUnmounted(() => {
  stopPolling()
})

const handleUpload = () => {
  router.push('/knowledge/upload')
}

const handleChat = () => {
  router.push('/knowledge/query')
}

const handleSearch = () => {
  loadKnowledgeBases()
}

const handleSortChange = () => {
  searchKeyword.value = ''
  selectedCategory.value = ''
  loadKnowledgeBases()
}

const handleCategoryChange = () => {
  searchKeyword.value = ''
  loadKnowledgeBases()
}

const handleDownload = async (kb: KnowledgeBaseItem) => {
  try {
    const blob = await knowledgeBaseApi.downloadKnowledgeBase(kb.id)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = kb.originalFilename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('下载失败:', error)
    window.$message.error('下载失败')
  }
}

const handleRevectorize = async (id: number) => {
  try {
    revectorizing.value = id
    await knowledgeBaseApi.revectorize(id)
    await loadData(false)
    window.$message.success('重新向量化已启动')
  } catch (error) {
    console.error('重新向量化失败', error)
    window.$message.error('重新向量化失败')
  } finally {
    revectorizing.value = null
  }
}

const handleDelete = async () => {
  if (!deleteItem.value) return
  try {
    deleting.value = true
    await knowledgeBaseApi.deleteKnowledgeBase(deleteItem.value.id)
    showDeleteDialog.value = false
    deleteItem.value = null
    await loadData()
    window.$message.success('删除成功')
  } catch (error) {
    console.error('删除失败:', error)
    window.$message.error('删除失败')
  } finally {
    deleting.value = false
  }
}

const handleCancelDelete = () => {
  showDeleteDialog.value = false
  deleteItem.value = null
}

const columns = [
  {
    title: '名称',
    key: 'name',
    render: (row: KnowledgeBaseItem) => h('div', { class: 'flex items-center gap-3' }, [
      h('span', { class: 'i-carbon-document text-gray-400' }),
      h('div', {}, [
        h('p', { class: 'font-medium text-gray-900' }, row.name),
        h('p', { class: 'text-xs text-gray-400' }, row.originalFilename)
      ])
    ])
  },
  {
    title: '分类',
    key: 'category',
    render: (row: KnowledgeBaseItem) => row.category
      ? h(NTag, { size: 'small', bordered: false }, { default: () => row.category })
      : h('span', { class: 'text-gray-400' }, '未分类')
  },
  {
    title: '大小',
    key: 'fileSize',
    render: (row: KnowledgeBaseItem) => formatFileSize(row.fileSize)
  },
  {
    title: '状态',
    key: 'vectorStatus',
    render: (row: KnowledgeBaseItem) => h(NSpace, { align: 'center', size: 'small' }, {
      default: () => [
        h('span', { class: getStatusIcon(row.vectorStatus) }),
        h(NTag, { type: getStatusTagType(row.vectorStatus), size: 'small', bordered: false }, {
          default: () => getStatusText(row.vectorStatus)
        })
      ]
    })
  },
  {
    title: '提问',
    key: 'questionCount',
    render: (row: KnowledgeBaseItem) => row.questionCount
  },
  {
    title: '上传时间',
    key: 'uploadedAt',
    render: (row: KnowledgeBaseItem) => formatDateTime(row.uploadedAt)
  },
  {
    title: '操作',
    key: 'actions',
    align: 'right' as const,
    render: (row: KnowledgeBaseItem) => h(NSpace, { align: 'center', size: 'small' }, {
      default: () => [
        h(NButton, {
          quaternary: true,
          size: 'small',
          onClick: () => handleDownload(row)
        }, {
          icon: () => h('span', { class: 'i-carbon-download' }),
          default: () => '下载'
        }),
        row.vectorStatus === 'FAILED' ? h(NButton, {
          quaternary: true,
          size: 'small',
          loading: revectorizing.value === row.id,
          onClick: () => handleRevectorize(row.id)
        }, {
          icon: () => h('span', { class: 'i-carbon-renew' }),
          default: () => '重试'
        }) : null,
        h(NButton, {
          quaternary: true,
          size: 'small',
          type: 'error',
          onClick: () => {
            deleteItem.value = row
            showDeleteDialog.value = true
          }
        }, {
          icon: () => h('span', { class: 'i-carbon-trash-can' }),
          default: () => '删除'
        })
      ]
    })
  }
]
</script>