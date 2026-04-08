<template>
  <ConfirmDialog
    :open="open"
    @update:open="emit('update:open', $event)"
    :title="`删除${itemType}`"
    :message="customMessage || defaultMessage"
    confirm-text="确定删除"
    cancel-text="取消"
    confirm-variant="danger"
    :loading="loading"
    @confirm="onConfirm"
    @cancel="onCancel"
  />
</template>

<script setup lang="ts">
/**
 * 删除确认对话框组件
 * - 简化删除确认对话框的使用
 * - 自动生成删除提示消息
 */
import { computed } from 'vue'
import ConfirmDialog from './ConfirmDialog.vue'

interface DeleteItem {
  id: number
  name?: string
  title?: string
  filename?: string
  sessionId?: string
  [key: string]: unknown
}

interface Props {
  open: boolean
  item: DeleteItem | null
  itemType: string
  loading?: boolean
  customMessage?: string
  onConfirm: () => void
  onCancel: () => void
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const emit = defineEmits<{
  'update:open': [value: boolean]
}>()

const getItemName = () => {
  if (!props.item) return ''
  return props.item.name || props.item.title || props.item.filename || props.item.sessionId || 
    (props.item.id ? `ID: ${props.item.id}` : '')
}

const defaultMessage = computed(() => {
  if (!props.item) return ''
  return `确定要删除${props.itemType}"${getItemName()}"吗？删除后无法恢复。`
})
</script>