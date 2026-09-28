import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    meta: { requiresAuth: true },
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'plans',
        name: 'Plans',
        component: () => import('@/views/PlanManagement.vue'),
        meta: { title: '招聘计划' }
      },
      {
        path: 'candidates',
        name: 'Candidates',
        component: () => import('@/views/CandidateManagement.vue'),
        meta: { title: '候选人管理' }
      },
      {
        path: 'approvals',
        name: 'Approvals',
        component: () => import('@/views/ApprovalManagement.vue'),
        meta: { title: '审批管理' }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('@/views/Announcement.vue'),
        meta: { title: '系统公告' }
      },
      {
        path: 'system-logs',
        name: 'SystemLogs',
        component: () => import('@/views/SystemLog.vue'),
        meta: { title: '系统日志' }
      },
      {
        path: 'file-versions',
        name: 'FileVersions',
        component: () => import('@/views/FileVersion.vue'),
        meta: { title: '文件版本' }
      },
      {
        path: 'feishu-test',
        name: 'FeishuTest',
        component: () => import('@/views/WeChatTest.vue'),
        meta: { title: '飞书消息测试' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router
