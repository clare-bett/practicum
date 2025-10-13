<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <h2 class="login-title">
          <el-icon :size="32" color="#409eff"><ChatDotSquare /></el-icon>
          <span>灵犀论坛</span>
        </h2>

        <!-- 登录类型选择 -->
        <el-tabs v-model="loginType" class="login-tabs">
          <el-tab-pane label="普通登录" name="user">
            <template #label>
              <span class="tab-label">
                <el-icon><User /></el-icon>
                普通登录
              </span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="管理员登录" name="admin">
            <template #label>
              <span class="tab-label">
                <el-icon><Lock /></el-icon>
                管理员登录
              </span>
            </template>
          </el-tab-pane>
        </el-tabs>

        <p class="login-subtitle">
          {{ loginType === 'admin' ? '管理员身份验证' : '欢迎回来' }}
        </p>

        <el-form 
          ref="loginFormRef" 
          :model="loginForm" 
          :rules="loginRules" 
          class="login-form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              size="large"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              size="large"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-button 
            type="primary" 
            size="large" 
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loginType === 'admin' ? '管理员登录' : '登录' }}
          </el-button>
        </el-form>

        <!-- 提示信息 -->
        <el-alert
          v-if="loginType === 'admin'"
          title="提示"
          type="warning"
          :closable="false"
          show-icon
          class="admin-tip"
        >
          仅限管理员账号登录，普通用户请选择"普通登录"
        </el-alert>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { ChatDotSquare, User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const loginType = ref('user') // 'user' | 'admin'

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        
        // 获取用户角色
        const userRole = userStore.userInfo?.role
        
        // 如果选择管理员登录，但实际不是管理员
        if (loginType.value === 'admin' && userRole !== 'ADMIN') {
          ElMessage.error('该账号不是管理员，无法以管理员身份登录')
          userStore.logout()
          loading.value = false
          return
        }
        
        ElMessage.success('登录成功')
        
        // 根据登录类型和实际角色跳转
        let redirectPath = route.query.redirect || '/'
        
        if (loginType.value === 'admin' && userRole === 'ADMIN') {
          // 管理员登录直接跳转到管理后台
          redirectPath = '/admin'
        } else if (userRole === 'ADMIN' && loginType.value === 'user') {
          // 管理员选择普通登录，跳转到首页
          redirectPath = '/'
        }
        
        router.push(redirectPath)
      } catch (error) {
        console.error('登录失败：', error)
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 420px;
}

.login-card {
  background: #fff;
  border-radius: 16px;
  padding: 48px 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.login-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.login-tabs {
  margin-bottom: 16px;
}

.login-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}

.login-subtitle {
  text-align: center;
  color: #909399;
  font-size: 16px;
  margin-bottom: 24px;
}

.login-form {
  margin-bottom: 16px;
}

.admin-tip {
  margin-bottom: 16px;
}

.admin-tip :deep(.el-alert__content) {
  font-size: 13px;
}

.login-button {
  width: 100%;
  margin-top: 8px;
}

.login-footer {
  text-align: center;
  color: #606266;
  font-size: 14px;
}

.register-link {
  color: #409eff;
  margin-left: 4px;
  transition: opacity 0.3s;
}

.register-link:hover {
  opacity: 0.8;
}

@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
  }
}
</style>

