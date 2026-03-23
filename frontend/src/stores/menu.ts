import { defineStore } from 'pinia'
import { ref } from 'vue'
import { menuApi } from '@/api'
import type { Router, Menu } from '@/types'

export const useMenuStore = defineStore('menu', () => {
  const routers = ref<Router[]>([])
  const menuTree = ref<Menu[]>([])

  const getRouters = async () => {
    const res = await menuApi.getRouters()
    routers.value = res.data
    return res.data
  }

  const getMenuTree = async () => {
    const res = await menuApi.getTree()
    menuTree.value = res.data
    return res.data
  }

  const clearMenus = () => {
    routers.value = []
    menuTree.value = []
  }

  return {
    routers,
    menuTree,
    getRouters,
    getMenuTree,
    clearMenus
  }
})