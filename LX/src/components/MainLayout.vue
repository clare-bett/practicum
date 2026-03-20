<template>
  <div class="main-layout">
    <AppHeader />
    <main class="main-content">
      <slot></slot>
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from './AppHeader.vue'
import AppFooter from './AppFooter.vue'
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 24px 20px;
}

@media (max-width: 768px) {
  .main-content {
    padding: 16px 12px;
  }
}
</style>

