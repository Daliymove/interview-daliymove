/**
 * 日期格式化工具函数
 * - formatDate: 格式化日期
 * - formatDateTime: 格式化日期时间
 * - formatDateOnly: 格式化仅日期
 */
import dayjs from 'dayjs'

/**
 * 格式化日期为中文格式
 * @param dateStr 日期字符串
 * @returns 格式化后的日期字符串
 */
export function formatDate(dateStr: string | null | undefined): string {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD')
}

/**
 * 格式化日期时间（包含时分）
 * @param dateStr 日期字符串
 * @returns 格式化后的日期时间字符串
 */
export function formatDateTime(dateStr: string | null | undefined): string {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm')
}

/**
 * 格式化日期（仅日期部分）
 * @param dateStr 日期字符串
 * @returns 格式化后的日期字符串
 */
export function formatDateOnly(dateStr: string | null | undefined): string {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('MM-DD')
}

/**
 * 格式化完整日期时间
 * @param dateStr 日期字符串
 * @returns 格式化后的完整日期时间字符串
 */
export function formatFullDateTime(dateStr: string | null | undefined): string {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY年MM月DD日 HH:mm')
}