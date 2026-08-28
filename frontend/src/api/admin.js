import request from './index.js'

// 管理员：全量拼单列表（keyword/status 筛选，分页）
export function getAdminGrouponList(params) {
  return request.get('/admin/groupon/list', { params })
}

// 管理员：强制下架拼单
export function adminCancelGroupon(id) {
  return request.put(`/admin/groupon/${id}/cancel`)
}

// 管理员：用户列表
export function getAdminUserList(params) {
  return request.get('/admin/user/list', { params })
}

// 管理员：禁用用户
export function banUser(id) {
  return request.put(`/admin/user/${id}/ban`)
}

// 管理员：解除禁用
export function unbanUser(id) {
  return request.put(`/admin/user/${id}/unban`)
}

// 管理员：看板聚合数据
export function getDashboard() {
  return request.get('/admin/dashboard')
}

// 管理员：操作日志分页查询（keyword/action 筛选）
export function getAdminLogList(params) {
  return request.get('/admin/log/list', { params })
}
