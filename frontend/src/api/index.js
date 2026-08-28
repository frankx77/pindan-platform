import axios from 'axios'
import router from '../router/index.js'

// Axios 实例：统一 baseURL，请求/响应拦截器
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：自动带 token
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  (response) => {
    // 文件流（blob）不按业务 JSON 解析，直接返回原始响应
    if (response.config.responseType === 'blob') {
      return response
    }
    const res = response.data
    // 业务错误码统一弹错
    if (res.code !== 200) {
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // 未登录跳登录页
      router.push('/login')
    } else if (error.response && error.response.status === 403) {
      // 无管理员权限等访问受限场景
      return Promise.reject(new Error('无权限执行此操作'))
    }
    return Promise.reject(error)
  }
)

export default request

