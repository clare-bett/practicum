<template>
  <MainLayout>
    <div class="home-page">
      <!-- 分类导航 -->
      <div class="category-section">
        <el-card class="category-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">校园板块</span>
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
                <template v-if="searchKeyword">
                  搜索结果："{{ searchKeyword }}"
                  <el-tag size="small" type="info" style="margin-left: 8px">
                    {{ total }} 个结果
                  </el-tag>
                </template>
                <template v-else>
                  {{
                    selectedCategory
                      ? getCategoryName(selectedCategory)
                      : "最新动态"
                  }}
                </template>
              </span>
              <div class="header-actions">
                <el-button
                  v-if="searchKeyword"
                  size="small"
                  @click="clearSearch"
                >
                  <el-icon><Close /></el-icon>
                  清除搜索
                </el-button>
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
            </div>
          </template>

          <div v-loading="loading" class="post-list">
            <template v-if="posts.length > 0">
              <PostCard v-for="post in posts" :key="post.id" :post="post" />
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
import { ref, computed, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import { useCategoryStore } from "@/stores/category";
import { getPostList, getPostListByCategory, searchPosts } from "@/api/post";
import MainLayout from "@/components/MainLayout.vue";
import PostCard from "@/components/PostCard.vue";
import { ElMessage } from "element-plus";
import {
  Notebook,
  CollectionTag,
  EditPen,
  Close,
} from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const categoryStore = useCategoryStore();

const isLoggedIn = computed(() => userStore.isLoggedIn);
const categories = computed(() => categoryStore.categories);

const loading = ref(false);
const posts = ref([]);
const selectedCategory = ref(null);
const searchKeyword = ref("");
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

onMounted(() => {
  categoryStore.fetchCategories();
  // 初始化URL中的搜索和板块参数
  searchKeyword.value = route.query.keyword || "";
  selectedCategory.value = searchKeyword.value
    ? null
    : parseCategoryId(route.query.categoryId);
  fetchPosts();
});

// 监听路由变化
watch(
  () => [route.query.keyword, route.query.categoryId],
  ([newKeyword, newCategoryId]) => {
    searchKeyword.value = newKeyword || "";
    selectedCategory.value = searchKeyword.value
      ? null
      : parseCategoryId(newCategoryId); // 搜索时清除分类选择
    pageNum.value = 1;
    fetchPosts();
  },
);

const parseCategoryId = (value) => {
  if (!value) return null;
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : null;
};

const fetchPosts = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    };

    let res;
    // 优先执行搜索
    if (searchKeyword.value) {
      res = await searchPosts(searchKeyword.value, params);
    } else if (selectedCategory.value) {
      res = await getPostListByCategory(selectedCategory.value, params);
    } else {
      res = await getPostList(params);
    }

    if (res.code === 200 && res.data) {
      posts.value = res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("获取帖子列表失败：", error);
    ElMessage.error("获取帖子列表失败");
  } finally {
    loading.value = false;
  }
};

const selectCategory = (categoryId) => {
  pageNum.value = 1;
  // 切换板块时清除搜索关键词，并同步到URL，watch会自动刷新列表
  router.replace({
    path: "/",
    query: categoryId ? { categoryId: String(categoryId) } : {},
  });
};

const getCategoryName = (categoryId) => {
  const category = categories.value.find((c) => c.id === categoryId);
  return category ? category.name : "全部";
};

const handlePageChange = (page) => {
  pageNum.value = page;
  fetchPosts();
  window.scrollTo({ top: 0, behavior: "smooth" });
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  pageNum.value = 1;
  fetchPosts();
};

const clearSearch = () => {
  const query = selectedCategory.value
    ? { categoryId: String(selectedCategory.value) }
    : {};
  pageNum.value = 1;
  router.replace({ path: "/", query });
};

const goToCreate = () => {
  const query = selectedCategory.value
    ? { categoryId: String(selectedCategory.value) }
    : {};
  router.push({ path: "/post/create", query });
};
</script>

<style scoped>
.home-page {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 20px;
}

/* 分类卡片标题渐变 */
.category-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #409eff, #36cfc9);
  border-radius: 10px 10px 0 0;
  padding: 14px 18px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  display: flex;
  align-items: center;
}

/* 帖子列表卡片标题 */
.post-list-card :deep(.el-card__header) {
  border-bottom: 2px solid #e6f4ff;
}

.post-list-card .header-title {
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.25s ease;
  color: #5a7090;
  border-left: 3px solid transparent;
}

.category-item:hover {
  background-color: #e6f4ff;
  color: #409eff;
  border-left-color: #69c0ff;
  transform: translateX(3px);
}

.category-item.active {
  background: linear-gradient(to right, #e6f4ff, #f0f8ff);
  color: #409eff;
  font-weight: 600;
  border-left-color: #409eff;
}

.post-list {
  min-height: 400px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e6f4ff;
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
    border-left: none;
    border-bottom: 3px solid transparent;
  }

  .category-item:hover,
  .category-item.active {
    transform: none;
    border-left-color: transparent;
    border-bottom-color: #409eff;
  }
}

@media (max-width: 768px) {
  .pagination :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
