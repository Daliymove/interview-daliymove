/**
 * 搜索表单组合式函数
 * - 搜索关键词管理
 * - 状态筛选管理
 * - 重置搜索
 */
import { reactive } from 'vue'

export interface SearchFormState {
  keyword: string
  status: number | null
  [key: string]: any
}

export interface UseSearchFormOptions {
  onReset?: () => void
  onSearch?: () => void
}

export function useSearchForm(options: UseSearchFormOptions = {}) {
  const { onReset, onSearch } = options

  const searchForm = reactive<SearchFormState>({
    keyword: '',
    status: null
  })

  const resetSearch = () => {
    searchForm.keyword = ''
    searchForm.status = null
    onReset?.()
  }

  const doSearch = () => {
    onSearch?.()
  }

  const getParams = () => ({
    keyword: searchForm.keyword || undefined,
    status: searchForm.status
  })

  return {
    searchForm,
    resetSearch,
    doSearch,
    getParams
  }
}