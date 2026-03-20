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
            <div class="content-editor">
              <div class="content-toolbar">
                <el-upload
                  action="#"
                  :show-file-list="false"
                  multiple
                  :before-upload="handleContentImageUpload"
                  accept="image/jpeg,image/png,image/gif,image/webp"
                >
                  <el-button size="small" :loading="imageUploading">
                    {{ imageUploading ? "上传中..." : "上传图片" }}
                  </el-button>
                </el-upload>
                <span class="toolbar-tip"
                  >支持多图预览，点击图片可放大，右上角可删除</span
                >
              </div>
              <div v-if="uploadedImages.length > 0" class="image-preview-list">
                <div
                  v-for="(img, idx) in uploadedImages"
                  :key="img + idx"
                  class="preview-item"
                >
                  <el-button
                    class="preview-delete"
                    size="small"
                    type="danger"
                    circle
                    @click.stop="removeImage(idx)"
                  >
                    ×
                  </el-button>
                  <el-image
                    :src="img"
                    :preview-src-list="uploadedImages"
                    :initial-index="idx"
                    fit="cover"
                    class="preview-image"
                  />
                </div>
              </div>
              <el-input
                v-model="postForm.content"
                type="textarea"
                :rows="15"
                placeholder="请输入帖子内容..."
                maxlength="5000"
                show-word-limit
              />
            </div>
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
import { ref, reactive, computed, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useCategoryStore } from "@/stores/category";
import { createPost } from "@/api/post";
import { uploadImage } from "@/api/upload";
import MainLayout from "@/components/MainLayout.vue";
import { ElMessage } from "element-plus";
import { EditPen } from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();
const categoryStore = useCategoryStore();

const categories = computed(() => categoryStore.categories);
const BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const postFormRef = ref(null);
const loading = ref(false);
const imageUploading = ref(false);
const uploadedImages = ref([]);

const postForm = reactive({
  title: "",
  content: "",
  categoryId: null,
});

const postRules = {
  categoryId: [{ required: true, message: "请选择板块", trigger: "change" }],
  title: [
    { required: true, message: "请输入帖子标题", trigger: "blur" },
    {
      min: 2,
      max: 200,
      message: "标题长度在 2 到 200 个字符",
      trigger: "blur",
    },
  ],
  content: [
    { required: true, message: "请输入帖子内容", trigger: "blur" },
    {
      min: 5,
      max: 5000,
      message: "内容长度在 5 到 5000 个字符",
      trigger: "blur",
    },
  ],
};

onMounted(async () => {
  await categoryStore.fetchCategories();
  applyDefaultCategory();
});

watch(
  () => route.query.categoryId,
  () => {
    applyDefaultCategory();
  },
);

const applyDefaultCategory = () => {
  const value = route.query.categoryId;
  if (!value) return;

  const categoryId = Number(value);
  if (!Number.isFinite(categoryId)) return;

  const exists = categories.value.some((item) => item.id === categoryId);
  if (exists) {
    postForm.categoryId = categoryId;
  }
};

const handleContentImageUpload = async (file) => {
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error("图片大小不能超过 5MB");
    return false;
  }

  imageUploading.value = true;
  try {
    const res = await uploadImage(file);
    if (res.code === 200 && res.data) {
      const url = res.data.startsWith("http") ? res.data : BASE_URL + res.data;
      if (!uploadedImages.value.includes(url)) {
        uploadedImages.value.push(url);
      }
      ElMessage.success("图片上传成功");
    } else {
      ElMessage.error(res.message || "图片上传失败");
    }
  } catch (error) {
    ElMessage.error("图片上传失败，请重试");
  } finally {
    imageUploading.value = false;
  }
  return false;
};

const removeImage = (index) => {
  uploadedImages.value.splice(index, 1);
};

const buildPostContent = (text, images) => {
  const pureText = (text || "").trim();
  const imageHtml = images
    .map((url) => `<img src="${url}" alt="帖子图片" />`)
    .join("\n");
  if (pureText && imageHtml) {
    return `${pureText}\n${imageHtml}`;
  }
  return pureText || imageHtml;
};

const handleSubmit = async () => {
  if (!postFormRef.value) return;

  await postFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!postForm.content.trim() && uploadedImages.value.length === 0) {
        ElMessage.warning("请输入内容或上传至少一张图片");
        return;
      }
      loading.value = true;
      try {
        const payload = {
          ...postForm,
          content: buildPostContent(postForm.content, uploadedImages.value),
        };
        const res = await createPost(payload);
        if (res.code === 200 && res.data) {
          ElMessage.success("发布成功");
          await router.push({
            path: `/post/${res.data.id}`,
            query: { aiPending: "1" },
          });
        }
      } catch (error) {
        console.error("发布失败：", error);
        ElMessage.error(error.message || "发布失败");
      } finally {
        loading.value = false;
      }
    }
  });
};

const handleCancel = () => {
  router.back();
};
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

.content-editor {
  width: 100%;
}

.content-toolbar {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.toolbar-tip {
  font-size: 12px;
  color: #909399;
}

.image-preview-list {
  margin-bottom: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.preview-item {
  position: relative;
  width: 72px;
  height: 72px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
}

.preview-delete {
  position: absolute;
  right: 4px;
  top: 4px;
  z-index: 3;
  width: 18px;
  height: 18px;
  min-height: 18px;
  padding: 0;
}

.preview-image {
  width: 100%;
  height: 100%;
  cursor: zoom-in;
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
