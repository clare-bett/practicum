import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/Home.vue'),
      meta: { title: '首页' }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue'),
      meta: { title: '注册' }
    },
    {
      path: '/category/:id',
      name: 'category',
      component: () => import('@/views/Category.vue'),
      meta: { title: '板块' }
    },
    {
      path: '/post/:id',
      name: 'postDetail',
      component: () => import('@/views/PostDetail.vue'),
      meta: { title: '帖子详情' }
    },
    {
      path: '/post/create',
      name: 'createPost',
      component: () => import('@/views/CreatePost.vue'),
      meta: { title: '发帖', requiresAuth: true }
    },
    {
      path: '/post/:id/edit',
      name: 'editPost',
      component: () => import('@/views/EditPost.vue'),
      meta: { title: '编辑帖子', requiresAuth: true }
    },
    {
      path: '/user/:id',
      name: 'userProfile',
      component: () => import('@/views/UserProfile.vue'),
      meta: { title: '个人主页' }
    },
    {
      path: '/profile',
      name: 'myProfile',
      component: () => import('@/views/MyProfile.vue'),
      meta: { title: '个人中心', requiresAuth: true }
    },
    {
      path: '/admin',
      name: 'admin',
      redirect: '/admin/posts',
      component: () => import('@/views/admin/AdminLayout.vue'),
      meta: { title: '管理后台', requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'posts',
        name: 'adminPosts',
        component: () => import('@/views/admin/PostManagement.vue'),
        meta: { title: '帖子管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'categories',
        name: 'adminCategories',
        component: () => import('@/views/admin/CategoryManagement.vue'),
        meta: { title: '板块管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'users',
        name: 'adminUsers',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'reports',
        name: 'adminReports',
        component: () => import('@/views/admin/ReportManagement.vue'),
        meta: { title: '举报管理', requiresAuth: true, requiresAdmin: true }
      }
    ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'notFound',
      component: () => import('@/views/NotFound.vue'),
      meta: { title: '404' }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 灵犀论坛` : '灵犀论坛'

  // 检查是否需要登录
  if (to.meta.requiresAuth && !isLoggedIn()) {
    ElMessage.warning('请先登录')
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
    return
  }

  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin) {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      const user = JSON.parse(userInfo)
      if (user.role !== 'ADMIN') {
        ElMessage.error('需要管理员权限')
        next('/')
        return
      }
    } else {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }
  }

  next()
})

export default router
