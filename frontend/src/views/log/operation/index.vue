<template>
  <div class="space-y-4">
    <n-card :bordered="false">
      <div class="flex-between mb-4">
        <div class="flex items-center gap-2">
          <span class="i-carbon-document text-primary text-xl"></span>
          <span class="text-lg font-semibold">操作日志</span>
        </div>
      </div>

      <div class="flex flex-wrap gap-3 mb-4">
        <n-input
          v-model:value="searchForm.keyword"
          placeholder="搜索用户名/操作描述"
          class="w-64"
          clearable
          @keyup.enter="loadData"
        >
          <template #prefix>
            <span class="i-carbon-search text-gray-400"></span>
          </template>
        </n-input>
        <n-select
          v-model:value="searchForm.status"
          :options="statusOptions"
          placeholder="状态筛选"
          class="w-32"
          clearable
        />
        <n-button type="primary" @click="loadData">
          <template #icon>
            <span class="i-carbon-search"></span>
          </template>
          查询
        </n-button>
        <n-button @click="resetSearch">
          <template #icon>
            <span class="i-carbon-reset"></span>
          </template>
          重置
        </n-button>
      </div>

      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="false"
        :row-key="(row: OperationLog) => row.id"
        :bordered="false"
      />
      <div class="flex justify-end mt-4">
        <n-pagination
          v-model:page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-count="pageCount"
          :page-sizes="[10, 20, 50]"
          show-size-picker
          show-quick-jumper
          @update:page="handlePageChange"
          @update:page-size="handlePageSizeChange"
        >
          <template #prefix>共 {{ pagination.itemCount }} 条</template>
        </n-pagination>
      </div>
    </n-card>

    <n-modal v-model:show="detailVisible" preset="card" title="日志详情" class="w-[700px]" :bordered="false">
      <n-descriptions label-placement="left" :column="2">
        <n-descriptions-item label="日志ID">{{ currentLog?.id }}</n-descriptions-item>
        <n-descriptions-item label="用户名">{{ currentLog?.username || '-' }}</n-descriptions-item>
        <n-descriptions-item label="操作描述" :span="2">{{ currentLog?.operation }}</n-descriptions-item>
        <n-descriptions-item label="请求方法" :span="2">{{ currentLog?.method }}</n-descriptions-item>
        <n-descriptions-item label="请求参数" :span="2">
          <n-code :code="formatParams(currentLog?.params)" language="json" />
        </n-descriptions-item>
        <n-descriptions-item label="IP地址">{{ currentLog?.ip }}</n-descriptions-item>
        <n-descriptions-item label="执行时间">{{ currentLog?.executeTime }}ms</n-descriptions-item>
        <n-descriptions-item label="状态">
          <n-tag :type="currentLog?.status === 1 ? 'success' : 'error'" size="small">
            {{ currentLog?.status === 1 ? '成功' : '失败' }}
          </n-tag>
        </n-descriptions-item>
        <n-descriptions-item label="创建时间">{{ currentLog?.createTime }}</n-descriptions-item>
        <n-descriptions-item v-if="currentLog?.status === 0" label="错误信息" :span="2">
          <n-text type="error">{{ currentLog?.errorMsg }}</n-text>
        </n-descriptions-item>
      </n-descriptions>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, h, onMounted, computed } from 'vue'
import { NButton, NSpace, NTag } from 'naive-ui'
import type { DataTableColumns } from 'naive-ui'
import { logApi } from '@/api'
import type { OperationLog } from '@/types'
import dayjs from 'dayjs'

const loading = ref(false)
const tableData = ref<OperationLog[]>([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const pageCount = computed(() => {
  return Math.ceil(pagination.itemCount / pagination.pageSize)
})

const searchForm = reactive({
  keyword: '',
  status: null as number | null
})

const statusOptions = [
  { label: '成功', value: 1 },
  { label: '失败', value: 0 }
]

const detailVisible = ref(false)
const currentLog = ref<OperationLog | null>(null)

const columns: DataTableColumns<OperationLog> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '用户名', key: 'username', width: 120 },
  { title: '操作描述', key: 'operation', ellipsis: { tooltip: true } },
  { title: 'IP地址', key: 'ip', width: 140 },
  {
    title: '状态',
    key: 'status',
    width: 80,
    render(row) {
      return h(NTag, {
        type: row.status === 1 ? 'success' : 'error',
        size: 'small',
        round: true
      }, { default: () => row.status === 1 ? '成功' : '失败' })
    }
  },
  {
    title: '执行时间',
    key: 'executeTime',
    width: 100,
    render(row) {
      return `${row.executeTime || 0}ms`
    }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
    render(row) {
      return row.createTime ? dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') : '-'
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 140,
    render(row) {
      return h(NSpace, { size: 'small' }, {
        default: () => [
          h(NButton, { size: 'small', quaternary: true, type: 'primary', onClick: () => handleDetail(row) }, { default: () => '详情' }),
          h(NButton, { size: 'small', quaternary: true, type: 'error', onClick: () => handleDelete(row) }, { default: () => '删除' })
        ]
      })
    }
  }
]

const loadData = async () => {
  loading.value = true
  try {
    const res = await logApi.getPage({
      pageNum: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword,
      status: searchForm.status
    })
    tableData.value = res.data.records
    pagination.itemCount = res.data.total
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadData()
}

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  loadData()
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.page = 1
  loadData()
}

const handleDetail = (row: OperationLog) => {
  currentLog.value = row
  detailVisible.value = true
}

const handleDelete = async (row: OperationLog) => {
  window.$dialog.warning({
    title: '确认删除',
    content: `确定要删除该操作日志吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await logApi.delete(row.id)
        window.$message.success('删除成功')
        loadData()
      } catch (error: any) {
        window.$message.error(error.message)
      }
    }
  })
}

const formatParams = (params?: string) => {
  if (!params) return ''
  try {
    return JSON.stringify(JSON.parse(params), null, 2)
  } catch {
    return params
  }
}

onMounted(() => {
  loadData()
})
</script>