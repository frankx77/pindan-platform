import request from './index.js'

// 健康检查（阶段 0 联调验证用）
export function getHealth() {
  return request.get('/health')
}
