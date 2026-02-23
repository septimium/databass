import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('jwt_token') || null)
  const username = ref(localStorage.getItem('username') || null)

  const setAuth = (newToken, newUsername) => {
    token.value = newToken
    username.value = newUsername
    localStorage.setItem('jwt_token', newToken)
    localStorage.setItem('username', newUsername)
  }

  const logout = () => {
    token.value = null
    username.value = null
    localStorage.removeItem('jwt_token')
    localStorage.removeItem('username')
  }

  const isAuthenticated = () => {
    return !!token.value
  }

  return { token, username, setAuth, logout, isAuthenticated }
})