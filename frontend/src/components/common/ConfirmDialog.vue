<template>
  <n-modal
    :show="open"
    @update:show="emit('update:open', $event)"
    preset="card"
    :title="title"
    class="w-[400px]"
    :bordered="false"
    :mask-closable="!loading"
  >
    <div class="mb-4">
      <template v-if="typeof message === 'string'">
        <p class="whitespace-pre-line text-slate-600">{{ message }}</p>
      </template>
      <template v-else>
        <component :is="message" />
      </template>
    </div>

    <template v-if="customContent">
      <div class="mb-4">
        <component :is="customContent" />
      </div>
    </template>

    <template #footer>
      <div v-if="!hideButtons" class="flex justify-end gap-3">
        <n-button :disabled="loading" @click="handleCancel">
          {{ cancelText }}
        </n-button>
        <n-button
          :type="confirmButtonType"
          :loading="loading"
          :disabled="loading"
          @click="handleConfirm"
        >
          {{ loading ? '处理中...' : confirmText }}
        </n-button>
      </div>
    </template>
  </n-modal>
</template>

<script setup lang="ts">
/**
 * 确认对话框组件
 * - 支持自定义内容
 * - 支持不同变体（danger/primary/warning）
 * - 支持加载状态
 */
import { computed, type VNode } from 'vue'

interface Props {
  open: boolean
  title: string
  message: string | VNode
  confirmText?: string
  cancelText?: string
  confirmVariant?: 'danger' | 'primary' | 'warning'
  loading?: boolean
  customContent?: VNode
  hideButtons?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  confirmText: '确定',
  cancelText: '取消',
  confirmVariant: 'primary',
  loading: false,
  hideButtons: false
})

const emit = defineEmits<{
  confirm: []
  cancel: []
  'update:open': [value: boolean]
}>()

const confirmButtonType = computed(() => {
  switch (props.confirmVariant) {
    case 'danger':
      return 'error'
    case 'warning':
      return 'warning'
    default:
      return 'primary'
  }
})

const handleConfirm = () => {
  emit('confirm')
}

const handleCancel = () => {
  emit('cancel')
  emit('update:open', false)
}
</script>