/**
 * 弹窗工具函数
 * - 删除确认弹窗
 * - 操作确认弹窗
 */

export interface ConfirmOptions {
  title?: string
  content: string
  positiveText?: string
  negativeText?: string
}

/**
 * 显示删除确认弹窗
 * 
 * @param name 要删除的项名称
 * @param onConfirm 确认回调
 */
export function confirmDelete(name: string, onConfirm: () => Promise<void> | void) {
  window.$dialog.warning({
    title: '确认删除',
    content: `确定要删除 "${name}" 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await onConfirm()
        window.$message.success('删除成功')
      } catch (error) {
        // 错误已在请求拦截器中处理
      }
    }
  })
}

/**
 * 显示操作确认弹窗
 * 
 * @param options 弹窗配置
 * @param onConfirm 确认回调
 */
export function confirmAction(options: ConfirmOptions, onConfirm: () => Promise<void> | void) {
  window.$dialog.warning({
    title: options.title || '确认操作',
    content: options.content,
    positiveText: options.positiveText || '确定',
    negativeText: options.negativeText || '取消',
    onPositiveClick: async () => {
      try {
        await onConfirm()
      } catch (error) {
        // 错误已在请求拦截器中处理
      }
    }
  })
}