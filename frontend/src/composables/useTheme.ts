/**
 * 主题切换组合式函数
 * - 支持亮色/暗色主题切换
 * - 自动保存到 localStorage
 * - 监听系统主题变化
 */
import { ref, watch, onMounted, onUnmounted } from 'vue'

export type ThemeMode = 'light' | 'dark'

export interface UseThemeOptions {
  /** 默认主题，默认 'light' */
  defaultTheme?: ThemeMode
  /** 是否监听系统主题，默认 true */
  watchSystem?: boolean
  /** localStorage key，默认 'theme' */
  storageKey?: string
}

export interface UseThemeReturn {
  /** 当前主题 */
  theme: ReturnType<typeof ref<ThemeMode>>
  /** 是否为暗色主题 */
  isDark: ReturnType<typeof ref<boolean>>
  /** 切换主题 */
  toggle: () => void
  /** 设置主题 */
  setTheme: (theme: ThemeMode) => void
}

export function useTheme(options: UseThemeOptions = {}): UseThemeReturn {
  const {
    defaultTheme = 'light',
    watchSystem = true,
    storageKey = 'theme'
  } = options

  const theme = ref<ThemeMode>(defaultTheme)
  const isDark = ref<boolean>(false)

  const applyTheme = (newTheme: ThemeMode) => {
    isDark.value = newTheme === 'dark'
    
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  const setTheme = (newTheme: ThemeMode) => {
    theme.value = newTheme
    localStorage.setItem(storageKey, newTheme)
    applyTheme(newTheme)
  }

  const toggle = () => {
    setTheme(theme.value === 'light' ? 'dark' : 'light')
  }

  const getSystemTheme = (): ThemeMode => {
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark'
    }
    return 'light'
  }

  const loadTheme = () => {
    const savedTheme = localStorage.getItem(storageKey) as ThemeMode | null
    if (savedTheme && (savedTheme === 'light' || savedTheme === 'dark')) {
      setTheme(savedTheme)
    } else if (watchSystem) {
      setTheme(getSystemTheme())
    } else {
      setTheme(defaultTheme)
    }
  }

  let mediaQuery: MediaQueryList | null = null

  const handleSystemThemeChange = (e: MediaQueryListEvent) => {
    const systemTheme = e.matches ? 'dark' : 'light'
    
    if (!localStorage.getItem(storageKey)) {
      setTheme(systemTheme)
    }
  }

  onMounted(() => {
    loadTheme()

    if (watchSystem && window.matchMedia) {
      mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
      mediaQuery.addEventListener('change', handleSystemThemeChange)
    }
  })

  onUnmounted(() => {
    if (mediaQuery) {
      mediaQuery.removeEventListener('change', handleSystemThemeChange)
    }
  })

  watch(theme, (newTheme) => {
    applyTheme(newTheme)
  })

  return {
    theme,
    isDark,
    toggle,
    setTheme
  }
}