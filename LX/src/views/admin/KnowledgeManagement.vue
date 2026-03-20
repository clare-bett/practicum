<template>
  <div class="knowledge-management">
    <el-card>
      <div class="action-bar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加知识
        </el-button>
        <el-button @click="fetchList">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="list"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            {{ row.category || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容摘要" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            {{ (row.content || '').slice(0, 80) }}{{ (row.content || '').length > 80 ? '...' : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && list.length === 0" description="暂无知识库内容，请点击「添加知识」上传校历、学院信息等" />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="添加知识"
      width="640px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="如：2025-2026 校历" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="如：校历、学院介绍、办事指南（可选）" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="12"
            placeholder="粘贴或输入知识库文本，将用于 RAG 检索回答用户提问"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getKnowledgeList, addKnowledge, deleteKnowledge } from '@/api/knowledge'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  title: '',
  category: '',
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

function formatDateTime(val) {
  if (!val) return '-'
  const d = new Date(val)
  return d.toLocaleString('zh-CN')
}

onMounted(() => {
  fetchList()
})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getKnowledgeList()
    if (res.code === 200 && res.data) {
      list.value = res.data
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('获取知识库列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.title = ''
  form.category = ''
  form.content = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      const res = await addKnowledge({
        title: form.title,
        category: form.category || undefined,
        content: form.content
      })
      if (res.code === 200) {
        ElMessage.success('添加成功')
        dialogVisible.value = false
        fetchList()
      }
    } catch (e) {
      console.error(e)
      ElMessage.error(e.message || '添加失败')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除「${row.title}」吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteKnowledge(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchList()
      }
    } catch (e) {
      console.error(e)
      ElMessage.error(e.message || '删除失败')
    }
  }).catch(() => {})
}
</script>

<style scoped>
.knowledge-management .action-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
}
</style>
