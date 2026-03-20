<template>
  <div 
    class="post-card" 
    :class="{ 'is-top': post.isTop, 'is-essence': post.isEssence && !post.isTop }"
    @click="goToDetail"
  >
    <div class="post-card-inner">
      <div class="post-header">
        <div class="post-meta">
          <el-tag v-if="post.isTop" type="danger" size="small">置顶</el-tag>
          <el-tag v-if="post.isEssence" type="warning" size="small">精华</el-tag>
          <el-tag type="info" size="small">{{ post.categoryName }}</el-tag>
        </div>
      </div>

      <h3 class="post-title">{{ post.title }}</h3>

      <div class="post-images" v-if="postImages.length > 0">
        <div
          v-for="(img, idx) in postImages.slice(0, 3)"
          :key="img + idx"
          class="post-image-item"
        >
          <img :src="img" alt="帖子图片" class="post-image" />
        </div>
        <div v-if="postImages.length > 3" class="more-image-count">
          +{{ postImages.length - 3 }}
        </div>
      </div>

      <div class="post-content" v-if="postPreviewText">
        {{ postPreviewText }}
      </div>

      <div class="post-footer">
        <div class="post-author" @click.stop="goToUserProfile(post.authorId)">
          <el-avatar :size="28" :src="getAvatarUrl(post.authorAvatar)" class="clickable-avatar">
            {{ post.authorName?.charAt(0) || 'U' }}
          </el-avatar>
          <span class="author-name clickable-name">{{ post.authorName }}</span>
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
  </div>
</template>

<script setup>
import { computed } from 'vue'
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
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const postImages = computed(() => {
  const raw = props.post?.content || ''
  const matches = raw.match(/<img[^>]*src=["']([^"']+)["'][^>]*>/gi) || []
  return matches
    .map((item) => {
      const srcMatch = item.match(/src=["']([^"']+)["']/i)
      const src = srcMatch?.[1] || ''
      if (!src) return ''
      return src.startsWith('http') ? src : BASE_URL + src
    })
    .filter(Boolean)
})

const postPreviewText = computed(() => {
  const raw = props.post?.content || ''
  const plain = raw
    .replace(/<img[^>]*>/gi, '')
    .replace(/<[^>]+>/g, '')
    .replace(/\s+/g, ' ')
    .trim()
  if (plain) return truncateText(plain, 150)
  if (postImages.value.length > 0) return `共 ${postImages.value.length} 张图片`
  return ''
})
const getAvatarUrl = (src) => {
  if (!src) return ''
  if (src.startsWith('http')) return src
  return BASE_URL + src
}

const goToDetail = () => {
  router.push(`/post/${props.post.id}`)
}

const goToUserProfile = (userId) => {
  if (!Number.isFinite(Number(userId))) return
  router.push(`/user/${Number(userId)}`)
}
</script>

<style scoped>
.post-card {
  background: #fff;
  border-radius: 12px;
  padding: 0;
  margin-bottom: 14px;
  cursor: pointer;
  transition: all 0.28s ease;
  border: 1px solid rgba(64, 158, 255, 0.1);
  overflow: hidden;
  position: relative;
}

/* 卡片顶部渐变色条 */
.post-card::before {
  content: '';
  display: block;
  height: 3px;
  background: linear-gradient(to right, #409eff, #36cfc9);
  width: 0;
  transition: width 0.35s ease;
}

.post-card:hover::before {
  width: 100%;
}

.post-card:hover {
  box-shadow: 0 8px 28px rgba(64, 158, 255, 0.18);
  transform: translateY(-3px);
  border-color: rgba(64, 158, 255, 0.25);
}

/* 置顶帖顶部色条始终展开 */
.post-card.is-top::before {
  background: linear-gradient(to right, #f56c6c, #ff8c69);
  width: 100%;
}

/* 精华帖顶部色条始终展开 */
.post-card.is-essence::before {
  background: linear-gradient(to right, #e6a23c, #f7c948);
  width: 100%;
}

.post-card-inner {
  padding: 18px 20px 16px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.post-meta {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.post-title {
  font-size: 17px;
  font-weight: 600;
  color: #1a2a4a;
  margin-bottom: 10px;
  line-height: 1.5;
  transition: color 0.2s;
}

.post-card:hover .post-title {
  color: #409eff;
}

.post-content {
  font-size: 14px;
  color: #5a7090;
  line-height: 1.7;
  margin-bottom: 14px;
  background: #f8fbff;
  padding: 8px 12px;
  border-radius: 6px;
  border-left: 3px solid #bae7ff;
}

.post-images {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.post-image-item {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid rgba(64, 158, 255, 0.2);
  background: #f2f8ff;
  flex-shrink: 0;
}

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.more-image-count {
  height: 64px;
  min-width: 48px;
  padding: 0 10px;
  border-radius: 8px;
  border: 1px dashed rgba(64, 158, 255, 0.35);
  color: #409eff;
  background: rgba(64, 158, 255, 0.08);
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
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
  color: #4a6080;
  font-weight: 500;
}

.clickable-avatar,
.clickable-name {
  cursor: pointer;
}

.clickable-name:hover {
  color: #409eff;
}

.post-time {
  font-size: 12px;
  color: #8fa8c0;
}

.post-stats {
  display: flex;
  gap: 14px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #8fa8c0;
  transition: color 0.2s;
}

.stat-item:hover {
  color: #409eff;
}

@media (max-width: 768px) {
  .post-card-inner {
    padding: 14px 16px;
  }

  .post-title {
    font-size: 15px;
  }

  .post-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>

