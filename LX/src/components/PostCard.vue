<template>
  <div class="post-card" @click="goToDetail">
    <div class="post-header">
      <div class="post-meta">
        <el-tag v-if="post.isTop" type="danger" size="small">置顶</el-tag>
        <el-tag v-if="post.isEssence" type="warning" size="small">精华</el-tag>
        <el-tag type="info" size="small">{{ post.categoryName }}</el-tag>
      </div>
    </div>

    <h3 class="post-title">{{ post.title }}</h3>

    <div class="post-content" v-if="post.content">
      {{ truncateText(post.content, 150) }}
    </div>

    <div class="post-footer">
      <div class="post-author">
        <el-avatar :size="28" :src="post.authorAvatar">
          {{ post.authorName?.charAt(0) || 'U' }}
        </el-avatar>
        <span class="author-name">{{ post.authorName }}</span>
        <span class="post-time">{{ formatTime(post.createTime) }}</span>
      </div>

      <div class="post-stats">
        <span class="stat-item">
          <el-icon><View /></el-icon>
          {{ formatNumber(post.viewCount || 0) }}
        </span>
        <span class="stat-item">
          <el-icon><ChatDotRound /></el-icon>
          {{ formatNumber(post.replyCount || 0) }}
        </span>
        <span class="stat-item">
          <el-icon><Star /></el-icon>
          {{ formatNumber(post.likeCount || 0) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { formatTime, formatNumber, truncateText } from '@/utils/format'
import { View, ChatDotRound, Star } from '@element-plus/icons-vue'

const props = defineProps({
  post: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const goToDetail = () => {
  router.push(`/post/${props.post.id}`)
}
</script>

<style scoped>
.post-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e4e7ed;
}

.post-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.post-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  line-height: 1.5;
}

.post-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 16px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.post-time {
  font-size: 13px;
  color: #909399;
}

.post-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

@media (max-width: 768px) {
  .post-card {
    padding: 16px;
  }

  .post-title {
    font-size: 16px;
  }

  .post-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>

