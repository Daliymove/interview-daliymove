import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/api'
import type { LoginUser } from '@/types'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<LoginUser | null>(null)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info: LoginUser) => {
    userInfo.value = info
  }

  const login = async (username: string, password: string) => {
    const res = await authApi.login({ username, password })
    setToken(res.data.token)
    setUserInfo(res.data)
    return res.data
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

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