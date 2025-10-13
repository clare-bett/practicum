<template>
  <header class="app-header">
    <div class="header-container">
      <div class="header-left">
        <router-link to="/" class="logo">
          <el-icon :size="28"><ChatDotSquare /></el-icon>
          <span class="logo-text">灵犀论坛</span>
        </router-link>

        <nav class="nav-menu">
          <router-link to="/" class="nav-item">首页</router-link>
          <el-dropdown v-if="categories.length > 0" trigger="hover">
            <span class="nav-item">
              板块 <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-for="category in categories" :key="category.id">
                  <router-link :to="`/category/${category.id}`">
                    {{ category.name }}
                  </router-link>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </nav>
      </div>

      <div class="header-right">
        <el-button type="primary" @click="goToCreate" v-if="isLoggedIn">
          <el-icon><EditPen /></el-icon>
          <span>发帖</span>
        </el-button>

        <div v-if="isLoggedIn" class="user-section">
          <el-dropdown trigger="hover">
            <div class="user-info">
              <el-avatar :size="36" :src="userInfo?.avatar">
                {{ userInfo?.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="username">{{ userInfo?.nickname || userInfo?.username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToProfile">
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item v-if="isAdmin" @click="goToAdmin">
                  <el-icon><Setting /></el-icon> 管理后台
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div v-else class="auth-buttons">
          <el-button @click="goToLogin">登录</el-button>
          <el-button type="primary" @click="goToRegister">注册</el-button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCategoryStore } from '@/stores/category'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ChatDotSquare,
  ArrowDown,
  EditPen,
  User,
  SwitchButton,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const categoryStore = useCategoryStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const categories = computed(() => categoryStore.categories)
const isAdmin = computed(() => userStore.isAdmin)

onMounted(() => {
  categoryStore.fetchCategories()
})

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}

const goToProfile = () => {
  router.push('/profile')
}

const goToCreate = () => {
  router.push('/post/create')
}

const goToAdmin = () => {
  router.push('/admin')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('退出成功')
    router.push('/')
  }).catch(() => {})
}
</script>

<style scoped>
.app-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 64px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
  transition: opacity 0.3s;
}

.logo:hover {
  opacity: 0.8;
}

.logo-text {
  font-size: 18px;
}

.nav-menu {
  display: flex;
  gap: 24px;
  align-items: center;
}

.nav-item {
  color: #606266;
  font-size: 15px;
  cursor: pointer;
  transition: color 0.3s;
  display: flex;
  align-items: center;
}

.nav-item:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-section {
  margin-left: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
}

.auth-buttons {
  display: flex;
  gap: 8px;
}

@media (max-width: 768px) {
  .header-container {
    padding: 0 12px;
  }

  .nav-menu {
    display: none;
  }

  .logo-text {
    display: none;
  }

  .username {
    display: none;
  }
}
</style>

