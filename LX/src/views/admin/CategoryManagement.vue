<template>
  <div class="category-management">
    <el-card>
      <!-- 操作栏 -->
      <div class="action-bar">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建板块
        </el-button>
        <el-button @click="fetchCategories">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>

      <!-- 板块表格 -->
      <el-table
        v-loading="loading"
        :data="categories"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="name" label="名称" width="150" />

        <el-table-column prop="description" label="描述" min-width="200" />

        <el-table-column prop="icon" label="图标" width="100" />

        <el-table-column prop="sortOrder" label="排序" width="100" />

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="80px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入板块名称" />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入板块描述"
          />
        </el-form-item>

        <el-form-item label="图标" prop="icon">
          <el-input v-model="formData.icon" placeholder="请输入图标名称（可选）" />
        </el-form-item>

        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="formData.sortOrder"
            :min="0"
            :max="999"
            placeholder="排序值，越大越靠前"
          />
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="submitLoading"
          @click="handleSubmit"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getCategoryList } from '@/api/category'
import { createCategory, updateCategory, deleteCategory } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const categories = ref([])

const dialogVisible = ref(false)
const dialogMode = ref('create') // 'create' | 'edit'
const submitLoading = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  name: '',
  description: '',
  icon: '',
  sortOrder: 0,
  status: 1
})

const formRules = {
  name: [
    { required: true, message: '请输入板块名称', trigger: 'blur' },
    { min: 2, max: 100, message: '名称长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ]
}

const dialogTitle = computed(() => {
  return dialogMode.value === 'create' ? '新建板块' : '编辑板块'
})

onMounted(() => {
  fetchCategories()
})

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getCategoryList()
    if (res.code === 200 && res.data) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('获取板块列表失败：', error)
    ElMessage.error('获取板块列表失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  dialogMode.value = 'create'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogMode.value = 'edit'
  formData.id = row.id
  formData.name = row.name
  formData.description = row.description || ''
  formData.icon = row.icon || ''
  formData.sortOrder = row.sortOrder || 0
  formData.status = row.status
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除板块 "${row.name}" 吗？删除后该板块下的帖子将无法访问。`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteCategory(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchCategories()
      }
    } catch (error) {
      console.error('删除失败：', error)
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data = {
          name: formData.name,
          description: formData.description,
          icon: formData.icon,
          sortOrder: formData.sortOrder,
          status: formData.status
        }

        let res
        if (dialogMode.value === 'create') {
          res = await createCategory(data)
        } else {
          res = await updateCategory(formData.id, data)
        }

        if (res.code === 200) {
          ElMessage.success(dialogMode.value === 'create' ? '创建成功' : '更新成功')
          dialogVisible.value = false
          fetchCategories()
        }
      } catch (error) {
        console.error('操作失败：', error)
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const resetForm = () => {
  formData.id = null
  formData.name = ''
  formData.description = ''
  formData.icon = ''
  formData.sortOrder = 0
  formData.status = 1
  if (formRef.value) {
    formRef.value.resetFields()
  }
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
.category-management {
  width: 100%;
}

.action-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
</style>

