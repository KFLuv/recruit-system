import request from './request'

export function getAnnouncements() {
  return request.get('/announcements')
}

export function getPublishedAnnouncements() {
  return request.get('/announcements/published')
}

export function createAnnouncement(data) {
  return request.post('/announcements', data)
}

export function updateAnnouncement(id, data) {
  return request.put(`/announcements/${id}`, data)
}

export function deleteAnnouncement(id) {
  return request.delete(`/announcements/${id}`)
}
