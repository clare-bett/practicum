<template>
  <div class="user-management">
    <el-card>
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="statusFilter" @change="handleStatusChange">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="1">正常</el-radio-button>
          <el-radio-button :label="0">已冻结</el-radio-button>
        </el-radio-group>

        <el-button type="primary" @click="fetchUsers">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>

      <!-- 用户表格 -->
      <el-table
        v-loading="loading"
        :data="users"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="username" label="用户名" width="150" />

        <el-table-column prop="nickname" label="昵称" width="150" />

        <el-table-column prop="email" label="邮箱" min-width="180" />

        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.role === 'ADMIN'" type="danger">管理员</el-tag>
            <el-tag v-else type="info">普通用户</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 0" type="danger">已冻结</el-tag>
            <el-tag v-else type="warning">未知</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <template v-if="row.role !== 'ADMIN'">
              <el-button
                v-if="row.status === 1"
                type="warning"
                size="small"
                @click="handleFreeze(row)"
              >
                冻结
              </el-button>
              <el-button
                v-else
                type="success"
                size="small"
                @click="handleUnfreeze(row)"
              >
                恢复
              </el-button>
            </template>
            <el-tag v-else type="info" size="small">管理员</el-tag>
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
import { getAdminUserList, adminFreezeUser, adminUnfreezeUser } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const users = ref([])
const statusFilter = ref(null)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  fetchUsers()
})

const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: statusFilter.value
    }
    const res = await getAdminUserList(params)
    if (res.code === 200 && res.data) {
      users.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取用户列表失败：', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleStatusChange = () => {
  pageNum.value = 1
  fetchUsers()
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchUsers()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchUsers()
}

const handleFreeze = (row) => {
  ElMessageBox.confirm(
    `确定要冻结用户 "${row.username}" 吗？冻结后该用户将无法登录。`, 
    '冻结用户', 
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await adminFreezeUser(row.id)
      if (res.code === 200) {
        ElMessage.success('冻结成功')
        fetchUsers()
      }
    } catch (error) {
      console.error('冻结失败：', error)
      ElMessage.error(error.message || '冻结失败')
    }
  }).catch(() => {})
}

const handleUnfreeze = (row) => {
  ElMessageBox.confirm(
    `确定要恢复用户 "${row.username}" 吗？恢复后该用户可以正常登录。`, 
    '恢复用户', 
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  ).then(async () => {
    try {
      const res = await adminUnfreezeUser(row.id)
      if (res.code === 200) {
        ElMessage.success('恢复成功')
        fetchUsers()
      }
    } catch (error) {
      console.error('恢复失败：', error)
      ElMessage.error(error.message || '恢复失败')
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
.user-management {
  width: 100%;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e4e7ed;
}
</style>

