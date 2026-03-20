<template>
  <MainLayout>
    <div class="my-profile-page">
      <el-row :gutter="20">
        <!-- 左侧：用户信息 -->
        <el-col :xs="24" :sm="24" :md="8">
          <el-card class="profile-card">
            <div class="profile-header">
              <el-avatar :size="100" :src="userInfo?.avatar">
                {{ userInfo?.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <h3 class="profile-name">{{ userInfo?.nickname || userInfo?.username }}</h3>
              <p class="profile-username">@{{ userInfo?.username }}</p>
              <el-tag v-if="userInfo?.role === 'ADMIN'" type="danger" size="small">
                管理员
              </el-tag>
            </div>

            <div class="profile-info">
              <div class="info-item">
                <span class="info-label">邮箱</span>
                <span class="info-value">{{ userInfo?.email }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">注册时间</span>
                <span class="info-value">{{ formatTime(userInfo?.createTime) }}</span>
              </div>
            </div>

            <!-- 编辑功能已移除 -->
          </el-card>
        </el-col>

        <!-- 右侧：我的帖子 -->
        <el-col :xs="24" :sm="24" :md="16">
          <el-card class="posts-card">
            <template #header>
              <div class="card-header">
                <span class="header-title">我的帖子</span>
                <el-button type="primary" size="small" @click="goToCreate">
                  <el-icon><EditPen /></el-icon>
                  发帖
                </el-button>
              </div>
            </template>

            <div v-loading="loading" class="post-list">
              <template v-if="posts.length > 0">
                <PostCard 
                  v-for="post in posts" 
                  :key="post.id" 
                  :post="post" 
                />
              </template>
              <el-empty v-else description="还没有发布过帖子" />
            </div>

            <div class="pagination" v-if="total > 0">
              <el-pagination
                v-model:current-page="pageNum"
                v-model:page-size="pageSize"
                :total="total"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next"
                @size-change="handleSizeChange"
                @current-change="handlePageChange"
              />
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 编辑功能已移除 -->
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getPostListByAuthor } from '@/api/post'
import MainLayout from '@/components/MainLayout.vue'
import PostCard from '@/components/PostCard.vue'
import { ElMessage } from 'element-plus'
import { formatTime } from '@/utils/format'
import { EditPen } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const posts = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  fetchMyPosts()
})

const fetchMyPosts = async () => {
  if (!userInfo.value?.id) return

  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const res = await getPostListByAuthor(userInfo.value.id, params)
    if (res.code === 200 && res.data) {
      posts.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取我的帖子失败：', error)
    ElMessage.error('获取我的帖子失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchMyPosts()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchMyPosts()
}

const goToCreate = () => {
  router.push('/post/create')
}
</script>

<style scoped>
.my-profile-page {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card,
.posts-card {
  border-radius: 8px;
  margin-bottom: 20px;
}

.profile-header {
  text-align: center;
  padding: 20px 0;
}

.profile-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 4px;
}

.profile-username {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.profile-info {
  margin-top: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 13px;
  color: #909399;
}

.info-value {
  font-size: 14px;
  color: #303133;
  word-break: break-word;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.post-list {
  min-height: 400px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e4e7ed;
}

@media (max-width: 768px) {
  .my-profile-page :deep(.el-col) {
    margin-bottom: 20px;
  }
}
</style>

