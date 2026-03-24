<template>
  <div class="space-y-4">
    <n-card :bordered="false">
      <div class="flex-between mb-4">
        <div class="flex items-center gap-2">
          <span class="i-carbon-building text-primary text-xl"></span>
          <span class="text-lg font-semibold">部门管理</span>
        </div>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <span class="i-carbon-add"></span>
          </template>
          新增部门
        </n-button>
      </div>

      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :row-key="(row: Dept) => row.id"
        :bordered="false"
        default-expand-all
      />
    </n-card>

    <n-modal v-model:show="modalVisible" preset="card" :title="modalTitle" class="w-[500px]" :bordered="false">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80">
        <n-form-item label="上级部门" path="parentId">
          <n-tree-select
            v-model:value="form.parentId"
            :options="parentDeptOptions"
            placeholder="请选择上级部门"
            clearable
          />
        </n-form-item>
        <n-form-item label="部门名称" path="deptName">
          <n-input v-model:value="form.deptName" placeholder="请输入部门名称" />
        </n-form-item>
        <n-form-item label="负责人" path="leader">
          <n-input v-model:value="form.leader" placeholder="请输入负责人" />
        </n-form-item>
        <n-form-item label="联系电话" path="phone">
          <n-input v-model:value="form.phone" placeholder="请输入联系电话" />
        </n-form-item>
        <n-form-item label="邮箱" path="email">
          <n-input v-model:value="form.email" placeholder="请输入邮箱" />
        </n-form-item>
        <n-form-item label="排序" path="sort">
          <n-input-number v-model:value="form.sort" :min="0" class="w-full" />
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
import type { DataTableColumns, FormInst, FormRules, TreeSelectOption } from 'naive-ui'

interface Dept {
  id: number
  deptName: string
  parentId: number
  leader?: string
  phone?: string
  email?: string
  status: number
  sort: number
  children?: Dept[]
}

const loading = ref(false)
const tableData = ref<Dept[]>([])

const modalVisible = ref(false)
const modalTitle = ref('新增部门')
const isEdit = ref(false)
const formRef = ref<FormInst | null>(null)
const submitLoading = ref(false)
const currentId = ref<number | null>(null)

const form = reactive({
  parentId: 0,
  deptName: '',
  leader: '',
  phone: '',
  email: '',
  sort: 0,
  status: 1
})

const rules: FormRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

const parentDeptOptions = computed<TreeSelectOption[]>(() => {
  const buildOptions = (depts: Dept[]): TreeSelectOption[] => {
    return depts.map(dept => ({
      key: dept.id,
      label: dept.deptName,
      children: dept.children ? buildOptions(dept.children) : undefined
    }))
  }
  
  return [
    { key: 0, label: '根部门' },
    ...buildOptions(tableData.value)
  ]
})

const columns: DataTableColumns<Dept> = [
  { title: '部门名称', key: 'deptName', width: 200 },
  { title: '负责人', key: 'leader', width: 120 },
  { title: '联系电话', key: 'phone', width: 140 },
  { title: '邮箱', key: 'email', ellipsis: { tooltip: true } },
  { title: '排序', key: 'sort', width: 80 },
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
    await new Promise(resolve => setTimeout(resolve, 500))
    tableData.value = [
      { 
        id: 1, 
        deptName: '总公司', 
        parentId: 0, 
        leader: 'admin', 
        phone: '13800138000', 
        email: 'admin@example.com',
        status: 1, 
        sort: 1,
        children: [
          { id: 2, deptName: '技术部', parentId: 1, leader: '张三', phone: '13800138001', email: 'tech@example.com', status: 1, sort: 1 },
          { id: 3, deptName: '产品部', parentId: 1, leader: '李四', phone: '13800138002', email: 'product@example.com', status: 1, sort: 2 },
          { id: 4, deptName: '运营部', parentId: 1, leader: '王五', phone: '13800138003', email: 'operation@example.com', status: 1, sort: 3 }
        ]
      }
    ]
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  modalTitle.value = '新增部门'
  isEdit.value = false
  Object.assign(form, {
    parentId: 0,
    deptName: '',
    leader: '',
    phone: '',
    email: '',
    sort: 0,
    status: 1
  })
  modalVisible.value = true
}

const handleEdit = (row: Dept) => {
  modalTitle.value = '编辑部门'
  isEdit.value = true
  currentId.value = row.id
  Object.assign(form, {
    parentId: row.parentId,
    deptName: row.deptName,
    leader: row.leader || '',
    phone: row.phone || '',
    email: row.email || '',
    sort: row.sort,
    status: row.status
  })
  modalVisible.value = true
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

const handleDelete = (row: Dept) => {
  window.$dialog.warning({
    title: '确认删除',
    content: `确定要删除部门 "${row.deptName}" 吗？`,
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