/**
 * 用户状态管理
 * - 用户 Token 管理
 * - 用户信息管理
 * - 登录、登出操作
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/api'
import type { LoginUser } from '@/types'

/**
 * 用户 Store
 * 
 * 使用 Composition API 风格定义
 */
export const useUserStore = defineStore('user', () => {
  /** 用户 Token，从 localStorage 初始化 */
  const token = ref<string>(localStorage.getItem('token') || '')
  
  /** 用户信息对象 */
  const userInfo = ref<LoginUser | null>(null)

  /**
   * 设置 Token
   * 
   * @param newToken 新的 Token 值
   */
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  /**
   * 设置用户信息
   * 
   * @param info 用户信息对象
   */
  const setUserInfo = (info: LoginUser) => {
    userInfo.value = info
  }

  /**
   * 用户登录
   * 
   * @param username 用户名
   * @param password 密码
   * @returns 登录用户信息
   */
  const login = async (username: string, password: string) => {
    const res = await authApi.login({ username, password })
    setToken(res.data.token)
    setUserInfo(res.data)
    return res.data
  }

  /**
   * 用户登出
   * 
   * 清除 Token 和用户信息
   */
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  /**
   * 获取当前用户信息
   * 
   * @returns 用户信息对象，如果未登录或获取失败则返回 null
   */
  const getCurrentUser = async () => {
    if (!token.value) return null
    try {
      const res = await authApi.getCurrentUser()
      setUserInfo(res.data)
      return res.data
    } catch (error) {
      logout()
      return null
    }
  }

  return {
    token,
    userInfo,
    setToken,
    setUserInfo,
    login,
    logout,
    getCurrentUser
  }
})