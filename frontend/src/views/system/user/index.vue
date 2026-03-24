<template>
  <div class="space-y-4">
    <n-card :bordered="false">
      <div class="flex-between mb-4">
        <div class="flex items-center gap-2">
          <span class="i-carbon-user-multiple text-primary text-xl"></span>
          <span class="text-lg font-semibold">用户列表</span>
        </div>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <span class="i-carbon-add"></span>
          </template>
          新增用户
        </n-button>
      </div>

      <div class="flex flex-wrap gap-3 mb-4">
        <n-input
          v-model:value="searchForm.keyword"
          placeholder="搜索用户名/昵称/邮箱/手机号"
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
        :row-key="(row: User) => row.id"
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

    <n-modal v-model:show="modalVisible" preset="card" :title="modalTitle" class="w-[520px]" :bordered="false">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80">
        <n-form-item label="用户名" path="username">
          <n-input v-model:value="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </n-form-item>
        <n-form-item label="密码" path="password">
          <n-input
            v-model:value="form.password"
            type="password"
            placeholder="请输入密码"
            show-password-on="click"
          />
        </n-form-item>
        <n-form-item label="昵称" path="nickname">
          <n-input v-model:value="form.nickname" placeholder="请输入昵称" />
        </n-form-item>
        <n-form-item label="邮箱" path="email">
          <n-input v-model:value="form.email" placeholder="请输入邮箱" />
        </n-form-item>
        <n-form-item label="手机号" path="phone">
          <n-input v-model:value="form.phone" placeholder="请输入手机号" />
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

    <n-modal v-model:show="roleModalVisible" preset="card" title="分配角色" class="w-[400px]" :bordered="false">
      <n-checkbox-group v-model:value="selectedRoleIds">
        <n-space>
          <n-checkbox v-for="role in roleList" :key="role.id" :value="role.id" :label="role.roleName" />
        </n-space>
      </n-checkbox-group>
      <template #footer>
        <div class="flex justify-end gap-2">
          <n-button @click="roleModalVisible = false">取消</n-button>
          <n-button type="primary" :loading="roleSubmitLoading" @click="handleAssignRole">确定</n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, h, onMounted, computed } from 'vue'
import { NButton, NSpace, NTag, NSwitch } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { userApi, roleApi } from '@/api'
import type { User, Role } from '@/types'

const loading = ref(false)
const tableData = ref<User[]>([])
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

const modalVisible = ref(false)
const modalTitle = ref('新增用户')
const isEdit = ref(false)
const formRef = ref<FormInst | null>(null)
const submitLoading = ref(false)
const currentUserId = ref<number | null>(null)

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  status: 1,
  deptId: undefined as number | undefined
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const roleModalVisible = ref(false)
const roleSubmitLoading = ref(false)
const roleList = ref<Role[]>([])
const selectedRoleIds = ref<number[]>([])
const assignUserId = ref<number | null>(null)

const columns: DataTableColumns<User> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '用户名', key: 'username' },
  { title: '昵称', key: 'nickname' },
  { title: '邮箱', key: 'email' },
  { title: '手机号', key: 'phone' },
  {
    title: '角色',
    key: 'roles',
    render(row) {
      return h(NSpace, { size: 'small' }, {
        default: () => row.roles?.map(role => h(NTag, { type: 'info', size: 'small', round: true }, { default: () => role }))
      })
    }
  },
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
    width: 220,
    render(row) {
      return h(NSpace, { size: 'small' }, {
        default: () => [
          h(NButton, { size: 'small', quaternary: true, type: 'primary', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
          h(NButton, { size: 'small', quaternary: true, type: 'info', onClick: () => handleAssignRoleClick(row) }, { default: () => '分配角色' }),
          h(NButton, { size: 'small', quaternary: true, type: 'error', onClick: () => handleDelete(row) }, { default: () => '删除' })
        ]
      })
    }
  }
]

const loadData = async () => {
  loading.value = true
  try {
    const res = await userApi.getPage({
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
  modalTitle.value = '新增用户'
  isEdit.value = false
  Object.assign(form, {
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    status: 1
  })
  modalVisible.value = true
}

const handleEdit = (row: User) => {
  modalTitle.value = '编辑用户'
  isEdit.value = true
  currentUserId.value = row.id
  Object.assign(form, {
    username: row.username,
    password: '123456',
    nickname: row.nickname,
    email: row.email,
    phone: row.phone,
    status: row.status
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitLoading.value = true
    
    if (isEdit.value && currentUserId.value) {
      await userApi.update({ id: currentUserId.value, ...form })
    } else {
      await userApi.save(form)
    }
    
    window.$message.success('操作成功')
    modalVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleStatusChange = async (row: User, val: boolean) => {
  try {
    await userApi.update({ id: row.id, status: val ? 1 : 0 })
    window.$message.success('状态更新成功')
    loadData()
  } catch {}
}

const handleDelete = async (row: User) => {
  window.$dialog.warning({
    title: '确认删除',
    content: `确定要删除用户 "${row.username}" 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await userApi.delete(row.id)
        window.$message.success('删除成功')
        loadData()
      } catch {}
    }
  })
}

const loadRoleList = async () => {
  const res = await roleApi.listAll()
  roleList.value = res.data
}

const handleAssignRoleClick = async (row: User) => {
  assignUserId.value = row.id
  await loadRoleList()
  
  selectedRoleIds.value = (row.roles as unknown as { id: number }[])?.map(role => role.id) || []
  
  roleModalVisible.value = true
}

const handleAssignRole = async () => {
  if (!assignUserId.value) return
  
  try {
    roleSubmitLoading.value = true
    await userApi.assignRoles(assignUserId.value, selectedRoleIds.value)
    window.$message.success('分配角色成功')
    roleModalVisible.value = false
    loadData()
  } finally {
    roleSubmitLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>