import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import type { ApiResponse } from '@/types'
import { ElMessage } from 'element-plus'

/**
 * Axios 请求封装
 *
 * 提供统一的 HTTP 请求客户端，包含：
 * - 请求/响应拦截器
 * - 统一错误处理
 * - 请求超时配置
 */
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
})

/**
 * 请求拦截器
 */
service.interceptors.request.use(
  (config) => {
    // 可在此添加 Token 等认证信息
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 */
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data
    // 如果返回的状态码不是 200，视为错误
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return response
  },
  (error) => {
    console.error('响应错误:', error)
    const message = error.response?.data?.message || error.message || '网络异常'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

/**
 * 通用 GET 请求
 *
 * @param url   请求 URL
 * @param params 查询参数
 * @param config 额外配置
 * @returns 响应数据
 */
export async function get<T>(url: string, params?: Record<string, unknown>, config?: AxiosRequestConfig): Promise<T> {
  const response = await service.get<ApiResponse<T>>(url, { params, ...config })
  return response.data.data
}

/**
 * 通用 POST 请求
 *
 * @param url  请求 URL
 * @param data 请求体
 * @param config 额外配置
 * @returns 响应数据
 */
export async function post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  const response = await service.post<ApiResponse<T>>(url, data, config)
  return response.data.data
}

/**
 * 通用 PUT 请求
 *
 * @param url  请求 URL
 * @param data 请求体
 * @param config 额外配置
 * @returns 响应数据
 */
export async function put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  const response = await service.put<ApiResponse<T>>(url, data, config)
  return response.data.data
}

/**
 * 通用 DELETE 请求
 *
 * @param url  请求 URL
 * @param config 额外配置
 * @returns 响应数据
 */
export async function del<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  const response = await service.delete<ApiResponse<T>>(url, config)
  return response.data.data
}

export default service
