/**
 * 分页逻辑组合式函数
 * - 分页状态管理
 * - 页码切换处理
 * - 每页条数切换处理
 */
import { reactive, computed } from 'vue'

export interface PaginationState {
  page: number
  pageSize: number
  itemCount: number
}

export interface UsePaginationOptions {
  defaultPageSize?: number
  onLoad?: () => Promise<void>
}

export function usePagination(options: UsePaginationOptions = {}) {
  const { defaultPageSize = 10, onLoad } = options

  const pagination = reactive<PaginationState>({
    page: 1,
    pageSize: defaultPageSize,
    itemCount: 0
  })

  const pageCount = computed(() => {
    return Math.ceil(pagination.itemCount / pagination.pageSize)
  })

  const handlePageChange = (page: number) => {
    pagination.page = page
    onLoad?.()
  }

  const handlePageSizeChange = (pageSize: number) => {
    pagination.pageSize = pageSize
    pagination.page = 1
    onLoad?.()
  }

  const resetPagination = () => {
    pagination.page = 1
    pagination.pageSize = defaultPageSize
    pagination.itemCount = 0
  }

  const setTotal = (total: number) => {
    pagination.itemCount = total
  }

  const getParams = () => ({
    pageNum: pagination.page,
    pageSize: pagination.pageSize
  })

  return {
    pagination,
    pageCount,
    handlePageChange,
    handlePageSizeChange,
    resetPagination,
    setTotal,
    getParams
  }
}