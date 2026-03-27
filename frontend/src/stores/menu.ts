/**
 * 菜单状态管理
 * - 动态路由管理
 * - 菜单树管理
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { menuApi } from '@/api'
import type { Router, Menu } from '@/types'

/**
 * 菜单 Store
 * 
 * 使用 Composition API 风格定义
 */
export const useMenuStore = defineStore('menu', () => {
  /** 动态路由列表 */
  const routers = ref<Router[]>([])
  
  /** 菜单树数据 */
  const menuTree = ref<Menu[]>([])

  /**
   * 获取当前用户的动态路由
   * 
   * @returns 路由列表
   */
  const getRouters = async () => {
    const res = await menuApi.getRouters()
    routers.value = res.data
    return res.data
  }

  /**
   * 获取菜单树
   * 
   * @returns 菜单树数据
   */
  const getMenuTree = async () => {
    const res = await menuApi.getTree()
    menuTree.value = res.data
    return res.data
  }

  /**
   * 清除菜单数据
   * 
   * 用于登出时清除状态
   */
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