import request from './request'

// 审批管理：实际用的是 recruit-plans 中 status=submitting 的数据
export function getApprovalList(params) {
  return request.get('/recruit-plans', { params: { ...params, status: 'submitting' } })
}

export function getApprovalHistory() {
  return request.get('/recruit-plans', { params: { status: 'approved' } })
}

export function getRejectedList() {
  return request.get('/recruit-plans', { params: { status: 'rejected' } })
}
