<template>
  <MainLayout>
    <div class="create-post-page">
      <el-card class="create-post-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">
              <el-icon><EditPen /></el-icon>
              发布新帖
            </span>
          </div>
        </template>

        <el-form
          ref="postFormRef"
          :model="postForm"
          :rules="postRules"
          label-width="80px"
          class="post-form"
        >
          <el-form-item label="板块" prop="categoryId">
            <el-select
              v-model="postForm.categoryId"
              placeholder="请选择板块"
              size="large"
              style="width: 100%"
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              >
                <span>{{ category.name }}</span>
                <span style="color: #909399; font-size: 13px; margin-left: 8px">
                  {{ category.description }}
                </span>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="标题" prop="title">
            <el-input
              v-model="postForm.title"
              placeholder="请输入帖子标题"
              size="large"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="内容" prop="content">
            <el-input
              v-model="postForm.content"
              type="textarea"
              :rows="15"
              placeholder="请输入帖子内容..."
              maxlength="5000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <div class="form-actions">
              <el-button @click="handleCancel">取消</el-button>
              <el-button
                type="primary"
                :loading="loading"
                @click="handleSubmit"
              >
                发布
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCategoryStore } from '@/stores/category'
import { createPost } from '@/api/post'
import MainLayout from '@/components/MainLayout.vue'
import { ElMessage } from 'element-plus'
import { EditPen } from '@element-plus/icons-vue'

const router = useRouter()
const categoryStore = useCategoryStore()

const categories = computed(() => categoryStore.categories)

const postFormRef = ref(null)
const loading = ref(false)

const postForm = reactive({
  title: '',
  content: '',
  categoryId: null
})

const postRules = {
  categoryId: [
    { required: true, message: '请选择板块', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入帖子标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度在 5 到 200 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入帖子内容', trigger: 'blur' },
    { min: 10, max: 5000, message: '内容长度在 10 到 5000 个字符', trigger: 'blur' }
  ]
}

onMounted(() => {
  categoryStore.fetchCategories()
})

const handleSubmit = async () => {
  if (!postFormRef.value) return

  await postFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await createPost(postForm)
        if (res.code === 200 && res.data) {
          ElMessage.success('发布成功')
          router.push(`/post/${res.data.id}`)
        }
      } catch (error) {
        console.error('发布失败：', error)
        ElMessage.error(error.message || '发布失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleCancel = () => {
  router.back()
}
</script>

<style scoped>
.create-post-page {
  max-width: 900px;
  margin: 0 auto;
}

.create-post-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-form {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  width: 100%;
}

@media (max-width: 768px) {
  .post-form :deep(.el-form-item__label) {
    width: 60px !important;
  }
}
</style>

