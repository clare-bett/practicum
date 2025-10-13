<template>
  <MainLayout>
    <div class="home-page">
      <!-- 分类导航 -->
      <div class="category-section">
        <el-card class="category-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">论坛板块</span>
            </div>
          </template>
          <div class="category-list">
            <div 
              class="category-item" 
              :class="{ active: !selectedCategory }"
              @click="selectCategory(null)"
            >
              <el-icon><Notebook /></el-icon>
              <span>全部</span>
            </div>
            <div 
              v-for="category in categories" 
              :key="category.id"
              class="category-item"
              :class="{ active: selectedCategory === category.id }"
              @click="selectCategory(category.id)"
            >
              <el-icon><CollectionTag /></el-icon>
              <span>{{ category.name }}</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 帖子列表 -->
      <div class="post-section">
        <el-card class="post-list-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">
                {{ selectedCategory ? getCategoryName(selectedCategory) : '最新帖子' }}
              </span>
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
            <el-empty v-else description="暂无帖子" />
          </div>

          <!-- 分页 -->
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
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useCategoryStore } from '@/stores/category'
import { getPostList, getPostListByCategory } from '@/api/post'
import MainLayout from '@/components/MainLayout.vue'
import PostCard from '@/components/PostCard.vue'
import { ElMessage } from 'element-plus'
import { 
  Notebook, 
  CollectionTag, 
  EditPen 
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const categoryStore = useCategoryStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const categories = computed(() => categoryStore.categories)

const loading = ref(false)
const posts = ref([])
const selectedCategory = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  categoryStore.fetchCategories()
  fetchPosts()
})

const fetchPosts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }

    let res
    if (selectedCategory.value) {
      res = await getPostListByCategory(selectedCategory.value, params)
    } else {
      res = await getPostList(params)
    }

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

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  pageNum.value = 1
  fetchPosts()
}

const getCategoryName = (categoryId) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '全部'
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
.home-page {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 20px;
}

.category-card,
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

.category-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
}

.category-item:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

.category-item.active {
  background-color: #ecf5ff;
  color: #409eff;
  font-weight: 500;
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

@media (max-width: 968px) {
  .home-page {
    grid-template-columns: 1fr;
  }

  .category-section {
    order: 2;
  }

  .post-section {
    order: 1;
  }

  .category-list {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .category-item {
    flex: 0 0 auto;
  }
}

@media (max-width: 768px) {
  .pagination :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>

