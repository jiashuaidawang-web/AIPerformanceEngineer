<template>
  <div class="dashboard" v-loading="loading">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <template #header>资源总数</template>
          <div class="metric">{{ overview.resourceCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>Evidence 总数</template>
          <div class="metric">{{ overview.evidenceCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>Knowledge 条目</template>
          <div class="metric">{{ overview.knowledgeCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>待处理推荐</template>
          <div class="metric">{{ overview.pendingRecommendations }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>资源状态分布</template>
          <div ref="statusChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>资源类型分布</template>
          <div ref="typeChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  resourceApi,
  evidenceApi,
  knowledgeApi,
  recommendationApi,
  type Resource,
} from '@/api'

const loading = ref(false)
const overview = ref({
  resourceCount: 0,
  evidenceCount: 0,
  knowledgeCount: 0,
  pendingRecommendations: 0,
})

const statusChart = ref<HTMLElement>()
const typeChart = ref<HTMLElement>()
let statusInstance: echarts.ECharts | null = null
let typeInstance: echarts.ECharts | null = null

function countByField(resources: Resource[], field: 'status' | 'resourceType') {
  const map = new Map<string, number>()
  resources.forEach(r => {
    const key = (r[field] as string) || 'UNKNOWN'
    map.set(key, (map.get(key) || 0) + 1)
  })
  return Array.from(map.entries()).map(([name, value]) => ({ name, value }))
}

function renderPie(el: HTMLElement | undefined, data: Array<{ name: string; value: number }>) {
  if (!el) return null
  const chart = echarts.init(el)
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: data.length ? data : [{ name: '暂无数据', value: 1, itemStyle: { color: '#dcdfe6' } }],
    }],
  })
  return chart
}

async function loadOverview() {
  loading.value = true
  try {
    const [resourcesRes, evidenceRes, knowledgeRes, pendingRes] = await Promise.all([
      resourceApi.list(),
      evidenceApi.list().catch(() => ({ data: [] })),
      knowledgeApi.list().catch(() => ({ data: [] })),
      recommendationApi.list({ status: 'PENDING' }).catch(() => ({ data: [] })),
    ])

    const resources: Resource[] = resourcesRes.data || []
    overview.value.resourceCount = resources.length
    overview.value.evidenceCount = (evidenceRes.data || []).length
    overview.value.knowledgeCount = (knowledgeRes.data || []).length
    overview.value.pendingRecommendations = (pendingRes.data || []).length

    statusInstance?.dispose()
    typeInstance?.dispose()
    statusInstance = renderPie(statusChart.value, countByField(resources, 'status'))
    typeInstance = renderPie(typeChart.value, countByField(resources, 'resourceType'))
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleResize() {
  statusInstance?.resize()
  typeInstance?.resize()
}

onMounted(() => {
  loadOverview()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  statusInstance?.dispose()
  typeInstance?.dispose()
})
</script>

<style scoped>
.metric {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  padding: 20px 0;
}
</style>
