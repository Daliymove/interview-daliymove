<template>
  <div class="space-y-4">
    <n-card :bordered="false">
      <div class="flex-between mb-4">
        <div class="flex items-center gap-2">
          <span class="i-carbon-locked text-primary text-xl"></span>
          <span class="text-lg font-semibold">权限列表</span>
        </div>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <span class="i-carbon-add"></span>
          </template>
          新增权限
        </n-button>
      </div>

      <div class="flex flex-wrap gap-3 mb-4">
        <n-input
          v-model:value="searchForm.keyword"
          placeholder="搜索权限名称/编码"
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
        :row-key="(row: Permission) => row.id"
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

    <n-modal v-model:show="modalVisible" preset="card" :title="modalTitle" class="w-[560px]" :bordered="false">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80">
        <n-grid :cols="2" :x-gap="16">
          <n-gi>
            <n-form-item label="权限名称" path="permissionName">
              <n-input v-model:value="form.permissionName" placeholder="请输入权限名称" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="权限编码" path="permissionCode">
              <n-input v-model:value="form.permissionCode" placeholder="请输入权限编码" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="资源类型" path="resourceType">
              <n-select
                v-model:value="form.resourceType"
                :options="resourceTypeOptions"
                placeholder="请选择资源类型"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="HTTP方法" path="method">
              <n-select
                v-model:value="form.method"
                :options="methodOptions"
                placeholder="请选择HTTP方法"
                clearable
              />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-form-item label="资源路径" path="resourceUrl">
          <n-input v-model:value="form.resourceUrl" placeholder="请输入资源路径" />
        </n-form-item>
        <n-form-item label="描述" path="description">
          <n-input v-model:value="form.description" placeholder="请输入描述" type="textarea" :rows="2" />
        </n-form-item>
        <n-grid :cols="2" :x-gap="16">
          <n-gi>
            <n-form-item label="排序" path="sort">
              <n-input-number v-model:value="form.sort" :min="0" class="w-full" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="状态" path="status">
              <n-switch v-model:value="form.status" :checked-value="1" :unchecked-value="0">
                <template #checked>启用</template>
                <template #unchecked>禁用</template>
              </n-switch>
            </n-form-item>
          </n-gi>
        </n-grid>
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
import { NButton, NSpace, NSwitch, NTag } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { permissionApi } from '@/api'
import type { Permission } from '@/types'

const loading = ref(false)
const tableData = ref<Permission[]>([])
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
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

const resourceTypeOptions = [
  { label: '菜单', value: 1 },
  { label: '按钮', value: 2 },
  { label: '接口', value: 3 }
]

const methodOptions = [
  { label: 'GET', value: 'GET' },
  { label: 'POST', value: 'POST' },
  { label: 'PUT', value: 'PUT' },
  { label: 'DELETE', value: 'DELETE' }
]

const modalVisible = ref(false)
const modalTitle = ref('新增权限')
const isEdit = ref(false)
const formRef = ref<FormInst | null>(null)
const submitLoading = ref(false)
const currentPermissionId = ref<number | null>(null)

const form = reactive({
  permissionName: '',
  permissionCode: '',
  resourceType: 1,
  resourceUrl: '',
  method: '',
  description: '',
  sort: 0,
  status: 1
})

const rules: FormRules = {
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }]
}

const columns: DataTableColumns<Permission> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '权限名称', key: 'permissionName', width: 120 },
  { title: '权限编码', key: 'permissionCode', width: 150 },
  {
    title: '类型',
    key: 'resourceType',
    width: 100,
    render(row) {
      const typeMap: Record<number, { label: string; type: 'info' | 'success' | 'warning' }> = {
        1: { label: '菜单', type: 'info' },
        2: { label: '按钮', type: 'success' },
        3: { label: '接口', type: 'warning' }
      }
      const item = typeMap[row.resourceType] || { label: '未知', type: 'info' }
      return h(NTag, { type: item.type, size: 'small', round: true }, { default: () => item.label })
    }
  },
  { title: '资源路径', key: 'resourceUrl', ellipsis: { tooltip: true } },
  {
    title: '方法',
    key: 'method',
    width: 80,
    render(row) {
      if (!row.method) return '-'
      const colorMap: Record<string, any> = {
        GET: 'success',
        POST: 'info',
        PUT: 'warning',
        DELETE: 'error'
      }
      return h(NTag, { type: colorMap[row.method] || 'default', size: 'small', round: true }, { default: () => row.method })
    }
  },
  { title: '排序', key: 'sort', width: 80 },
  {
    title: '状态',
    key: 'status',
    width: 100,
    render(row) {
      return h(NSwitch, {
        value: row.status === 1,
        onUpdateValue: (val: boolean) => handleStatusChange(row, val)
      })
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 140,
    render(row) {
      return h(NSpace, { size: 'small' }, {
        default: () => [
          h(NButton, { size: 'small', quaternary: true, type: 'primary', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
          h(NButton, { size: 'small', quaternary: true, type: 'error', onClick: () => handleDelete(row) }, { default: () => '删除' })
        ]
      })
    }
  }
]

const loadData = async () => {
  loading.value = true
  try {
    const res = await permissionApi.getPage({
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

const handleAdd = () => {
  modalTitle.value = '新增权限'
  isEdit.value = false
  Object.assign(form, {
    permissionName: '',
    permissionCode: '',
    resourceType: 1,
    resourceUrl: '',
    method: '',
    description: '',
    sort: 0,
    status: 1
  })
  modalVisible.value = true
}

const handleEdit = (row: Permission) => {
  modalTitle.value = '编辑权限'
  isEdit.value = true
  currentPermissionId.value = row.id
  Object.assign(form, {
    permissionName: row.permissionName,
    permissionCode: row.permissionCode,
    resourceType: row.resourceType,
    resourceUrl: row.resourceUrl,
    method: row.method,
    description: row.description,
    sort: row.sort,
    status: row.status
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitLoading.value = true
    
    if (isEdit.value && currentPermissionId.value) {
      await permissionApi.update({ id: currentPermissionId.value, ...form })
    } else {
      await permissionApi.save(form)
    }
    
    window.$message.success('操作成功')
    modalVisible.value = false
    loadData()
  } catch (error: any) {
    if (error.message) {
      window.$message.error(error.message)
    }
  } finally {
    submitLoading.value = false
  }
}

const handleStatusChange = async (row: Permission, val: boolean) => {
  try {
    await permissionApi.update({ id: row.id, status: val ? 1 : 0 })
    window.$message.success('状态更新成功')
    loadData()
  } catch (error: any) {
    window.$message.error(error.message)
  }
}

const handleDelete = async (row: Permission) => {
  window.$dialog.warning({
    title: '确认删除',
    content: `确定要删除权限 "${row.permissionName}" 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await permissionApi.delete(row.id)
        window.$message.success('删除成功')
        loadData()
      } catch (error: any) {
        window.$message.error(error.message)
      }
    }
  })
}

onMounted(() => {
  loadData()
})
</script>