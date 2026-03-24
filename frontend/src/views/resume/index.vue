<template>
  <div class="space-y-4">
    <n-card :bordered="false">
      <div class="flex-between mb-4">
        <div class="flex items-center gap-2">
          <span class="i-carbon-document text-primary text-xl"></span>
          <span class="text-lg font-semibold">简历管理</span>
        </div>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <span class="i-carbon-add"></span>
          </template>
          新增简历
        </n-button>
      </div>

      <div class="flex flex-wrap gap-3 mb-4">
        <n-input
          v-model:value="searchForm.keyword"
          placeholder="搜索简历名称/姓名"
          class="w-64"
          clearable
          @keyup.enter="loadData"
        >
          <template #prefix>
            <span class="i-carbon-search text-gray-400"></span>
          </template>
        </n-input>
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
        :row-key="(row: Resume) => row.id"
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

    <n-modal v-model:show="modalVisible" preset="card" :title="modalTitle" class="w-[600px]" :bordered="false">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80">
        <n-form-item label="简历名称" path="name">
          <n-input v-model:value="form.name" placeholder="请输入简历名称" />
        </n-form-item>
        <n-form-item label="姓名" path="realName">
          <n-input v-model:value="form.realName" placeholder="请输入姓名" />
        </n-form-item>
        <n-form-item label="手机号" path="phone">
          <n-input v-model:value="form.phone" placeholder="请输入手机号" />
        </n-form-item>
        <n-form-item label="邮箱" path="email">
          <n-input v-model:value="form.email" placeholder="请输入邮箱" />
        </n-form-item>
        <n-form-item label="求职意向" path="intention">
          <n-input v-model:value="form.intention" placeholder="请输入求职意向" />
        </n-form-item>
        <n-form-item label="状态" path="status">
          <n-switch v-model:value="form.status" :checked-value="1" :unchecked-value="0">
            <template #checked>启用</template>
            <template #unchecked>禁用</template>
          </n-switch>
        </n-form-item>
      </n-form>
      <template #footer>
        <div class="flex justify-end gap-2">
          <n-button @click="modalVisible = false">取消</n-button>
          <n-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, h, onMounted, computed } from 'vue'
import { NButton, NSpace, NTag } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'

interface Resume {
  id: number
  name: string
  realName: string
  phone: string
  email: string
  intention: string
  status: number
  createTime: string
}

const loading = ref(false)
const tableData = ref<Resume[]>([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const pageCount = computed(() => {
  return Math.ceil(pagination.itemCount / pagination.pageSize)
})

const searchForm = reactive({
  keyword: ''
})

const modalVisible = ref(false)
const modalTitle = ref('新增简历')
const isEdit = ref(false)
const formRef = ref<FormInst | null>(null)
const submitLoading = ref(false)
const currentId = ref<number | null>(null)

const form = reactive({
  name: '',
  realName: '',
  phone: '',
  email: '',
  intention: '',
  status: 1
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入简历名称', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const columns: DataTableColumns<Resume> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '简历名称', key: 'name' },
  { title: '姓名', key: 'realName', width: 100 },
  { title: '手机号', key: 'phone', width: 130 },
  { title: '邮箱', key: 'email', ellipsis: { tooltip: true } },
  { title: '求职意向', key: 'intention', ellipsis: { tooltip: true } },
  {
    title: '状态',
    key: 'status',
    width: 80,
    render(row) {
      return h(NTag, {
        type: row.status === 1 ? 'success' : 'default',
        size: 'small',
        round: true
      }, { default: () => row.status === 1 ? '启用' : '禁用' })
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 180,
    render(row) {
      return h(NSpace, { size: 'small' }, {
        default: () => [
          h(NButton, { size: 'small', quaternary: true, type: 'primary', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
          h(NButton, { size: 'small', quaternary: true, type: 'info', onClick: () => handlePreview(row) }, { default: () => '预览' }),
          h(NButton, { size: 'small', quaternary: true, type: 'error', onClick: () => handleDelete(row) }, { default: () => '删除' })
        ]
      })
    }
  }
]

const loadData = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    tableData.value = [
      { id: 1, name: 'Java开发工程师简历', realName: '张三', phone: '13800138001', email: 'zhangsan@example.com', intention: 'Java开发工程师', status: 1, createTime: '2024-01-01' },
      { id: 2, name: '前端开发工程师简历', realName: '李四', phone: '13800138002', email: 'lisi@example.com', intention: '前端开发工程师', status: 1, createTime: '2024-01-02' }
    ]
    pagination.itemCount = 2
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
  pagination.page = 1
  loadData()
}

const handleAdd = () => {
  modalTitle.value = '新增简历'
  isEdit.value = false
  Object.assign(form, {
    name: '',
    realName: '',
    phone: '',
    email: '',
    intention: '',
    status: 1
  })
  modalVisible.value = true
}

const handleEdit = (row: Resume) => {
  modalTitle.value = '编辑简历'
  isEdit.value = true
  currentId.value = row.id
  Object.assign(form, {
    name: row.name,
    realName: row.realName,
    phone: row.phone,
    email: row.email,
    intention: row.intention,
    status: row.status
  })
  modalVisible.value = true
}

const handlePreview = (row: Resume) => {
  window.$message.info(`预览简历: ${row.name}`)
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitLoading.value = true
    await new Promise(resolve => setTimeout(resolve, 500))
    window.$message.success('操作成功')
    modalVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row: Resume) => {
  window.$dialog.warning({
    title: '确认删除',
    content: `确定要删除简历 "${row.name}" 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      window.$message.success('删除成功')
      loadData()
    }
  })
}

onMounted(() => {
  loadData()
})
</script>