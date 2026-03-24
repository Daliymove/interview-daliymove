<template>
  <div class="space-y-4">
    <n-card :bordered="false">
      <div class="flex-between mb-4">
        <div class="flex items-center gap-2">
          <span class="i-carbon-user-role text-primary text-xl"></span>
          <span class="text-lg font-semibold">角色列表</span>
        </div>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <span class="i-carbon-add"></span>
          </template>
          新增角色
        </n-button>
      </div>

      <div class="flex flex-wrap gap-3 mb-4">
        <n-input
          v-model:value="searchForm.keyword"
          placeholder="搜索角色名称/编码"
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
        :row-key="(row: Role) => row.id"
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
        <n-form-item label="角色名称" path="roleName">
          <n-input v-model:value="form.roleName" placeholder="请输入角色名称" />
        </n-form-item>
        <n-form-item label="角色编码" path="roleCode">
          <n-input v-model:value="form.roleCode" placeholder="请输入角色编码" :disabled="isEdit" />
        </n-form-item>
        <n-form-item label="描述" path="description">
          <n-input v-model:value="form.description" placeholder="请输入描述" type="textarea" :rows="3" />
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

    <n-modal v-model:show="permissionModalVisible" preset="card" title="分配权限" class="w-[500px]" :bordered="false">
      <n-checkbox-group v-model:value="selectedPermissionIds">
        <n-space>
          <n-checkbox
            v-for="permission in permissionList"
            :key="permission.id"
            :value="permission.id"
            :label="permission.permissionName"
          />
        </n-space>
      </n-checkbox-group>
      <template #footer>
        <div class="flex justify-end gap-2">
          <n-button @click="permissionModalVisible = false">取消</n-button>
          <n-button type="primary" :loading="permissionSubmitLoading" @click="handleAssignPermission">确定</n-button>
        </div>
      </template>
    </n-modal>

    <n-modal v-model:show="menuModalVisible" preset="card" title="分配菜单" class="w-[500px]" :bordered="false">
      <n-tree
        :data="menuTreeData"
        :checked-keys="selectedMenuIds"
        checkable
        cascade
        selectable
        @update:checked-keys="handleMenuCheck"
      />
      <template #footer>
        <div class="flex justify-end gap-2">
          <n-button @click="menuModalVisible = false">取消</n-button>
          <n-button type="primary" :loading="menuSubmitLoading" @click="handleAssignMenu">确定</n-button>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, h, onMounted, computed } from 'vue'
import { NButton, NSpace, NSwitch } from 'naive-ui'
import type { DataTableColumns, FormInst, FormRules, TreeOption } from 'naive-ui'
import { roleApi, permissionApi, menuApi } from '@/api'
import type { Role, Permission, Menu } from '@/types'

const loading = ref(false)
const tableData = ref<Role[]>([])
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
const modalTitle = ref('新增角色')
const isEdit = ref(false)
const formRef = ref<FormInst | null>(null)
const submitLoading = ref(false)
const currentRoleId = ref<number | null>(null)

const form = reactive({
  roleName: '',
  roleCode: '',
  description: '',
  status: 1
})

const rules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const permissionModalVisible = ref(false)
const permissionSubmitLoading = ref(false)
const permissionList = ref<Permission[]>([])
const selectedPermissionIds = ref<number[]>([])
const assignRoleId = ref<number | null>(null)

const menuModalVisible = ref(false)
const menuSubmitLoading = ref(false)
const menuTreeData = ref<TreeOption[]>([])
const selectedMenuIds = ref<string[]>([])

const columns: DataTableColumns<Role> = [
  { title: 'ID', key: 'id', width: 80 },
  { title: '角色名称', key: 'roleName' },
  { title: '角色编码', key: 'roleCode' },
  { title: '描述', key: 'description', ellipsis: { tooltip: true } },
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
    width: 300,
    render(row) {
      return h(NSpace, { size: 'small' }, {
        default: () => [
          h(NButton, { size: 'small', quaternary: true, type: 'primary', onClick: () => handleEdit(row) }, { default: () => '编辑' }),
          h(NButton, { size: 'small', quaternary: true, type: 'info', onClick: () => handleAssignPermissionClick(row) }, { default: () => '分配权限' }),
          h(NButton, { size: 'small', quaternary: true, type: 'warning', onClick: () => handleAssignMenuClick(row) }, { default: () => '分配菜单' }),
          h(NButton, { size: 'small', quaternary: true, type: 'error', onClick: () => handleDelete(row) }, { default: () => '删除' })
        ]
      })
    }
  }
]

const loadData = async () => {
  loading.value = true
  try {
    const res = await roleApi.getPage({
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
  modalTitle.value = '新增角色'
  isEdit.value = false
  Object.assign(form, {
    roleName: '',
    roleCode: '',
    description: '',
    status: 1
  })
  modalVisible.value = true
}

const handleEdit = (row: Role) => {
  modalTitle.value = '编辑角色'
  isEdit.value = true
  currentRoleId.value = row.id
  Object.assign(form, {
    roleName: row.roleName,
    roleCode: row.roleCode,
    description: row.description,
    status: row.status
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitLoading.value = true
    
    if (isEdit.value && currentRoleId.value) {
      await roleApi.update({ id: currentRoleId.value, ...form })
    } else {
      await roleApi.save(form)
    }
    
    window.$message.success('操作成功')
    modalVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleStatusChange = async (row: Role, val: boolean) => {
  try {
    await roleApi.update({ id: row.id, status: val ? 1 : 0 })
    window.$message.success('状态更新成功')
    loadData()
  } catch {}
}

const handleDelete = async (row: Role) => {
  window.$dialog.warning({
    title: '确认删除',
    content: `确定要删除角色 "${row.roleName}" 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await roleApi.delete(row.id)
        window.$message.success('删除成功')
        loadData()
      } catch {}
    }
  })
}

const loadPermissionList = async () => {
  const res = await permissionApi.listAll()
  permissionList.value = res.data
}

const handleAssignPermissionClick = async (row: Role) => {
  assignRoleId.value = row.id
  await loadPermissionList()
  
  const roleRes = await roleApi.getById(row.id)
  selectedPermissionIds.value = roleRes.data.permissionIds || []
  
  permissionModalVisible.value = true
}

const handleAssignPermission = async () => {
  if (!assignRoleId.value) return
  
  try {
    permissionSubmitLoading.value = true
    await roleApi.assignPermissions(assignRoleId.value, selectedPermissionIds.value)
    window.$message.success('分配权限成功')
    permissionModalVisible.value = false
  } finally {
    permissionSubmitLoading.value = false
  }
}

const loadMenuTree = async () => {
  const res = await menuApi.getTree()
  menuTreeData.value = buildTreeOptions(res.data)
}

const buildTreeOptions = (menus: Menu[]): TreeOption[] => {
  return menus.map(menu => ({
    key: menu.id.toString(),
    label: menu.menuName,
    children: menu.children ? buildTreeOptions(menu.children) : undefined
  }))
}

const handleAssignMenuClick = async (row: Role) => {
  assignRoleId.value = row.id
  await loadMenuTree()
  
  const roleRes = await roleApi.getById(row.id)
  selectedMenuIds.value = (roleRes.data.menuIds || []).map(id => id.toString())
  
  menuModalVisible.value = true
}

const handleMenuCheck = (keys: Array<string | number>) => {
  selectedMenuIds.value = keys.map(k => k.toString())
}

const handleAssignMenu = async () => {
  if (!assignRoleId.value) return
  
  try {
    menuSubmitLoading.value = true
    const menuIds = selectedMenuIds.value.map(id => parseInt(id))
    await roleApi.assignMenus(assignRoleId.value, menuIds)
    window.$message.success('分配菜单成功')
    menuModalVisible.value = false
  } finally {
    menuSubmitLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>