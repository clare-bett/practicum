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
            <el-avatar :size="48" :src="post.authorAvatar">
              {{ post.authorName?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="author-details">
              <div class="author-name">{{ post.authorName }}</div>
              <div class="post-time">{{ formatTime(post.createTime) }}</div>
            </div>
          </div>

          <div class="post-body" v-html="formatContent(post.content)"></div>

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
import { ref, computed, onMounted, watch } from 'vue'
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
  Warning
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const currentUserId = computed(() => userStore.userId)

const postId = computed(() => Number(route.params.id))
const post = ref(null)
const postLoading = ref(false)

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

onMounted(() => {
  fetchPost()
  fetchReplies()
  if (isLoggedIn.value) {
    checkInteractionStatus()
  }
})

watch(() => route.params.id, () => {
  if (route.name === 'postDetail') {
    fetchPost()
    pageNum.value = 1
    fetchReplies()
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
      }
    } else {
      const res = await likePost(postId.value)
      if (res.code === 200) {
        hasLiked.value = true
        post.value.likeCount++
        ElMessage.success('点赞成功')
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
      }
    } else {
      const res = await favoritePost(postId.value)
      if (res.code === 200) {
        hasFavorited.value = true
        ElMessage.success('收藏成功')
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

    const res = await createReply(data)
    if (res.code === 200) {
      // 检查是否@了AI助手
      const hasAIMention = replyContent.value.includes('@AI助手') || replyContent.value.includes('@AI')
      
      if (hasAIMention) {
        ElMessage.success('回复成功！AI助手正在准备回复中...')
      } else {
        ElMessage.success('回复成功')
      }
      
      replyContent.value = ''
      
      // 刷新回复列表
      pageNum.value = 1
      fetchReplies()
      fetchPost() // 刷新帖子信息（更新回复数）
      
      // 如果@了AI，延迟5秒后再次刷新以显示AI回复
      if (hasAIMention) {
        setTimeout(() => {
          fetchReplies()
          ElMessage.info('AI助手已回复')
        }, 5000)
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

    const res = await createReply(data)
    if (res.code === 200) {
      // 检查是否@了AI助手
      const hasAIMention = replyData.content.includes('@AI助手') || replyData.content.includes('@AI')
      
      if (hasAIMention) {
        ElMessage.success('回复成功！AI助手正在准备回复中...')
      } else {
        ElMessage.success('回复成功')
      }
      
      // 关闭输入框
      activeReplyId.value = null
      
      // 刷新回复列表
      fetchReplies()
      fetchPost()
      
      // 调用回调函数（重置子组件状态）
      if (callback) callback()
      
      // 如果@了AI，延迟5秒后再次刷新
      if (hasAIMention) {
        setTimeout(() => {
          fetchReplies()
          ElMessage.info('AI助手已回复')
        }, 5000)
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
        fetchReplies()
        fetchPost()
      }
    } catch (error) {
      console.error('删除回复失败：', error)
      ElMessage.error('删除回复失败')
    }
  }).catch(() => {})
}

const handleEdit = () => {
  // TODO: 实现编辑功能
  ElMessage.info('编辑功能待实现')
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
        router.push('/')
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
  border-radius: 8px;
}

.post-content {
  padding: 8px;
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
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
  line-height: 1.4;
}

.post-author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.post-time {
  font-size: 13px;
  color: #909399;
}

.post-body {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  margin-bottom: 24px;
}

.post-stats {
  display: flex;
  gap: 24px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.post-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.reply-editor {
  margin-bottom: 24px;
}

.reply-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding: 12px;
  background: linear-gradient(135deg, #f0f7ff 0%, #f5f3ff 100%);
  border-radius: 8px;
  border: 1px dashed #667eea;
}

.toolbar-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #667eea;
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
  color: #606266;
  font-size: 14px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 24px;
}

.login-link {
  color: #409eff;
  margin: 0 4px;
}

.reply-list {
  min-height: 200px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e4e7ed;
}

@media (max-width: 768px) {
  .post-title {
    font-size: 22px;
  }

  .post-body {
    font-size: 14px;
  }
}
</style>

