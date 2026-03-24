import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import type { Router } from '@/types'

const modules = import.meta.glob('../views/**/*.vue')

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
  'assistant/index': () => import('@/views/assistant/index.vue'),
  'interview/index': () => import('@/views/interview/index.vue')
}

const getComponent = (component?: string) => {
  if (!component || component === 'Layout') {
    return () => import('@/layouts/BasicLayout.vue')
  }
  if (componentMap[component]) return componentMap[component]
  return modules[`../views/${component}.vue`]
}

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

const buildDynamicRoutes = (routers: Router[]): RouteRecordRaw[] => {
  const routes: RouteRecordRaw[] = []
  
  for (const router of routers) {
    const hasChildren = router.children && router.children.length > 0
    const isDirectory = router.component === 'Layout' || !router.component
    
    if (isDirectory && hasChildren) {
      const childRoutes = buildDynamicRoutes(router.children!)
      routes.push(...childRoutes)
    } else {
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
    }
  }
  
  return routes
}

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes
})

let routesAdded = false
let firstRoute = '/dashboard'

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

const resetRoutes = () => {
  router.removeRoute('Layout')
  routesAdded = false
  firstRoute = '/dashboard'
}

const getFirstRoute = () => firstRoute

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