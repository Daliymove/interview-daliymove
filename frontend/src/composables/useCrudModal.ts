/**
 * CRUD弹窗状态管理组合式函数
 * - 弹窗显示/隐藏状态
 * - 新增/编辑模式切换
 * - 表单引用管理
 * - 提交加载状态
 */
import { ref, reactive } from 'vue'
import type { FormInst } from 'naive-ui'

export interface UseCrudModalOptions<T> {
  defaultForm?: T
  onAdd?: () => void
  onEdit?: (id: number, data: T) => void
  onSubmit?: (isEdit: boolean, data: T) => Promise<void>
}

export function useCrudModal<T extends Record<string, any>>(options: UseCrudModalOptions<T> = {}) {
  const { defaultForm, onAdd, onEdit, onSubmit } = options

  const modalVisible = ref(false)
  const modalTitle = ref('新增')
  const isEdit = ref(false)
  const formRef = ref<FormInst | null>(null)
  const submitLoading = ref(false)
  const currentId = ref<number | null>(null)
  const form = reactive<T>((defaultForm || {}) as T)

  const openAdd = () => {
    modalTitle.value = '新增'
    isEdit.value = false
    currentId.value = null
    if (defaultForm) {
      Object.assign(form, defaultForm)
    }
    modalVisible.value = true
    onAdd?.()
  }

  const openEdit = (id: number, data: Partial<T>) => {
    modalTitle.value = '编辑'
    isEdit.value = true
    currentId.value = id
    Object.assign(form, data)
    modalVisible.value = true
    onEdit?.(id, form as T)
  }

  const closeModal = () => {
    modalVisible.value = false
    formRef.value?.restoreValidation()
  }

  const handleSubmit = async () => {
    try {
      await formRef.value?.validate()
      submitLoading.value = true
      
      if (onSubmit) {
        await onSubmit(isEdit.value, form as T)
      }
      
      window.$message.success('操作成功')
      closeModal()
    } finally {
      submitLoading.value = false
    }
  }

  return {
    modalVisible,
    modalTitle,
    isEdit,
    formRef,
    submitLoading,
    currentId,
    form,
    openAdd,
    openEdit,
    closeModal,
    handleSubmit
  }
}