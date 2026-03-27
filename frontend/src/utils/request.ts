/**
 * HTTP 请求工具类
 * - 请求拦截：自动添加 Token 到请求头
 * - 响应拦截：统一处理错误响应和 401 状态
 * - 请求方法封装：get、post、put、delete
 */
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { useUserStore } from '@/stores/user'
import router from '@/router'

/**
 * Axios 实例
 * 
 * 配置了基础的请求地址、超时时间和请求头
 */
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

/**
 * 请求拦截器
 * 
 * 自动在请求头中添加 Token
 */
service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = userStore.token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 
 * 统一处理响应错误：
 * - code !== 200 时显示错误消息
 * - code === 401 时自动登出并跳转到登录页
 */
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    if (res.code !== 200) {
      window.$message.error(res.message || '请求失败')
      
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || 'Error'))
    }
    
    return res
  },
  (error) => {
    window.$message.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

/**
 * 请求方法封装对象
 * 
 * 提供了常用的 HTTP 请求方法，简化调用
 */
export const request = {
  /**
   * GET 请求
   * 
   * @param url 请求地址
   * @param params 查询参数
   * @param config 额外配置
   * @returns Promise<T> 响应数据
   */
  get<T>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.get(url, { params, ...config })
  },
  
  /**
   * POST 请求
   * 
   * @param url 请求地址
   * @param data 请求体数据
   * @param config 额外配置
   * @returns Promise<T> 响应数据
   */
  post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.post(url, data, config)
  },
  
  /**
   * PUT 请求
   * 
   * @param url 请求地址
   * @param data 请求体数据
   * @param config 额外配置
   * @returns Promise<T> 响应数据
   */
  put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.put(url, data, config)
  },
  
  /**
   * DELETE 请求
   * 
   * @param url 请求地址
   * @param params 查询参数
   * @param config 额外配置
   * @returns Promise<T> 响应数据
   */
  delete<T>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    return service.delete(url, { params, ...config })
  }
}

export default service