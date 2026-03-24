export interface User {
  id: number
  username: string
  nickname: string
  email?: string
  phone?: string
  avatar?: string
  status: number
  deptId?: number
  deptName?: string
  roles?: string[]
  createTime?: string
  updateTime?: string
}

export interface Role {
  id: number
  roleName: string
  roleCode: string
  description?: string
  status: number
  permissionIds?: number[]
  menuIds?: number[]
  createTime?: string
  updateTime?: string
}

export interface Permission {
  id: number
  permissionName: string
  permissionCode: string
  resourceType: number
  resourceUrl?: string
  method?: string
  parentId: number
  description?: string
  status: number
  sort: number
  createTime?: string
  updateTime?: string
}

export interface Menu {
  id: number
  menuName: string
  menuCode: string
  parentId: number
  path?: string
  component?: string
  icon?: string
  menuType: number
  visible: number
  status: number
  sort: number
  children?: Menu[]
  createTime?: string
  updateTime?: string
}

export interface Router {
  id: number
  path: string
  name: string
  component?: string
  redirect?: string
  meta?: RouterMeta
  children?: Router[]
}

export interface RouterMeta {
  title: string
  icon?: string
  hidden?: boolean
  keepAlive?: boolean
}

export interface OperationLog {
  id: number
  userId?: number
  username?: string
  operation?: string
  method?: string
  params?: string
  ip?: string
  status: number
  errorMsg?: string
  executeTime?: number
  createTime?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface Result<T> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface LoginUser {
  userId: number
  username: string
  nickname: string
  avatar?: string
  token: string
}

export interface dept {
  id: number
  deptName: string
  parentId: number
  status: number
  sort: number
  children?: dept[]
  createTime?: string
  updateTime?: string
}

export interface Message {
  id: number
  role: 'user' | 'assistant' | 'system'
  content: string
  createTime?: number
}

export interface Conversation {
  id: number
  title: string
  modelType?: string
  createTime?: number
  updateTime?: number
  messages?: Message[]
}