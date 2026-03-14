<template>
  <MainLayout>
    <div class="favorites-page">
      <el-card class="post-list-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">
              <el-icon><Star /></el-icon>
              我的收藏
            </span>
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
          <el-empty v-else description="暂无收藏的帖子，去首页看看吧" />
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
import { ref, onMounted } from 'vue'
import { getMyFavorites } from '@/api/interaction'
import MainLayout from '@/components/MainLayout.vue'
import PostCard from '@/components/PostCard.vue'
import { ElMessage } from 'element-plus'
import { Star } from '@element-plus/icons-vue'

const loading = ref(false)
const posts = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  fetchFavorites()
})

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await getMyFavorites({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    if (res.code === 200 && res.data) {
      posts.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取收藏列表失败：', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchFavorites()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchFavorites()
}
</script>

<style scoped>
.favorites-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
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
  display: flex;
  align-items: center;
  gap: 8px;
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
