import { defineStore } from 'pinia'
import { getCategoryList } from '@/api/category'

export const useCategoryStore = defineStore('category', {
  state: () => ({
    categories: [],
    loading: false
  }),

  getters: {
    getCategoryById: (state) => (id) => {
      return state.categories.find(category => category.id === id)
    },
    
    activeCategoriesCount: (state) => {
      return state.categories.filter(c => c.status === 1).length
    }
  },

  actions: {
    /**
     * 获取板块列表
     */
    async fetchCategories() {
      if (this.categories.length > 0 && !this.loading) {
        return this.categories
      }

      this.loading = true
      try {
        const res = await getCategoryList()
        if (res.code === 200 && res.data) {
          this.categories = res.data.filter(c => c.status === 1)
          return this.categories
        }
        throw new Error(res.message || '获取板块列表失败')
      } catch (error) {
        console.error('获取板块列表失败：', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * 重置板块列表（强制刷新）
     */
    resetCategories() {
      this.categories = []
    }
  }
})

