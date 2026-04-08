/**
 * 路由配置文件
 * - 静态路由配置（登录页、404页）
 * - 动态路由生成和管理
 * - 路由守卫（权限验证）
 */
import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import type { Router } from '@/types'

/** 动态导入所有视图组件 */
const modules = import.meta.glob('../views/**/*.vue')

/**
 * 组件映射表
 * 
 * 将路由配置中的 component 字段映射到实际的组件文件
 */
const componentMap: Record<string, () => Promise<any>> = {
  'Layout': () => import('@/layouts/BasicLayout.vue'),
  'dashboard/index': () => import('@/views/dashboard/index.vue'),
  'system/user/index': () => import('@/views/system/user/index.vue'),
  'system/role/index': () => import('@/views/system/role/index.vue'),
  'system/permission/index': () => import('@/views/system/permission/index.vue'),
  'system/menu/index': () => import('@/views/system/menu/index.vue'),
  'system/dept/index': () => import('@/views/system/dept/index.vue'),
  'log/operation/index': () => import('@/views/log/operation/index.vue'),
  'resume/index': () => import('@/views/resume/index.vue'),
  'resume/Upload': () => import('@/views/resume/Upload.vue'),
  'resume/Detail': () => import('@/views/resume/Detail.vue'),
  'assistant/index': () => import('@/views/assistant/index.vue'),
  'interview/index': () => import('@/views/interview/index.vue'),
  'interview/Session': () => import('@/views/interview/Session.vue'),
  'knowledge/index': () => import('@/views/knowledge/index.vue'),
  'knowledge/Upload': () => import('@/views/knowledge/Upload.vue'),
  'knowledge/Query': () => import('@/views/knowledge/Query.vue')
}

/**
 * 获取组件函数
 * 
 * @param component 组件路径
 * @returns 组件导入函数
 */
const getComponent = (component?: string) => {
  if (!component || component === 'Layout') {
    return () => import('@/layouts/BasicLayout.vue')
  }
  if (componentMap[component]) return componentMap[component]
  return modules[`../views/${component}.vue`]
}

/**
 * 静态路由配置
 * 
 * 这些路由不需要权限验证，始终存在
 */
const staticRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

/**
 * 构建动态路由
 * 
 * 将后端返回的路由数据转换为 Vue Router 路由配置
 * 
 * @param routers 后端返回的路由数据
 * @returns Vue Router 路由配置数组
 */
const buildDynamicRoutes = (routers: Router[]): RouteRecordRaw[] => {
  const routes: RouteRecordRaw[] = []
  
  for (const router of routers) {
    const hasChildren = router.children && router.children.length > 0
    const isDirectory = router.component === 'Layout' || !router.component
    
    if (isDirectory && hasChildren) {
      // 纯目录节点，展平子路由
      const childRoutes = buildDynamicRoutes(router.children!)
      routes.push(...childRoutes)
    } else {
      // 添加当前路由
      routes.push({
        path: router.path,
        name: router.name,
        component: getComponent(router.component),
        meta: {
          title: router.meta?.title,
          icon: router.meta?.icon,
          hidden: router.meta?.hidden,
          keepAlive: router.meta?.keepAlive
        },
        redirect: router.redirect
      } as RouteRecordRaw)
      // 非 Layout 父菜单也可能有子路由（如简历管理下的隐藏子页），需要展平添加
      if (hasChildren) {
        const childRoutes = buildDynamicRoutes(router.children!)
        routes.push(...childRoutes)
      }
    }
  }
  
  return routes
}

/**
 * Vue Router 实例
 */
const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes
})

/** 标记动态路由是否已添加 */
let routesAdded = false

/** 记录第一个路由路径 */
let firstRoute = '/dashboard'

/**
 * 添加动态路由
 * 
 * @param routers 后端返回的路由数据
 */
const addDynamicRoutes = (routers: Router[]) => {
  if (routesAdded) return
  
  const dynamicRoutes = buildDynamicRoutes(routers)
  firstRoute = dynamicRoutes[0]?.path || '/dashboard'
  
  const layoutRoute: RouteRecordRaw = {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/BasicLayout.vue'),
    redirect: firstRoute,
    children: dynamicRoutes
  }
  
  router.addRoute(layoutRoute)
  routesAdded = true
}

/**
 * 重置路由
 * 
 * 清除动态添加的路由，用于登出时重置路由状态
 */
const resetRoutes = () => {
  router.removeRoute('Layout')
  routesAdded = false
  firstRoute = '/dashboard'
}

/**
 * 获取第一个路由路径
 * 
 * @returns 第一个路由路径
 */
const getFirstRoute = () => firstRoute

/**
 * 全局路由守卫
 * 
 * 处理：
 * - 登录状态验证
 * - 动态路由加载
 * - 权限验证失败跳转
 */
router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()
  const menuStore = useMenuStore()

  if (to.path === '/login') {
    if (userStore.token && userStore.userInfo) {
      next(firstRoute)
      return
    }
    next()
    return
  }

  if (!userStore.token) {
    next('/login')
    return
  }

  if (!routesAdded) {
    try {
      if (!userStore.userInfo) {
        await userStore.getCurrentUser()
      }
      const routers = await menuStore.getRouters()
      addDynamicRoutes(routers)
      next({ path: to.path, replace: true })
      return
    } catch (error) {
      userStore.logout()
      resetRoutes()
      menuStore.clearMenus()
      next('/login')
      return
    }
  }

  next()
})

export default router
export { resetRoutes, getFirstRoute }