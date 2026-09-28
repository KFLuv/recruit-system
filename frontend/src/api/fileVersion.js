import request from './request'

export function getFileVersions(bizType, bizId) {
  return request.get('/file-versions', { params: { bizType, bizId } })
}

export function addFileVersion(data) {
  return request.post('/file-versions', data)
}
