import request from './index.js'

// 注册
export function register(data) {
  return request.post('/user/register', data)
}

// 登录
export function login(data) {
  return request.post('/user/login', data)
}
