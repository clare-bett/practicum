<template>
  <MainLayout>
    <div class="category-page">
      <el-card class="category-info-card">
        <div v-loading="categoryLoading" class="category-info">
          <h2 class="category-title">
            <el-icon><CollectionTag /></el-icon>
            {{ category?.name || '板块' }}
          </h2>
          <p class="category-description">{{ category?.description || '暂无描述' }}</p>
        </div>
      </el-card>

      <el-card class="post-list-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">帖子列表</span>
            <el-button 
              type="primary" 
              size="small"
              @click="goToCreate"
              v-if="isLoggedIn"
            >
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
          <el-empty v-else description="该板块暂无帖子" />
        </div>

        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </el-card>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getCategoryById } from '@/api/category'
import { getPostListByCategory } from '@/api/post'
import MainLayout from '@/components/MainLayout.vue'
import PostCard from '@/components/PostCard.vue'
import { ElMessage } from 'element-plus'
import { CollectionTag, EditPen } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)

const categoryId = computed(() => Number(route.params.id))
const category = ref(null)
const categoryLoading = ref(false)
const loading = ref(false)
const posts = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  fetchCategory()
  fetchPosts()
})

watch(() => route.params.id, () => {
  if (route.name === 'category') {
    fetchCategory()
    pageNum.value = 1
    fetchPosts()
  }
})

const fetchCategory = async () => {
  categoryLoading.value = true
  try {
    const res = await getCategoryById(categoryId.value)
    if (res.code === 200 && res.data) {
      category.value = res.data
    }
  } catch (error) {
    console.error('获取板块信息失败：', error)
    ElMessage.error('获取板块信息失败')
  } finally {
    categoryLoading.value = false
  }
}

const fetchPosts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const res = await getPostListByCategory(categoryId.value, params)
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
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchPosts()
}

const goToCreate = () => {
  router.push('/post/create')
}
</script>

<style scoped>
.category-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.category-info-card {
  border-radius: 8px;
}

.category-info {
  text-align: center;
  padding: 20px;
}

.category-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 12px;
}

.category-description {
  color: #606266;
  font-size: 14px;
}

.post-list-card {
  border-radius: 8px;
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
</style>

