import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi, getCurrentUserInfo } from '@/api/user'
import { setToken, setUserInfo, clearAuth, getToken, getUserInfo as getStoredUserInfo } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: getStoredUserInfo() || null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token && !!state.userInfo,
    userId: (state) => state.userInfo?.id || null,
    username: (state) => state.userInfo?.username || '',
    nickname: (state) => state.userInfo?.nickname || '',
    avatar: (state) => state.userInfo?.avatar || '',
    role: (state) => state.userInfo?.role || 'USER',
    isAdmin: (state) => state.userInfo?.role === 'ADMIN'
  },

  actions: {
    /**
     * 用户登录
     */
    async login(loginForm) {
      try {
        const res = await loginApi(loginForm)
        if (res.code === 200 && res.data) {
          // 后端登录返回的是 LoginVO，包含 token 和 user 对象
          const { token, user } = res.data
          
          if (token) {
            this.token = token
            setToken(token)
          }
          
          if (user) {
            this.userInfo = user
            setUserInfo(user)
          }
          
          return res
        }
        throw new Error(res.message || '登录失败')
      } catch (error) {
        console.error('登录失败：', error)
        throw error
      }
    },

    /**
     * 用户注册
     */
    async register(registerForm) {
      try {
        const res = await registerApi(registerForm)
        return res
      } catch (error) {
        console.error('注册失败：', error)
        throw error
      }
    },

    /**
     * 获取当前用户信息
     */
    async fetchUserInfo() {
      try {
        const res = await getCurrentUserInfo()
        if (res.code === 200 && res.data) {
          this.userInfo = res.data
          setUserInfo(res.data)
          return res.data
        }
        throw new Error(res.message || '获取用户信息失败')
      } catch (error) {
        console.error('获取用户信息失败：', error)
        throw error
      }
    },

    /**
     * 退出登录
     */
    logout() {
      this.token = ''
      this.userInfo = null
      clearAuth()
    }
  }
})

