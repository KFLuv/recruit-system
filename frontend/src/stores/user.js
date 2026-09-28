import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)

  function setToken(val) {
    token.value = val
    localStorage.setItem('token', val)
  }

  function setUserInfo(val) {
    userInfo.value = val
    localStorage.setItem('userInfo', JSON.stringify(val))
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  async function fetchUserInfo() {
    try {
      const res = await getCurrentUser()
      if (res.success && res.data) {
        setUserInfo(res.data)
      }
      return res
    } catch {
      logout()
      return null
    }
  }

  return { token, userInfo, isLoggedIn, setToken, setUserInfo, logout, fetchUserInfo }
})
