<template>
  <div>
    <el-card v-loading="loading">
      <template #header>
        <div class="header">
          <span>Timeline 查询</span>
          <el-form :inline="true">
            <el-form-item label="资源">
              <el-select
                v-model="query.resource_id"
                filterable
                placeholder="选择资源"
                style="width: 260px"
                @change="onResourceChange"
              >
                <el-option
                  v-for="r in resources"
                  :key="r.resourceId"
                  :label="`${r.resourceName} (${r.resourceId})`"
                  :value="r.resourceId"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="指标">
              <el-select v-model="selectedMetrics" placeholder="选择指标 (可多选)" style="width: 240px" multiple collapse-tags>
                <el-option v-for="m in availableMetrics" :key="m" :label="m" :value="m" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="range"
                type="datetimerange"
                value-format="x"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadTimeline">查询</el-button>
              <el-button @click="exportData" :disabled="!timelineData || !timelineData.points.length">导出 CSV</el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>

      <el-row :gutter="20" v-if="timelineData">
        <el-col :span="16">
          <v-chart class="chart" :option="chartOption" autoresize />
        </el-col>
        <el-col :span="8">
          <el-card>
            <template #header>统计特征</template>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="最小值">{{ timelineData.stats?.min?.toFixed(2) }}</el-descriptions-item>
              <el-descriptions-item label="最大值">{{ timelineData.stats?.max?.toFixed(2) }}</el-descriptions-item>
              <el-descriptions-item label="平均值">{{ timelineData.stats?.avg?.toFixed(2) }}</el-descriptions-item>
              <el-descriptions-item label="标准差">{{ timelineData.stats?.stdDev?.toFixed(2) }}</el-descriptions-item>
              <el-descriptions-item label="样本数">{{ timelineData.stats?.count }}</el-descriptions-item>
              <el-descriptions-item label="数据点">{{ timelineData.pointCount }}</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="请选择资源并查询" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { timelineApi, resourceApi, type TimelineData, type Resource } from '@/api'

const query = reactive({
  resource_id: '',
  metric_name: '',
})

const range = ref<[number, number]>([Date.now() - 3600000, Date.now()])
const timelineData = ref<TimelineData>()
const multiTimelineData = ref<TimelineData[]>([])
const resources = ref<Resource[]>([])
const availableMetrics = ref<string[]>([])
const selectedMetrics = ref<string[]>([])
const loading = ref(false)
const isMultiMode = computed(() => selectedMetrics.value.length > 1)

const COLORS = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272']

const chartOption = computed(() => {
  if (isMultiMode.value) {
    // 多指标对比模式
    return {
      tooltip: { trigger: 'axis' },
      legend: { data: multiTimelineData.value.map(d => d.metricName) },
      xAxis: {
        type: 'category',
        data: multiTimelineData.value[0]?.points.map(p => new Date(p.timestamp).toLocaleTimeString()) || [],
      },
      yAxis: { type: 'value' },
      series: multiTimelineData.value.map((d, idx) => ({
        name: d.metricName,
        type: 'line',
        smooth: true,
        data: d.points.map(p => p.value),
        itemStyle: { color: COLORS[idx % COLORS.length] },
      })),
    }
  }
  // 单指标模式
  return {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: timelineData.value?.points.map(p => new Date(p.timestamp).toLocaleTimeString()) || [],
    },
    yAxis: { type: 'value' },
    series: [{
      data: timelineData.value?.points.map(p => p.value) || [],
      type: 'line',
      smooth: true,
      areaStyle: { color: 'rgba(64,158,255,0.2)' },
      markLine: {
        data: [
          { type: 'average', name: '平均值' },
          { yAxis: 80, name: '阈值', lineStyle: { color: 'red' } },
        ],
      },
    }],
  }
})

async function loadResources() {
  try {
    const res = await resourceApi.list()
    resources.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function onResourceChange(resourceId: string) {
  query.resource_id = resourceId
  query.metric_name = ''
  availableMetrics.value = []
  timelineData.value = undefined

  if (!resourceId) return

  try {
    const res = await timelineApi.availableMetrics(resourceId, Number(range.value[0]), Number(range.value[1]))
    availableMetrics.value = res.data || []
    if (availableMetrics.value.length > 0) {
      query.metric_name = availableMetrics.value[0]
    }
  } catch (e) {
    console.error(e)
  }
}

async function loadTimeline() {
  if (!query.resource_id || !selectedMetrics.value.length) return
  loading.value = true
  try {
    if (selectedMetrics.value.length === 1) {
      // 单指标模式
      const res = await timelineApi.query({
        resource_id: query.resource_id,
        metric_name: selectedMetrics.value[0],
        start_time: Number(range.value[0]),
        end_time: Number(range.value[1]),
      })
      timelineData.value = res.data
      multiTimelineData.value = []
    } else {
      // 多指标对比模式
      const res = await timelineApi.batch({
        resource_id: query.resource_id,
        metric_names: selectedMetrics.value,
        start_time: Number(range.value[0]),
        end_time: Number(range.value[1]),
      })
      multiTimelineData.value = res.data || []
      timelineData.value = undefined
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function exportData() {
  if (!timelineData.value || !timelineData.value.points.length) return

  const headers = ['Timestamp', 'Value', 'Unit']
  const rows = timelineData.value.points.map(p => [
    new Date(p.timestamp).toLocaleString(),
    p.value,
    p.unit || ''
  ])

  const csvContent = [headers.join(','), ...rows.map(r => r.join(','))].join('\n')
  const blob = new Blob(['﻿' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `${query.resource_id}_${query.metric_name}_${Date.now()}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
}

onMounted(async () => {
  await loadResources()
})
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px; }
.chart { height: 400px; }
</style>
