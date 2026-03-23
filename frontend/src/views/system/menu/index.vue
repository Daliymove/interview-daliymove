<template>
  <div class="space-y-4">
    <n-card :bordered="false">
      <div class="flex-between mb-4">
        <div class="flex items-center gap-2">
          <span class="i-carbon-menu text-primary text-xl"></span>
          <span class="text-lg font-semibold">菜单列表</span>
        </div>
        <n-button type="primary" @click="handleAdd">
          <template #icon>
            <span class="i-carbon-add"></span>
          </template>
          新增菜单
        </n-button>
      </div>

      <n-data-table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :row-key="(row: Menu) => row.id"
        :bordered="false"
        default-expand-all
      />
    </n-card>

    <n-modal v-model:show="modalVisible" preset="card" :title="modalTitle" class="w-[600px]" :bordered="false">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80">
        <n-grid :cols="2" :x-gap="16">
          <n-gi>
            <n-form-item label="上级菜单" path="parentId">
              <n-tree-select
                v-model:value="form.parentId"
                :options="parentMenuOptions"
                placeholder="请选择上级菜单"
                clearable
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="菜单类型" path="menuType">
              <n-select
                v-model:value="form.menuType"
                :options="menuTypeOptions"
                placeholder="请选择菜单类型"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="菜单名称" path="menuName">
              <n-input v-model:value="form.menuName" placeholder="请输入菜单名称" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="菜单编码" path="menuCode">
              <n-input v-model:value="form.menuCode" placeholder="请输入菜单编码" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="路由路径" path="path">
              <n-input v-model:value="form.path" placeholder="请输入路由路径" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="组件路径" path="component">
              <n-input v-model:value="form.component" placeholder="请输入组件路径" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="图标" path="icon">
              <n-input v-model:value="form.icon" placeholder="请输入图标名称" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="排序" path="sort">
              <n-input-number v-model:value="form.sort" :min="0" class="w-full" />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-grid :cols="2" :x-gap="16">
          <n-gi>
            <n-form-item label="是否可见" path="visible">
              <n-switch v-model:value="form.visible" :checked-value="1" :unchecked-value="0">
                <template #checked>是</template>
                <template #unchecked>否</template>
              </n-switch>
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
import type { DataTableColumns, FormInst, FormRules, TreeSelectOption } from 'naive-ui'
import { menuApi } from '@/api'
import type { Menu } from '@/types'

const loading = ref(false)
const tableData = ref<Menu[]>([])

const modalVisible = ref(false)
const modalTitle = ref('新增菜单')
const isEdit = ref(false)
const formRef = ref<FormInst | null>(null)
const submitLoading = ref(false)
const currentMenuId = ref<number | null>(null)

const form = reactive({
  parentId: 0,
  menuName: '',
  menuCode: '',
  path: '',
  component: '',
  icon: '',
  menuType: 1,
  visible: 1,
  sort: 0,
  status: 1
})

const rules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuCode: [{ required: true, message: '请输入菜单编码', trigger: 'blur' }]
}

const menuTypeOptions = [
  { label: '目录', value: 1 },
  { label: '菜单', value: 2 },
  { label: '按钮', value: 3 }
]

const parentMenuOptions = computed<TreeSelectOption[]>(() => {
  const buildOptions = (menus: Menu[]): TreeSelectOption[] => {
    return menus.map(menu => ({
      key: menu.id,
      label: menu.menuName,
      children: menu.children ? buildOptions(menu.children) : undefined
    }))
  }
  
  return [
    { key: 0, label: '根目录' },
    ...buildOptions(tableData.value)
  ]
})

const columns: DataTableColumns<Menu> = [
  { title: '菜单名称', key: 'menuName', width: 200 },
  { title: '菜单编码', key: 'menuCode', width: 150 },
  { title: '路由路径', key: 'path', width: 180 },
  { title: '组件路径', key: 'component', width: 180, ellipsis: { tooltip: true } },
  { title: '图标', key: 'icon', width: 100 },
  {
    title: '类型',
    key: 'menuType',
    width: 100,
    render(row) {
      const typeMap: Record<number, { label: string; type: 'info' | 'success' | 'warning' }> = {
        1: { label: '目录', type: 'info' },
        2: { label: '菜单', type: 'success' },
        3: { label: '按钮', type: 'warning' }
      }
      const item = typeMap[row.menuType] || { label: '未知', type: 'info' }
      return h(NTag, { type: item.type, size: 'small', round: true }, { default: () => item.label })
    }
  },
  {
    title: '可见',
    key: 'visible',
    width: 80,
    render(row) {
      return h(NTag, { type: row.visible === 1 ? 'success' : 'default', size: 'small', round: true }, {
        default: () => row.visible === 1 ? '是' : '否'
      })
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
    const res = await menuApi.getTree()
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  modalTitle.value = '新增菜单'
  isEdit.value = false
  Object.assign(form, {
    parentId: 0,
    menuName: '',
    menuCode: '',
    path: '',
    component: '',
    icon: '',
    menuType: 1,
    visible: 1,
    sort: 0,
    status: 1
  })
  modalVisible.value = true
}

const handleEdit = (row: Menu) => {
  modalTitle.value = '编辑菜单'
  isEdit.value = true
  currentMenuId.value = row.id
  Object.assign(form, {
    parentId: row.parentId,
    menuName: row.menuName,
    menuCode: row.menuCode,
    path: row.path,
    component: row.component,
    icon: row.icon,
    menuType: row.menuType,
    visible: row.visible,
    sort: row.sort,
    status: row.status
  })
  modalVisible.value = true
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    submitLoading.value = true
    
    if (isEdit.value && currentMenuId.value) {
      await menuApi.update({ id: currentMenuId.value, ...form })
    } else {
      await menuApi.save(form)
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

const handleStatusChange = async (row: Menu, val: boolean) => {
  try {
    await menuApi.update({ id: row.id, status: val ? 1 : 0 })
    window.$message.success('状态更新成功')
    loadData()
  } catch (error: any) {
    window.$message.error(error.message)
  }
}

const handleDelete = async (row: Menu) => {
  window.$dialog.warning({
    title: '确认删除',
    content: `确定要删除菜单 "${row.menuName}" 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await menuApi.delete(row.id)
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