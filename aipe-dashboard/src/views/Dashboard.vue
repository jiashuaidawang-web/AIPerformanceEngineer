<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card>
          <template #header>资源总数</template>
          <div class="metric">{{ overview.resourceCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>今日 Observation</template>
          <div class="metric">{{ overview.observationCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>活跃 Evidence</template>
          <div class="metric">{{ overview.evidenceCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <template #header>待执行推荐</template>
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
          <template #header>最近 24h Observation 趋势</template>
          <div ref="trendChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { resourceApi, observationApi } from '@/api'

const overview = reactive({
  resourceCount: 0,
  observationCount: 0,
  evidenceCount: 0,
  pendingRecommendations: 0,
})

const statusChart = ref()
const trendChart = ref()

async function loadOverview() {
  try {
    const res = await resourceApi.list({ limit: 1 })
    overview.resourceCount = res.data?.length || 0
  } catch (e) {
    console.error(e)
  }
}

function renderStatusChart() {
  const chart = echarts.init(statusChart.value)
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 10, name: 'RUNNING' },
        { value: 2, name: 'STOPPED' },
        { value: 1, name: 'MAINTENANCE' },
      ],
    }],
  })
}

function renderTrendChart() {
  const chart = echarts.init(trendChart.value)
  const hours = Array.from({ length: 24 }, (_, i) => `${i}:00`)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: hours },
    yAxis: { type: 'value' },
    series: [{
      type: 'line',
      smooth: true,
      data: hours.map(() => Math.floor(Math.random() * 1000)),
      areaStyle: {},
    }],
  })
}

onMounted(() => {
  loadOverview()
  renderStatusChart()
  renderTrendChart()
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
