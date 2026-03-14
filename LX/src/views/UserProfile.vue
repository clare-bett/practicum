<template>
  <MainLayout>
    <div class="user-profile-page">
      <el-row :gutter="20">
        <!-- 左侧：用户信息 -->
        <el-col :xs="24" :sm="24" :md="8">
          <el-card v-loading="userLoading" class="profile-card">
            <div v-if="user" class="profile-header">
              <el-avatar :size="100" :src="user.avatar">
                {{ user.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <h3 class="profile-name">{{ user.nickname || user.username }}</h3>
              <p class="profile-username">@{{ user.username }}</p>
              <el-tag v-if="user.role === 'ADMIN'" type="danger" size="small">
                管理员
              </el-tag>
            </div>

            <div v-if="user" class="profile-info">
              <div class="info-item" v-if="user.bio">
                <span class="info-label">个人简介</span>
                <span class="info-value">{{ user.bio }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">注册时间</span>
                <span class="info-value">{{ formatTime(user.createTime) }}</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧：用户帖子 -->
        <el-col :xs="24" :sm="24" :md="16">
          <el-card class="posts-card">
            <template #header>
              <div class="card-header">
                <span class="header-title">TA的帖子</span>
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
              <el-empty v-else description="TA还没有发布过帖子" />
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
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getUserInfo } from '@/api/user'
import { getPostListByAuthor } from '@/api/post'
import MainLayout from '@/components/MainLayout.vue'
import PostCard from '@/components/PostCard.vue'
import { ElMessage } from 'element-plus'
import { formatTime } from '@/utils/format'

const route = useRoute()

const userId = computed(() => Number(route.params.id))
const user = ref(null)
const userLoading = ref(false)

const loading = ref(false)
const posts = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  fetchUser()
  fetchPosts()
})

watch(() => route.params.id, () => {
  if (route.name === 'userProfile') {
    fetchUser()
    pageNum.value = 1
    fetchPosts()
  }
})

const fetchUser = async () => {
  userLoading.value = true
  try {
    const res = await getUserInfo(userId.value)
    if (res.code === 200 && res.data) {
      user.value = res.data
    }
  } catch (error) {
    console.error('获取用户信息失败：', error)
    ElMessage.error('获取用户信息失败')
  } finally {
    userLoading.value = false
  }
}

const fetchPosts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const res = await getPostListByAuthor(userId.value, params)
    if (res.code === 200 && res.data) {
      posts.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取帖子列表失败：', error)
    ElMessage.error('获取帖子列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchPosts()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchPosts()
}
</script>

<style scoped>
.user-profile-page {
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
  .user-profile-page :deep(.el-col) {
    margin-bottom: 20px;
  }
}
</style>

