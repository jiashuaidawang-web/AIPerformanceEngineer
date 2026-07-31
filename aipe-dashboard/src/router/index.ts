import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '概览', icon: 'Odometer' },
      },
      {
        path: 'resources',
        name: 'Resources',
        component: () => import('@/views/ResourceList.vue'),
        meta: { title: '资源', icon: 'Box' },
      },
      {
        path: 'resources/:id',
        name: 'ResourceDetail',
        component: () => import('@/views/ResourceDetail.vue'),
        meta: { title: '资源详情', hidden: true },
      },
      {
        path: 'timeline',
        name: 'Timeline',
        component: () => import('@/views/TimelineView.vue'),
        meta: { title: '时序', icon: 'TrendCharts' },
      },
      {
        path: 'topology',
        name: 'Topology',
        component: () => import('@/views/TopologyView.vue'),
        meta: { title: '拓扑', icon: 'Share' },
      },
      {
        path: 'evidence',
        name: 'Evidence',
        component: () => import('@/views/EvidenceList.vue'),
        meta: { title: '证据', icon: 'Search' },
      },
      {
        path: 'knowledge',
        name: 'Knowledge',
        component: () => import('@/views/KnowledgeBase.vue'),
        meta: { title: '知识库', icon: 'Collection' },
      },
      {
        path: 'recommendations',
        name: 'Recommendations',
        component: () => import('@/views/RecommendationList.vue'),
        meta: { title: '推荐', icon: 'Star' },
      },
      {
        path: 'executions',
        name: 'Executions',
        component: () => import('@/views/ExecutionList.vue'),
        meta: { title: '执行', icon: 'VideoPlay' },
      },
      {
        path: 'alerts',
        name: 'AlertRules',
        component: () => import('@/views/AlertRules.vue'),
        meta: { title: '告警规则', icon: 'Bell' },
      },
      {
        path: 'alerts/records',
        name: 'AlertRecords',
        component: () => import('@/views/AlertRecords.vue'),
        meta: { title: '告警记录', hidden: true },
      },
      {
        path: 'agents',
        name: 'AgentManagement',
        component: () => import('@/views/AgentManagement.vue'),
        meta: { title: 'Agent 管理', icon: 'Monitor' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
