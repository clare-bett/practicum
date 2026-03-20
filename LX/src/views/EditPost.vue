<template>
  <MainLayout>
    <div class="edit-post-page">
      <el-card v-loading="pageLoading" class="edit-post-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">
              <el-icon><Edit /></el-icon>
              编辑帖子
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
              maxlength="100"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="内容" prop="content">
            <el-input
              v-model="postForm.content"
              type="textarea"
              :rows="15"
              placeholder="请输入帖子内容..."
              maxlength="10000"
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
                保存修改
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
import { useRouter, useRoute } from 'vue-router'
import { useCategoryStore } from '@/stores/category'
import { useUserStore } from '@/stores/user'
import { getPostById, updatePost } from '@/api/post'
import MainLayout from '@/components/MainLayout.vue'
import { ElMessage } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const categoryStore = useCategoryStore()
const userStore = useUserStore()

const postId = computed(() => Number(route.params.id))
const categories = computed(() => categoryStore.categories)
const currentUserId = computed(() => userStore.userId)
const isAdmin = computed(() => userStore.isAdmin)

const postFormRef = ref(null)
const loading = ref(false)
const pageLoading = ref(false)
const originalPost = ref(null)

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
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入帖子内容', trigger: 'blur' },
    { min: 10, max: 10000, message: '内容长度在 10 到 10000 个字符', trigger: 'blur' }
  ]
}

onMounted(async () => {
  await categoryStore.fetchCategories()
  await fetchPost()
})

const fetchPost = async () => {
  pageLoading.value = true
  try {
    const res = await getPostById(postId.value)
    if (res.code === 200 && res.data) {
      originalPost.value = res.data
      
      // 权限检查：只有作者或管理员可以编辑
      if (res.data.authorId !== currentUserId.value && !isAdmin.value) {
        ElMessage.error('您没有权限编辑此帖子')
        router.back()
        return
      }
      
      // 填充表单
      postForm.title = res.data.title
      postForm.content = res.data.content
      postForm.categoryId = res.data.categoryId
    }
  } catch (error) {
    console.error('获取帖子失败：', error)
    ElMessage.error('获取帖子失败')
    router.back()
  } finally {
    pageLoading.value = false
  }
}

const handleSubmit = async () => {
  if (!postFormRef.value) return

  await postFormRef.value.validate(async (valid) => {
    if (valid) {
      // 检查是否有改动
      const hasChanges = 
        postForm.title !== originalPost.value.title ||
        postForm.content !== originalPost.value.content ||
        postForm.categoryId !== originalPost.value.categoryId

      if (!hasChanges) {
        ElMessage.warning('没有检测到任何修改')
        return
      }

      loading.value = true
      try {
        const res = await updatePost(postId.value, postForm)
        if (res.code === 200) {
          ElMessage.success('编辑成功')
          router.push(`/post/${postId.value}`)
        }
      } catch (error) {
        console.error('编辑失败：', error)
        ElMessage.error(error.message || '编辑失败')
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
.edit-post-page {
  max-width: 900px;
  margin: 0 auto;
}

.edit-post-card {
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

