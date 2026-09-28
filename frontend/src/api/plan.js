import request from './request'

export function getPlanList(params) {
  return request.get('/recruit-plans', { params })
}

export function getPlanById(id) {
  return request.get(`/recruit-plans/${id}`)
}

export function createPlan(data) {
  return request.post('/recruit-plans', data)
}

export function updatePlan(id, data) {
  return request.put(`/recruit-plans/${id}`, data)
}

export function deletePlan(id) {
  return request.delete(`/recruit-plans/${id}`)
}

export function restorePlan(id) {
  return request.post(`/recruit-plans/${id}/restore`)
}

export function permanentDeletePlan(id) {
  return request.delete(`/recruit-plans/${id}/permanent`)
}

export function submitForApproval(id) {
  return request.post(`/recruit-plans/${id}/submit`)
}

export function revokePlan(id) {
  return request.post(`/recruit-plans/${id}/revoke`)
}

export function approvePlan(id, comment) {
  return request.post(`/recruit-plans/${id}/approve`, { comment })
}

export function rejectPlan(id, comment) {
  return request.post(`/recruit-plans/${id}/reject`, { comment })
}

export function batchSubmit(ids) {
  return request.post('/recruit-plans/batch/submit', ids)
}

export function batchRevoke(ids) {
  return request.post('/recruit-plans/batch/revoke', ids)
}

export function getPlansByYear(year) {
  return request.get(`/recruit-plans/year/${year}`)
}
