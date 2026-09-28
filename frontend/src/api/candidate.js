import request from './request'

export function getCandidateList(params) {
  return request.get('/candidates', { params })
}

export function getCandidateById(id) {
  return request.get(`/candidates/${id}`)
}

export function createCandidate(data) {
  return request.post('/candidates', data)
}

export function updateCandidate(id, data) {
  return request.put(`/candidates/${id}`, data)
}

export function deleteCandidate(id) {
  return request.delete(`/candidates/${id}`)
}

export function updateCandidateStatus(id, status, rejectionReason) {
  return request.post(`/candidates/${id}/status`, { status, rejectionReason })
}

export function recordCandidateScore(id, scores) {
  return request.post(`/candidates/${id}/score`, scores)
}

export function searchCandidates(keyword) {
  return request.get('/candidates/search', { params: { keyword } })
}
