<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="200px" class="admin-aside">
        <div class="admin-logo">
          <el-icon :size="24"><Setting /></el-icon>
          <span>管理后台</span>
        </div>
        <el-menu
          :default-active="$route.path"
          router
          class="admin-menu"
        >
          <el-menu-item index="/admin/posts">
            <el-icon><Document /></el-icon>
            <span>帖子管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/categories">
            <el-icon><FolderOpened /></el-icon>
            <span>板块管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/reports">
            <el-icon><Warning /></el-icon>
            <span>举报管理</span>
          </el-menu-item>
        </el-menu>
        
        <div class="admin-footer">
          <el-button text @click="backToHome">
            <el-icon><Back /></el-icon>
            返回前台
          </el-button>
        </div>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <el-header class="admin-header">
          <div class="admin-header-content">
            <h2>{{ $route.meta.title || '管理后台' }}</h2>
            <div class="admin-user">
              <el-avatar :size="32" :src="userInfo?.avatar">
                {{ userInfo?.nickname?.charAt(0) || 'A' }}
              </el-avatar>
              <span>{{ userInfo?.nickname || userInfo?.username }}</span>
            </div>
          </div>
        </el-header>

        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  Setting,
  Document,
  FolderOpened,
  User,
  Warning,
  Back
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

const backToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.el-container {
  min-height: 100vh;
}

.admin-aside {
  background-color: #001529;
  display: flex;
  flex-direction: column;
}

.admin-logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-menu {
  flex: 1;
  border-right: none;
  background-color: #001529;
}

.admin-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.65);
}

.admin-menu :deep(.el-menu-item:hover) {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.08);
}

.admin-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background-color: #1890ff;
}

.admin-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-footer :deep(.el-button) {
  width: 100%;
  color: rgba(255, 255, 255, 0.65);
}

.admin-footer :deep(.el-button:hover) {
  color: #fff;
}

.admin-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 24px;
}

.admin-header-content {
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.admin-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.admin-user {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.admin-main {
  padding: 24px;
}
</style>

