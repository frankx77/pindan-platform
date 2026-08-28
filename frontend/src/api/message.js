import request from './index.js'

// 我的消息列表
export function getMessages() {
  return request.get('/message/list')
}

// 未读数（Tabbar 角标）
export function getUnreadCount() {
  return request.get('/message/unread-count')
}

// 单条标记已读
export function markMessageRead(id) {
  return request.put(`/message/${id}/read`)
}

// 全部标记已读
export function markAllRead() {
  return request.put('/message/read-all')
}
