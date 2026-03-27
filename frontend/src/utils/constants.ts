/**
 * 全局常量定义
 * - 状态选项
 * - 菜单类型选项
 * - 权限类型选项
 * - 面试难度/类型选项
 */

/** 状态选项 */
export const STATUS_OPTIONS = [
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

/** 菜单类型选项 */
export const MENU_TYPE_OPTIONS = [
  { label: '目录', value: 1 },
  { label: '菜单', value: 2 },
  { label: '按钮', value: 3 }
]

/** 权限类型选项 */
export const PERMISSION_TYPE_OPTIONS = [
  { label: '菜单', value: 1 },
  { label: '按钮', value: 2 },
  { label: '接口', value: 3 }
]

/** 面试难度选项 */
export const DIFFICULTY_OPTIONS = [
  { label: '简单', value: 1 },
  { label: '中等', value: 2 },
  { label: '困难', value: 3 }
]

/** 面试类型选项 */
export const INTERVIEW_TYPE_OPTIONS = [
  { label: '技术面试', value: 1 },
  { label: 'HR面试', value: 2 },
  { label: '综合面试', value: 3 }
]