<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon size="24"><Monitor /></el-icon>
        <span>AI Performance</span>
      </div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409EFF"
      >
        <el-menu-item
          v-for="item in menuItems"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>{{ route.meta.title || 'AI Performance Engineer' }}</span>
        <el-dropdown>
          <el-avatar :size="32" :icon="UserFilled" />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { UserFilled } from '@element-plus/icons-vue'

const route = useRoute()

const menuItems = computed(() => [
  { path: '/dashboard', title: '概览', icon: 'Odometer' },
  { path: '/resources', title: '资源', icon: 'Box' },
  { path: '/timeline', title: '时序', icon: 'TrendCharts' },
  { path: '/topology', title: '拓扑', icon: 'Share' },
  { path: '/evidence', title: '证据', icon: 'Search' },
  { path: '/knowledge', title: '知识库', icon: 'Collection' },
  { path: '/recommendations', title: '推荐', icon: 'Star' },
  { path: '/executions', title: '执行', icon: 'VideoPlay' },
])
</script>

<style scoped>
.layout { height: 100vh; }
.aside { background: #001529; }
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  gap: 8px;
  border-bottom: 1px solid #1f2d3d;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
}
:deep(.el-menu) { border-right: none; }
</style>
