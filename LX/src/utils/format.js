/**
 * 格式化工具函数
 */

/**
 * 格式化时间
 * @param {string} dateStr - 日期字符串
 * @returns {string} 格式化后的时间
 */
export function formatTime(dateStr) {
  if (!dateStr) return ''
  
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < week) {
    return Math.floor(diff / day) + '天前'
  } else {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    
    if (year === now.getFullYear()) {
      return `${month}-${day} ${hours}:${minutes}`
    }
    return `${year}-${month}-${day}`
  }
}

/**
 * 格式化数字（大数字显示为 1k, 1w 等）
 * @param {number} num - 数字
 * @returns {string} 格式化后的数字
 */
export function formatNumber(num) {
  if (num < 1000) {
    return String(num)
  } else if (num < 10000) {
    return (num / 1000).toFixed(1) + 'k'
  } else {
    return (num / 10000).toFixed(1) + 'w'
  }
}

/**
 * 截取文本
 * @param {string} text - 文本
 * @param {number} length - 最大长度
 * @returns {string} 截取后的文本
 */
export function truncateText(text, length = 100) {
  if (!text) return ''
  if (text.length <= length) return text
  return text.substring(0, length) + '...'
}

