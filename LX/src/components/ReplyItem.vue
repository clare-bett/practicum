<template>
  <div class="reply-wrapper" @click.stop>
    <!-- 一级回复 -->
    <div class="reply-item" :class="{ 'ai-reply': reply.isAi === 1 }">
      <div class="reply-avatar">
        <el-avatar :size="40" :src="reply.userAvatar">
          {{ reply.username?.charAt(0) || 'U' }}
        </el-avatar>
      </div>

      <div class="reply-content">
        <div class="reply-header">
          <span class="reply-username">{{ reply.username }}</span>
          <!-- AI标识 -->
          <el-tag 
            v-if="reply.isAi === 1" 
            type="info" 
            effect="dark"
            size="small"
            class="ai-badge"
          >
            <el-icon><MagicStick /></el-icon>
            AI助手
          </el-tag>
          <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
        </div>

        <div class="reply-text">
          {{ reply.content }}
        </div>

        <div class="reply-actions">
          <el-button 
            text 
            size="small" 
            @click="handleReply"
            v-if="isLoggedIn"
          >
            <el-icon><ChatDotRound /></el-icon>
            回复
          </el-button>
          <el-button 
            text 
            size="small" 
            type="danger"
            @click="handleDelete"
            v-if="canDelete"
          >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 二级回复列表 -->
    <div v-if="reply.children && reply.children.length > 0" class="children-replies">
      <div 
        v-for="child in reply.children" 
        :key="child.id" 
        class="child-reply-item"
        :class="{ 'ai-reply': child.isAi === 1 }"
      >
        <div class="reply-avatar">
          <el-avatar :size="32" :src="child.userAvatar">
            {{ child.username?.charAt(0) || 'U' }}
          </el-avatar>
        </div>

        <div class="reply-content">
          <div class="reply-header">
            <span class="reply-username">{{ child.username }}</span>
            <!-- AI标识 -->
            <el-tag 
              v-if="child.isAi === 1" 
              type="info" 
              effect="dark"
              size="small"
              class="ai-badge"
            >
              <el-icon><MagicStick /></el-icon>
              AI助手
            </el-tag>
            <span class="reply-time">{{ formatTime(child.createTime) }}</span>
          </div>

          <div class="reply-text">
            <span v-if="child.replyToUsername" class="reply-to">
              回复 @{{ child.replyToUsername }}:
            </span>
            {{ child.content }}
          </div>

          <div class="reply-actions">
            <el-button 
              text 
              size="small" 
              @click="handleChildReply(child)"
              v-if="isLoggedIn"
            >
              <el-icon><ChatDotRound /></el-icon>
              回复
            </el-button>
            <el-button 
              text 
              size="small" 
              type="danger"
              @click="handleChildDelete(child.id)"
              v-if="canDeleteChild(child)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 二级回复输入框（当此一级回复被激活时显示） -->
    <div v-if="showReplyInput && isLoggedIn" class="child-reply-editor">
      <div class="reply-input-header">
        <span class="reply-target">回复 @{{ replyTargetUsername }}</span>
        <el-button text size="small" @click="handleCancelReply">
          <el-icon><Close /></el-icon>
          取消
        </el-button>
      </div>
      <el-input
        ref="childReplyInputRef"
        v-model="childReplyContent"
        type="textarea"
        :rows="3"
        placeholder="写下你的回复..."
        maxlength="500"
        show-word-limit
      />
      <div class="reply-input-actions">
        <el-button 
          type="primary" 
          size="small"
          :loading="submitting"
          @click="handleSubmitChildReply"
        >
          发布回复
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { formatTime } from '@/utils/format'
import { ChatDotRound, Delete, MagicStick, Close } from '@element-plus/icons-vue'

const props = defineProps({
  reply: {
    type: Object,
    required: true
  },
  activeReplyId: {
    type: Number,
    default: null
  }
})

const emit = defineEmits(['reply', 'delete', 'submitChildReply'])

const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const currentUserId = computed(() => userStore.userId)

// 二级回复输入框相关
const childReplyContent = ref('')
const childReplyInputRef = ref(null)
const replyTargetUsername = ref('')
const replyToUserId = ref(null)
const submitting = ref(false)

// 是否显示二级回复输入框
const showReplyInput = computed(() => {
  return props.activeReplyId === props.reply.id
})

const canDelete = computed(() => {
  return isLoggedIn.value && (
    currentUserId.value === props.reply.userId ||
    userStore.isAdmin
  )
})

const canDeleteChild = (child) => {
  return isLoggedIn.value && (
    currentUserId.value === child.userId ||
    userStore.isAdmin
  )
}

const handleReply = () => {
  // 回复一级回复，显示二级回复输入框
  replyTargetUsername.value = props.reply.username
  replyToUserId.value = props.reply.userId
  emit('reply', props.reply.id)
  
  // 等待DOM更新后聚焦输入框
  nextTick(() => {
    if (childReplyInputRef.value?.textarea) {
      childReplyInputRef.value.textarea.focus()
    }
  })
}

const handleDelete = () => {
  emit('delete', props.reply.id)
}

const handleChildReply = (child) => {
  // 回复二级回复，显示二级回复输入框
  replyTargetUsername.value = child.username
  replyToUserId.value = child.userId
  emit('reply', props.reply.id)
  
  // 等待DOM更新后聚焦输入框
  nextTick(() => {
    if (childReplyInputRef.value?.textarea) {
      childReplyInputRef.value.textarea.focus()
    }
  })
}

const handleChildDelete = (childId) => {
  emit('delete', childId)
}

const handleCancelReply = () => {
  childReplyContent.value = ''
  replyTargetUsername.value = ''
  replyToUserId.value = null
  emit('reply', null) // 取消激活状态
}

const handleSubmitChildReply = () => {
  if (!childReplyContent.value.trim()) {
    return
  }
  
  submitting.value = true
  
  // 提交二级回复
  emit('submitChildReply', {
    parentId: props.reply.id,
    replyToUserId: replyToUserId.value,
    content: childReplyContent.value
  }, () => {
    // 成功后的回调
    childReplyContent.value = ''
    replyTargetUsername.value = ''
    replyToUserId.value = null
    submitting.value = false
  })
}
</script>

<style scoped>
.reply-wrapper {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 0;
}

.reply-wrapper:last-child {
  border-bottom: none;
}

.reply-item {
  display: flex;
  gap: 12px;
  transition: all 0.3s;
}

.reply-item.ai-reply {
  background: linear-gradient(135deg, #f0f7ff 0%, #f5f3ff 100%);
  border-left: 3px solid #667eea;
  padding: 12px;
  margin-left: -12px;
  border-radius: 8px;
}

/* 二级回复容器 */
.children-replies {
  margin-top: 12px;
  margin-left: 52px; /* 对齐头像 */
  padding-left: 16px;
  border-left: 2px solid #e4e7ed;
}

/* 二级回复项 */
.child-reply-item {
  display: flex;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
  transition: all 0.3s;
}

.child-reply-item:last-child {
  border-bottom: none;
}

.child-reply-item.ai-reply {
  background: linear-gradient(135deg, #f0f7ff 0%, #f5f3ff 100%);
  border-left: 3px solid #667eea;
  padding: 8px 12px;
  margin-left: -16px;
  border-radius: 6px;
}

.child-reply-item .reply-avatar {
  flex-shrink: 0;
}

.reply-avatar {
  flex-shrink: 0;
}

/* 二级回复输入框 */
.child-reply-editor {
  margin-top: 12px;
  margin-left: 52px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.reply-input-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-target {
  font-size: 13px;
  color: #409eff;
  font-weight: 500;
}

.reply-input-actions {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.reply-username {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.reply-time {
  font-size: 13px;
  color: #909399;
}

.ai-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
}

.reply-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 8px;
}

.reply-to {
  color: #409eff;
  margin-right: 4px;
}

.reply-actions {
  display: flex;
  gap: 8px;
}
</style>

