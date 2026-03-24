import { request } from '@/utils/request'
import type { LoginUser, User, Role, Permission, Menu, Router, OperationLog, PageResult, Result, Conversation } from '@/types'

export const authApi = {
  login(data: { username: string; password: string }): Promise<Result<LoginUser>> {
    return request.post('/auth/login', data)
  },
  
  logout(): Promise<Result<void>> {
    return request.post('/auth/logout')
  },
  
  getCurrentUser(): Promise<Result<LoginUser>> {
    return request.get('/auth/current')
  }
}

export const userApi = {
  getPage(params: any): Promise<Result<PageResult<User>>> {
    return request.get('/user/page', params)
  },
  
  getById(id: number): Promise<Result<User>> {
    return request.get(`/user/${id}`)
  },
  
  save(data: Partial<User>): Promise<Result<void>> {
    return request.post('/user', data)
  },
  
  update(data: Partial<User>): Promise<Result<void>> {
    return request.put('/user', data)
  },
  
  delete(id: number): Promise<Result<void>> {
    return request.delete(`/user/${id}`)
  },
  
  assignRoles(userId: number, roleIds: number[]): Promise<Result<void>> {
    return request.post(`/user/${userId}/roles`, roleIds)
  }
}

export const roleApi = {
  getPage(params: any): Promise<Result<PageResult<Role>>> {
    return request.get('/role/page', params)
  },
  
  listAll(): Promise<Result<Role[]>> {
    return request.get('/role/list')
  },
  
  getById(id: number): Promise<Result<Role>> {
    return request.get(`/role/${id}`)
  },
  
  save(data: Partial<Role>): Promise<Result<void>> {
    return request.post('/role', data)
  },
  
  update(data: Partial<Role>): Promise<Result<void>> {
    return request.put('/role', data)
  },
  
  delete(id: number): Promise<Result<void>> {
    return request.delete(`/role/${id}`)
  },
  
  assignPermissions(roleId: number, permissionIds: number[]): Promise<Result<void>> {
    return request.post(`/role/${roleId}/permissions`, permissionIds)
  },
  
  assignMenus(roleId: number, menuIds: number[]): Promise<Result<void>> {
    return request.post(`/role/${roleId}/menus`, menuIds)
  }
}

export const menuApi = {
  getRouters(): Promise<Result<Router[]>> {
    return request.get('/menu/routers')
  },
  
  getTree(): Promise<Result<Menu[]>> {
    return request.get('/menu/tree')
  },
  
  getPage(params: any): Promise<Result<PageResult<Menu>>> {
    return request.get('/menu/page', params)
  },
  
  getById(id: number): Promise<Result<Menu>> {
    return request.get(`/menu/${id}`)
  },
  
  save(data: Partial<Menu>): Promise<Result<void>> {
    return request.post('/menu', data)
  },
  
  update(data: Partial<Menu>): Promise<Result<void>> {
    return request.put('/menu', data)
  },
  
  delete(id: number): Promise<Result<void>> {
    return request.delete(`/menu/${id}`)
  }
}

export const permissionApi = {
  getPage(params: any): Promise<Result<PageResult<Permission>>> {
    return request.get('/permission/page', params)
  },
  
  listAll(): Promise<Result<Permission[]>> {
    return request.get('/permission/list')
  },
  
  getById(id: number): Promise<Result<Permission>> {
    return request.get(`/permission/${id}`)
  },
  
  save(data: Partial<Permission>): Promise<Result<void>> {
    return request.post('/permission', data)
  },
  
  update(data: Partial<Permission>): Promise<Result<void>> {
    return request.put('/permission', data)
  },
  
  delete(id: number): Promise<Result<void>> {
    return request.delete(`/permission/${id}`)
  }
}

export const logApi = {
  getPage(params: any): Promise<Result<PageResult<OperationLog>>> {
    return request.get('/log/page', params)
  },
  
  delete(id: number): Promise<Result<void>> {
    return request.delete(`/log/${id}`)
  }
}

export const chatApi = {
  getConversations(): Promise<Conversation[]> {
    return request.get<Result<Conversation[]>>('/chat/conversations').then((res: Result<Conversation[]>) => res.data)
  },
  
  getConversation(id: number): Promise<Conversation> {
    return request.get<Result<Conversation>>(`/chat/conversation/${id}`).then((res: Result<Conversation>) => res.data)
  },
  
  createConversation(): Promise<Conversation> {
    return request.post<Result<Conversation>>('/chat/conversation').then((res: Result<Conversation>) => res.data)
  },
  
  updateTitle(id: number, title: string): Promise<void> {
    return request.put(`/chat/conversation/${id}/title`, null, { params: { title } })
  },
  
  deleteConversation(id: number): Promise<void> {
    return request.delete(`/chat/conversation/${id}`)
  }
}