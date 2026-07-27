<template>
  <div>
    <el-page-header @back="$router.back()" :content="resource?.resourceName || '资源详情'" />

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card>
          <template #header>基本信息</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="ID">{{ resource?.resourceId }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ resource?.resourceType }}</el-descriptions-item>
            <el-descriptions-item label="业务系统">{{ resource?.businessSystem }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="resource?.status === 'RUNNING' ? 'success' : 'danger'">
                {{ resource?.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="版本">{{ resource?.version }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <template #header>CPU 使用率趋势 (最近 1h)</template>
          <v-chart class="chart" :option="timelineOption" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import VChart from 'vue-echarts'
import { resourceApi, timelineApi, type Resource } from '@/api'

const route = useRoute()
const resource = ref<Resource>()
const points = ref<Array<{ timestamp: number; value: number }>>([])

const timelineOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: points.value.map(p => new Date(p.timestamp).toLocaleTimeString()),
  },
  yAxis: { type: 'value', max: 100 },
  series: [{
    data: points.value.map(p => p.value),
    type: 'line',
    smooth: true,
    areaStyle: { color: 'rgba(64,158,255,0.2)' },
  }],
}))

async function loadData() {
  const id = route.params.id as string
  try {
    const res = await resourceApi.get(id)
    resource.value = res.data
  } catch (e) {
    console.error(e)
  }
  try {
    const end = Date.now()
    const start = end - 3600000
    const tl = await timelineApi.query({
      resource_id: id,
      metric_name: 'cpu.usage',
      start_time: start,
      end_time: end,
    })
    points.value = tl.data?.points || []
  } catch (e) {
    console.error(e)
  }
}

onMounted(loadData)
</script>

<style scoped>
.chart { height: 350px; }
</style>
