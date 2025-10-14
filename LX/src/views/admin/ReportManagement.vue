<template>
  <div class="report-management">
    <div class="page-header">
      <h2>举报管理</h2>
      <div class="header-stats">
        <el-tag type="warning" size="large">
          待处理：{{ pendingCount }} 条
        </el-tag>
      </div>
    </div>

    <el-card>
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-radio-group v-model="filterStatus" @change="handleFilterChange">
          <el-radio-button :label="null">全部</el-radio-button>
          <el-radio-button :label="0">待处理</el-radio-button>
          <el-radio-button :label="1">已处理</el-radio-button>
          <el-radio-button :label="2">已驳回</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 举报列表 -->
      <el-table 
        :data="reportList" 
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="reportTypeName" label="类型" width="100" />
        
        <el-table-column label="被举报内容" min-width="200">
          <template #default="{ row }">
            <div class="target-content">{{ row.targetContent }}</div>
          </template>
        </el-table-column>
        
        <el-table-column prop="reporterName" label="举报人" width="120" />
        
        <el-table-column label="举报原因" min-width="180">
          <template #default="{ row }">
            <el-tooltip :content="row.reason" placement="top">
              <div class="reason-text">{{ row.reason }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <el-tag 
              :type="getStatusType(row.status)"
              size="small"
            >
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="处理信息" width="150">
          <template #default="{ row }">
            <div v-if="row.status !== 0" class="handle-info">
              <div>{{ row.handlerName }}</div>
              <div class="handle-time">{{ formatTime(row.handleTime) }}</div>
            </div>
            <span v-else class="pending-text">待处理</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="举报时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div v-if="row.status === 0" class="action-buttons">
              <el-button 
                type="danger" 
                size="small"
                @click="handleAction(row, 'delete')"
              >
                删除内容
              </el-button>
              <el-button 
                size="small"
                @click="handleAction(row, 'reject')"
              >
                驳回
              </el-button>
            </div>
            <div v-else class="handle-result">
              <el-tooltip :content="row.handleResult" placement="top">
                <el-tag size="small">{{ row.handleResult }}</el-tag>
              </el-tooltip>
            </div>
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

    <!-- 处理对话框 -->
    <el-dialog 
      v-model="handleDialogVisible" 
      :title="getActionTitle()"
      width="500px"
    >
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="举报类型">
          <span>{{ currentReport?.reportTypeName }}</span>
        </el-form-item>
        <el-form-item label="被举报内容">
          <div class="target-content-full">{{ currentReport?.targetContent }}</div>
        </el-form-item>
        <el-form-item label="举报原因">
          <div class="reason-full">{{ currentReport?.reason }}</div>
        </el-form-item>
        <el-form-item label="处理结果" required>
          <el-input
            v-model="handleForm.handleResult"
            type="textarea"
            :rows="3"
            placeholder="请输入处理结果说明"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="confirmHandle"
          :loading="handleSubmitting"
        >
          确认处理
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getReportList, handleReport as handleReportApi, getPendingReportCount } from '@/api/report'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatTime } from '@/utils/format'

const loading = ref(false)
const reportList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref(0) // 默认显示待处理
const pendingCount = ref(0)

const handleDialogVisible = ref(false)
const handleSubmitting = ref(false)
const currentReport = ref(null)
const currentAction = ref('')
const handleForm = ref({
  handleResult: ''
})

onMounted(() => {
  fetchReportList()
  fetchPendingCount()
})

const fetchReportList = async () => {
  loading.value = true
  try {
    const params = {
      status: filterStatus.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const res = await getReportList(params)
    if (res.code === 200 && res.data) {
      reportList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取举报列表失败：', error)
    ElMessage.error('获取举报列表失败')
  } finally {
    loading.value = false
  }
}

const fetchPendingCount = async () => {
  try {
    const res = await getPendingReportCount()
    if (res.code === 200) {
      pendingCount.value = res.data || 0
    }
  } catch (error) {
    console.error('获取待处理数量失败：', error)
  }
}

const handleFilterChange = () => {
  pageNum.value = 1
  fetchReportList()
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchReportList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchReportList()
}

const getStatusType = (status) => {
  const map = {
    0: 'warning',
    1: 'success',
    2: 'info'
  }
  return map[status] || 'info'
}

const getActionTitle = () => {
  const map = {
    delete: '删除违规内容',
    reject: '驳回举报'
  }
  return map[currentAction.value] || '处理举报'
}

const handleAction = (report, action) => {
  currentReport.value = report
  currentAction.value = action
  handleForm.value.handleResult = ''
  handleDialogVisible.value = true
}

const confirmHandle = async () => {
  if (!handleForm.value.handleResult.trim()) {
    ElMessage.warning('请填写处理结果')
    return
  }

  const actionText = {
    delete: '删除内容',
    reject: '驳回举报'
  }[currentAction.value]

  try {
    await ElMessageBox.confirm(
      `确定要${actionText}吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    handleSubmitting.value = true
    try {
      const params = {
        handleResult: handleForm.value.handleResult,
        action: currentAction.value
      }
      
      const res = await handleReportApi(currentReport.value.id, params)
      if (res.code === 200) {
        ElMessage.success('处理成功')
        handleDialogVisible.value = false
        fetchReportList()
        fetchPendingCount()
      }
    } catch (error) {
      console.error('处理举报失败：', error)
      ElMessage.error(error.message || '处理失败')
    } finally {
      handleSubmitting.value = false
    }
  } catch {
    // 用户取消操作
  }
}
</script>

<style scoped>
.report-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.header-stats {
  display: flex;
  gap: 16px;
}

.filter-bar {
  margin-bottom: 20px;
}

.target-content,
.reason-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.target-content-full,
.reason-full {
  padding: 12px;
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
  line-height: 1.6;
  word-break: break-all;
}

.handle-info {
  font-size: 12px;
}

.handle-time {
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.pending-text {
  color: var(--el-color-warning);
  font-size: 12px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.handle-result {
  max-width: 200px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

