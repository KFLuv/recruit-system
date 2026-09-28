import request from './request'

export function login(username, password) {
  return request.post('/users/login', { username, password })
}

export function register(data) {
  return request.post('/users/register', data)
}

export function logout() {
  return request.post('/auth/logout')
}

export function getCurrentUser() {
  return request.get('/auth/check')
}