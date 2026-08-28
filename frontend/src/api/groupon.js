import request from './index.js'

// 分类列表
export function getCategoryList() {
  return request.get('/category/list')
}

// 发布拼单
export function publishGroupon(data) {
  return request.post('/groupon', data)
}

// 分页列表（categoryTag/keyword/status 可选）
export function getGrouponList(params) {
  return request.get('/groupon/list', { params })
}

// 详情
export function getGrouponDetail(id) {
  return request.get(`/groupon/${id}`)
}

// 我发布的
export function getMyPublished() {
  return request.get('/groupon/my/published')
}

// 我参加的
export function getMyJoined() {
  return request.get('/groupon/my/joined')
}

// 申请加入拼单
export function applyGroupon(grouponId) {
  return request.post('/join-request', { grouponId })
}

// 团长审批通过
export function approveRequest(id) {
  return request.put(`/join-request/${id}/approve`)
}

// 团长审批拒绝
export function rejectRequest(id) {
  return request.put(`/join-request/${id}/reject`)
}

// 成员主动退出
export function quitRequest(id) {
  return request.put(`/join-request/${id}/quit`)
}

// 团长取消拼单
export function cancelGroupon(id) {
  return request.put(`/groupon/${id}/cancel`)
}

// 团长查看申请列表
export function getGrouponRequests(id) {
  return request.get(`/groupon/${id}/requests`)
}

// Excel 导出：我发布的拼单（返回 blob）
export function exportMyPublished() {
  return request.get('/groupon/my/published/export', { responseType: 'blob' })
}

// Excel 导出：我参加的拼单（返回 blob）
export function exportMyJoined() {
  return request.get('/groupon/my/joined/export', { responseType: 'blob' })
}
