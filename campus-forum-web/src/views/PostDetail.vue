<template>
  <MainLayout>
    <div class="post-detail-page">
      <!-- 帖子内容 -->
      <el-card v-loading="postLoading" class="post-card">
        <div v-if="post" class="post-content">
          <div class="post-header">
            <div class="post-meta">
              <el-tag v-if="post.isTop" type="danger" size="small">置顶</el-tag>
              <el-tag v-if="post.isEssence" type="warning" size="small">精华</el-tag>
              <el-tag type="info" size="small">
                <router-link :to="`/category/${post.categoryId}`">
                  {{ post.categoryName }}
                </router-link>
              </el-tag>
            </div>

            <el-dropdown v-if="canEdit" trigger="click">
              <el-button text>
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleEdit">
                    <el-icon><Edit /></el-icon> 编辑
                  </el-dropdown-item>
                  <el-dropdown-item @click="handleDelete">
                    <el-icon><Delete /></el-icon> 删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <h1 class="post-title">{{ post.title }}</h1>

          <div class="post-author-info">
            <el-avatar
              :size="48"
              :src="authorAvatarUrl"
              class="clickable-avatar"
              @click="goToUserProfile(post.authorId)"
            >
              {{ post.authorName?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="author-details">
              <div
                class="author-name clickable-name"
                @click="goToUserProfile(post.authorId)"
              >
                {{ post.authorName }}
              </div>
              <div class="post-time">{{ formatTime(post.createTime) }}</div>
            </div>
          </div>

          <div class="post-body" v-html="formatContent(postTextContent)"></div>
          <div v-if="postImageList.length > 0" class="post-image-list">
            <div
              v-for="(img, idx) in postImageList"
              :key="img + idx"
              class="post-image-item"
            >
              <el-image
                :src="img"
                :preview-src-list="postImageList"
                :initial-index="idx"
                fit="cover"
                class="post-image"
              />
            </div>
          </div>

          <div class="post-stats">
            <span class="stat-item">
              <el-icon><View /></el-icon>
              浏览 {{ formatNumber(post.viewCount || 0) }}
            </span>
            <span class="stat-item">
              <el-icon><ChatDotRound /></el-icon>
              回复 {{ formatNumber(post.replyCount || 0) }}
            </span>
            <span class="stat-item">
              <el-icon><Star /></el-icon>
              点赞 {{ formatNumber(post.likeCount || 0) }}
            </span>
          </div>

          <!-- 互动按钮 -->
          <div v-if="isLoggedIn" class="post-actions">
            <el-button 
              :type="hasLiked ? 'primary' : 'default'" 
              :icon="Star"
              @click="toggleLike"
              :loading="likeLoading"
            >
              {{ hasLiked ? '已点赞' : '点赞' }}
            </el-button>
            <el-button 
              :type="hasFavorited ? 'warning' : 'default'" 
              :icon="StarFilled"
              @click="toggleFavorite"
              :loading="favoriteLoading"
            >
              {{ hasFavorited ? '已收藏' : '收藏' }}
            </el-button>
            <el-button 
              v-if="!canEdit"
              :icon="Warning"
              @click="showReportDialog"
            >
              举报
            </el-button>
          </div>
        </div>
      </el-card>

      <!-- 举报对话框 -->
      <el-dialog 
        v-model="reportDialogVisible" 
        title="举报帖子" 
        width="500px"
      >
        <el-form :model="reportForm">
          <el-form-item label="举报原因">
            <el-input
              v-model="reportForm.reason"
              type="textarea"
              :rows="4"
              maxlength="500"
              show-word-limit
              placeholder="请详细说明举报原因，我们会尽快处理"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="reportDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitReport"
            :loading="reportSubmitting"
          >
            提交
          </el-button>
        </template>
      </el-dialog>

      <!-- 回复列表 -->
      <el-card class="reply-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">
              回复 ({{ total }})
            </span>
          </div>
        </template>

        <!-- 发布回复 -->
        <div v-if="isLoggedIn" class="reply-editor">
          <div class="reply-toolbar">
            <el-button 
              size="small" 
              @click="mentionAI"
              :icon="ChatDotRound"
            >
              @AI助手
            </el-button>
            <span class="toolbar-tip">
              <el-icon><InfoFilled /></el-icon>
              在回复中提及 @AI助手 或 @AI，将获得AI智能回复
            </span>
          </div>
          <el-input
            ref="replyInputRef"
            v-model="replyContent"
            type="textarea"
            :rows="4"
            placeholder="写下你对帖子的回复... 提示：输入 @AI助手 可以召唤AI为你解答"
            maxlength="500"
            show-word-limit
          />
          <div class="reply-editor-actions">
            <el-button 
              type="primary" 
              :loading="replyLoading"
              @click="handleSubmitReply"
            >
              发布回复
            </el-button>
          </div>
        </div>

        <div v-else class="login-tip">
          <span>请先</span>
          <router-link to="/login" class="login-link">登录</router-link>
          <span>后回复</span>
        </div>

        <!-- AI回复等待提示 -->
        <div v-if="isWaitingAIReply" class="ai-waiting-tip">
          <el-icon class="ai-spin"><Loading /></el-icon>
          AI助手正在思考回复中，将自动刷新...
        </div>

        <!-- 回复列表 -->
        <div 
          v-loading="replyLoading" 
          class="reply-list"
          @click.self="handleClickOutside"
        >
          <template v-if="replies.length > 0">
            <ReplyItem 
              v-for="reply in replies" 
              :key="reply.id" 
              :reply="reply"
              :activeReplyId="activeReplyId"
              :postAuthorId="post?.authorId"
              @reply="handleReplyTo"
              @delete="handleDeleteReply"
              @submitChildReply="handleSubmitChildReply"
            />
          </template>
          <el-empty v-else description="暂无回复" @click.self="handleClickOutside" />
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="pageNum"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </el-card>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getPostById, deletePost } from '@/api/post'
import { getReplyListByPost, createReply, deleteReply } from '@/api/reply'
import { 
  likePost, 
  unlikePost, 
  checkLikeStatus,
  favoritePost,
  unfavoritePost,
  checkFavoriteStatus
} from '@/api/interaction'
import { submitReport as submitReportApi } from '@/api/report'
import MainLayout from '@/components/MainLayout.vue'
import ReplyItem from '@/components/ReplyItem.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { formatTime, formatNumber } from '@/utils/format'
import {
  View,
  ChatDotRound,
  Star,
  StarFilled,
  MoreFilled,
  Edit,
  Delete,
  InfoFilled,
  Warning,
  Loading
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const isLoggedIn = computed(() => userStore.isLoggedIn)
const currentUserId = computed(() => userStore.userId)

const postId = computed(() => Number(route.params.id))
const post = ref(null)
const postLoading = ref(false)
const postImageList = computed(() => {
  const raw = post.value?.content || ''
  const imageMatches = raw.match(/<img[^>]*src=["']([^"']+)["'][^>]*>/gi) || []
  return imageMatches
    .map((item) => {
      const srcMatch = item.match(/src=["']([^"']+)["']/i)
      return srcMatch?.[1] || ''
    })
    .filter(Boolean)
})
const postTextContent = computed(() => {
  const raw = post.value?.content || ''
  return raw.replace(/<img[^>]*>/gi, '').replace(/\n{3,}/g, '\n\n').trim()
})
const authorAvatarUrl = computed(() => {
  const src = post.value?.authorAvatar
  if (!src) return ''
  if (src.startsWith('http')) return src
  return BASE_URL + src
})

const replies = ref([])
const replyLoading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const replyContent = ref('')
const replyInputRef = ref(null)
const activeReplyId = ref(null) // 当前激活的一级回复ID（用于显示二级回复输入框）

const canEdit = computed(() => {
  return isLoggedIn.value && post.value && (
    currentUserId.value === post.value.authorId ||
    userStore.isAdmin
  )
})

// 点赞/收藏状态
const hasLiked = ref(false)
const hasFavorited = ref(false)
const likeLoading = ref(false)
const favoriteLoading = ref(false)

// 举报相关
const reportDialogVisible = ref(false)
const reportSubmitting = ref(false)
const reportForm = ref({
  reason: ''
})

const refreshPage = () => {
  window.location.reload()
}

// AI回复轮询相关
let aiPollingTimer = null
const aiPollingCount = ref(0)
const isWaitingAIReply = ref(false)
const AI_POLLING_INTERVAL = 2000
const AI_POLLING_MAX = 20

const countAiReplies = (replyList) => {
  if (!Array.isArray(replyList)) return 0
  let count = 0
  replyList.forEach((item) => {
    if (item?.isAi === 1) count++
    if (Array.isArray(item?.children)) {
      item.children.forEach((child) => {
        if (child?.isAi === 1) count++
      })
    }
  })
  return count
}

const startAIReplyPolling = () => {
  stopAIReplyPolling()
  const baseTopReplyCount = total.value
  const baseAiCount = countAiReplies(replies.value)
  const basePostReplyCount = post.value?.replyCount || 0
  aiPollingCount.value = 0
  isWaitingAIReply.value = true

  aiPollingTimer = setInterval(async () => {
    aiPollingCount.value++
    await Promise.all([fetchPost(), fetchReplies()])

    // 三重检测：一级回复数量变化 / 当前页AI回复数量变化 / 帖子总回复数变化
    const hasNewTopReply = total.value > baseTopReplyCount
    const hasNewAiReply = countAiReplies(replies.value) > baseAiCount
    const hasNewPostReply = (post.value?.replyCount || 0) > basePostReplyCount
    if (hasNewTopReply || hasNewAiReply || hasNewPostReply) {
      stopAIReplyPolling()
      ElMessage.success('AI助手已回复！')
      if (route.query.aiPending === '1') {
        const { aiPending: _aiPending, ...restQuery } = route.query
        router.replace({ path: route.path, query: restQuery })
      }
      return
    }

    // 超时停止
    if (aiPollingCount.value >= AI_POLLING_MAX) {
      stopAIReplyPolling()
    }
  }, AI_POLLING_INTERVAL)
}

const stopAIReplyPolling = () => {
  if (aiPollingTimer) {
    clearInterval(aiPollingTimer)
    aiPollingTimer = null
  }
  isWaitingAIReply.value = false
  aiPollingCount.value = 0
}

onMounted(() => {
  const initPage = async () => {
    await Promise.all([fetchPost(), fetchReplies()])
    if (isLoggedIn.value) {
      checkInteractionStatus()
    }
    if (route.query.aiPending === '1') {
      ElMessage.success('发帖成功，AI助手正在生成首条回复...')
      startAIReplyPolling()
    }
  }
  initPage()
})

onUnmounted(() => {
  stopAIReplyPolling()
})

watch(() => route.params.id, () => {
  if (route.name === 'postDetail') {
    stopAIReplyPolling()
    fetchPost()
    pageNum.value = 1
    fetchReplies()
    if (route.query.aiPending === '1') {
      startAIReplyPolling()
    }
  }
})

const fetchPost = async () => {
  postLoading.value = true
  try {
    const res = await getPostById(postId.value)
    if (res.code === 200 && res.data) {
      post.value = res.data
    }
  } catch (error) {
    console.error('获取帖子详情失败：', error)
    ElMessage.error('获取帖子详情失败')
  } finally {
    postLoading.value = false
  }
}

// 检查点赞和收藏状态
const checkInteractionStatus = async () => {
  try {
    const [likeRes, favoriteRes] = await Promise.all([
      checkLikeStatus(postId.value),
      checkFavoriteStatus(postId.value)
    ])
    
    if (likeRes.code === 200) {
      hasLiked.value = likeRes.data
    }
    if (favoriteRes.code === 200) {
      hasFavorited.value = favoriteRes.data
    }
  } catch (error) {
    console.error('检查互动状态失败：', error)
  }
}

// 切换点赞
const toggleLike = async () => {
  likeLoading.value = true
  try {
    if (hasLiked.value) {
      const res = await unlikePost(postId.value)
      if (res.code === 200) {
        hasLiked.value = false
        post.value.likeCount--
        ElMessage.success('取消点赞成功')
        refreshPage()
      }
    } else {
      const res = await likePost(postId.value)
      if (res.code === 200) {
        hasLiked.value = true
        post.value.likeCount++
        ElMessage.success('点赞成功')
        refreshPage()
      }
    }
  } catch (error) {
    console.error('点赞操作失败：', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    likeLoading.value = false
  }
}

// 切换收藏
const toggleFavorite = async () => {
  favoriteLoading.value = true
  try {
    if (hasFavorited.value) {
      const res = await unfavoritePost(postId.value)
      if (res.code === 200) {
        hasFavorited.value = false
        ElMessage.success('取消收藏成功')
        refreshPage()
      }
    } else {
      const res = await favoritePost(postId.value)
      if (res.code === 200) {
        hasFavorited.value = true
        ElMessage.success('收藏成功')
        refreshPage()
      }
    }
  } catch (error) {
    console.error('收藏操作失败：', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

// 显示举报对话框
const showReportDialog = () => {
  reportForm.value.reason = ''
  reportDialogVisible.value = true
}

// 提交举报
const submitReport = async () => {
  if (!reportForm.value.reason.trim()) {
    ElMessage.warning('请填写举报原因')
    return
  }

  reportSubmitting.value = true
  try {
    const data = {
      reportType: 1, // 1-帖子
      targetId: postId.value,
      reason: reportForm.value.reason
    }
    
    const res = await submitReportApi(data)
    if (res.code === 200) {
      ElMessage.success('举报成功，我们会尽快处理')
      reportDialogVisible.value = false
      refreshPage()
    }
  } catch (error) {
    console.error('举报失败：', error)
    ElMessage.error(error.message || '举报失败')
  } finally {
    reportSubmitting.value = false
  }
}

const fetchReplies = async () => {
  replyLoading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const res = await getReplyListByPost(postId.value, params)
    if (res.code === 200 && res.data) {
      replies.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取回复列表失败：', error)
    ElMessage.error('获取回复列表失败')
  } finally {
    replyLoading.value = false
  }
}

const handleSubmitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }

  replyLoading.value = true
  try {
    // 全局输入框只用于一级回复（parentId为null）
    const data = {
      postId: postId.value,
      content: replyContent.value,
      parentId: null,  // 一级回复
      replyToUserId: null
    }

    const hasAIMention = replyContent.value.includes('@AI助手') || replyContent.value.includes('@AI')
    const res = await createReply(data)
    if (res.code === 200) {
      replyContent.value = ''
      pageNum.value = 1

      if (hasAIMention) {
        ElMessage.success('回复成功！AI助手正在回复中，请稍候...')
        await fetchReplies()
        stopAIReplyPolling()
        startAIReplyPolling()
      } else {
        ElMessage.success('回复成功')
        await fetchReplies()
      }
    }
  } catch (error) {
    console.error('回复失败：', error)
    ElMessage.error(error.message || '回复失败')
  } finally {
    replyLoading.value = false
  }
}

// 处理二级回复提交
const handleSubmitChildReply = async (replyData, callback) => {
  try {
    const data = {
      postId: postId.value,
      content: replyData.content,
      parentId: replyData.parentId,  // 一级回复的ID
      replyToUserId: replyData.replyToUserId
    }

    const hasAIMention = replyData.content.includes('@AI助手') || replyData.content.includes('@AI')
    const res = await createReply(data)
    if (res.code === 200) {
      // 关闭输入框
      activeReplyId.value = null

      // 调用回调函数（重置子组件状态）
      if (callback) callback()

      if (hasAIMention) {
        ElMessage.success('回复成功！AI助手正在回复中，请稍候...')
        await fetchReplies()
        stopAIReplyPolling()
        startAIReplyPolling()
      } else {
        ElMessage.success('回复成功')
        await fetchReplies()
      }
    }
  } catch (error) {
    console.error('回复失败：', error)
    ElMessage.error(error.message || '回复失败')
    // 调用回调函数以重置loading状态
    if (callback) callback()
  }
}

// @AI助手 快捷输入
const mentionAI = () => {
  const mention = '@AI助手 '
  const cursorPosition = replyInputRef.value?.textarea?.selectionStart || 0
  const textBefore = replyContent.value.substring(0, cursorPosition)
  const textAfter = replyContent.value.substring(cursorPosition)
  
  replyContent.value = textBefore + mention + textAfter
  
  // 聚焦输入框并移动光标到@AI助手后面
  setTimeout(() => {
    if (replyInputRef.value?.textarea) {
      replyInputRef.value.textarea.focus()
      const newPosition = cursorPosition + mention.length
      replyInputRef.value.textarea.setSelectionRange(newPosition, newPosition)
    }
  }, 0)
}

const handleReplyTo = (replyId) => {
  // replyId可能是一级回复的ID，或者是null（取消回复）
  activeReplyId.value = replyId
}

// 点击外部区域关闭二级输入框
const handleClickOutside = () => {
  if (activeReplyId.value !== null) {
    activeReplyId.value = null
  }
}

const handleDeleteReply = (replyId) => {
  ElMessageBox.confirm('确定要删除这条回复吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteReply(replyId)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        refreshPage()
      }
    } catch (error) {
      console.error('删除回复失败：', error)
      ElMessage.error('删除回复失败')
    }
  }).catch(() => {})
}

const handleEdit = () => {
  router.push(`/post/${postId.value}/edit`)
}

const goToUserProfile = (userId) => {
  if (!Number.isFinite(Number(userId))) return
  router.push(`/user/${Number(userId)}`)
}

const handleDelete = () => {
  ElMessageBox.confirm('确定要删除这篇帖子吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deletePost(postId.value)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        await router.push('/')
        refreshPage()
      }
    } catch (error) {
      console.error('删除帖子失败：', error)
      ElMessage.error('删除帖子失败')
    }
  }).catch(() => {})
}

const handlePageChange = (page) => {
  pageNum.value = page
  fetchReplies()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1
  fetchReplies()
}

const formatContent = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}
</script>

<style scoped>
.post-detail-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-card,
.reply-card {
  border-radius: 12px;
}

/* 帖子卡片顶部渐变色条 */
.post-card :deep(.el-card__body) {
  padding: 0;
}

.post-content {
  padding: 24px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.post-meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.post-title {
  font-size: 26px;
  font-weight: bold;
  color: #1a2a4a;
  margin-bottom: 20px;
  line-height: 1.5;
}

/* 作者信息区加浅海蓝渐变背景 */
.post-author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: linear-gradient(to right, #e6f4ff, #f0fbff);
  border-radius: 10px;
  margin-bottom: 24px;
  border-left: 4px solid #409eff;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.author-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a2a4a;
}

.clickable-avatar {
  cursor: pointer;
}

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

.post-body {
  font-size: 15px;
  line-height: 1.95;
  color: #2a3a50;
  margin-bottom: 24px;
  letter-spacing: 0.01em;
}

.post-image-list {
  margin-bottom: 24px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.post-image-item {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e6f4ff;
}

.post-image {
  width: 100%;
  height: 100%;
  cursor: zoom-in;
}

.post-stats {
  display: flex;
  gap: 20px;
  padding: 14px 16px;
  border-top: 1px solid #e6f4ff;
  background: #f7fbff;
  border-radius: 0 0 8px 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #7090aa;
  transition: color 0.2s;
}

.stat-item:hover {
  color: #409eff;
}

.post-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e6f4ff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a2a4a;
}

.reply-editor {
  margin-bottom: 24px;
}

/* 回复工具栏改为海洋风格 */
.reply-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding: 10px 14px;
  background: linear-gradient(135deg, #e6f4ff 0%, #f0fbff 100%);
  border-radius: 8px;
  border: 1px dashed #69c0ff;
}

.toolbar-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #409eff;
  flex: 1;
}

.reply-editor-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.login-tip {
  text-align: center;
  padding: 24px;
  color: #7090aa;
  font-size: 14px;
  background: linear-gradient(to right, #f0f8ff, #e6f4ff);
  border-radius: 8px;
  margin-bottom: 24px;
  border: 1px solid #bae7ff;
}

.login-link {
  color: #409eff;
  margin: 0 4px;
  font-weight: 500;
}

.ai-waiting-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #e6f4ff 0%, #f0fbff 100%);
  border: 1px dashed #69c0ff;
  border-radius: 8px;
  font-size: 13px;
  color: #409eff;
}

.ai-spin {
  animation: spin 1.2s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.reply-list {
  min-height: 200px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e6f4ff;
}

@media (max-width: 768px) {
  .post-title {
    font-size: 20px;
  }

  .post-body {
    font-size: 14px;
  }

  .post-content {
    padding: 16px;
  }
}
</style>

