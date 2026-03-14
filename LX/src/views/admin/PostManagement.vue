<template>
  <div class="post-management">
    <el-card>
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="statusFilter" @change="handleStatusChange">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="1">正常</el-radio-button>
          <el-radio-button :label="0">已删除</el-radio-button>
        </el-radio-group>

        <el-button type="primary" @click="fetchPosts">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>

      <!-- 帖子表格 -->
      <el-table
        v-loading="loading"
        :data="posts"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <router-link :to="`/post/${row.id}`" target="_blank" class="post-link">
              {{ row.title }}
            </router-link>
          </template>
        </el-table-column>

        <el-table-column prop="categoryName" label="板块" width="120" />

        <el-table-column prop="authorName" label="作者" width="120" />

        <el-table-column label="统计" width="180">
          <template #default="{ row }">
            <div class="stats">
              <el-tag size="small">浏览 {{ row.viewCount }}</el-tag>
              <el-tag size="small" type="success">回复 {{ row.replyCount }}</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">已删除</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="handleRestore(row)"
            >
              恢复
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminPostList, adminDeletePost, adminRestorePost } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const posts = ref([])
const statusFilter = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  fetchPosts()
})

const fetchPosts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: statusFilter.value
    }
    const res = await getAdminPostList(params)
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

const handleStatusChange = () => {
  pageNum.value = 1
  fetchPosts()
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

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除帖子 "${row.title}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await adminDeletePost(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchPosts()
      }
    } catch (error) {
      console.error('删除失败：', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleRestore = (row) => {
  ElMessageBox.confirm(`确定要恢复帖子 "${row.title}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      const res = await adminRestorePost(row.id)
      if (res.code === 200) {
        ElMessage.success('恢复成功')
        fetchPosts()
      }
    } catch (error) {
      console.error('恢复失败：', error)
      ElMessage.error('恢复失败')
    }
  }).catch(() => {})
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}
</script>

<style scoped>
.post-management {
  width: 100%;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.post-link {
  color: #409eff;
  text-decoration: none;
}

.post-link:hover {
  text-decoration: underline;
}

.stats {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e4e7ed;
}
</style>

