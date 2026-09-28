import request from './request'

export function getSystemLogs(params) {
  return request.get('/system-logs', { params })
}
