<template>
  <footer class="app-footer">
    <div class="footer-container">
      <div class="footer-content">
        <p>&copy; 2026 灵犀论坛. All rights reserved.</p>
        <div class="footer-links">
          <a href="#" class="footer-link">关于我们</a>
          <span class="separator">|</span>
          <a href="#" class="footer-link">联系方式</a>
          <span class="separator">|</span>
          <a href="#" class="footer-link">用户协议</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.app-footer {
  background: #fff;
  border-top: 1px solid #e4e7ed;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
}

.footer-content {
  text-align: center;
  color: #909399;
  font-size: 14px;
}

.footer-content p {
  margin-bottom: 8px;
}

.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.footer-link {
  color: #909399;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #409eff;
}

.separator {
  color: #dcdfe6;
}
</style>

