<template>
  <div>
    <el-card>
      <template #header>
        <div class="header">
          <span>Timeline 查询</span>
          <el-form :inline="true">
            <el-form-item label="资源">
              <el-input v-model="query.resource_id" placeholder="resource_id" />
            </el-form-item>
            <el-form-item label="指标">
              <el-input v-model="query.metric_name" placeholder="cpu.usage" />
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
      <el-empty v-else description="请输入查询条件" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import VChart from 'vue-echarts'
import { timelineApi, type TimelineData } from '@/api'

const query = reactive({
  resource_id: '',
  metric_name: 'cpu.usage',
})

const range = ref<[number, number]>([Date.now() - 3600000, Date.now()])
const timelineData = ref<TimelineData>()

const chartOption = computed(() => ({
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
}))

async function loadTimeline() {
  if (!query.resource_id) return
  try {
    const res = await timelineApi.query({
      ...query,
      start_time: range.value[0],
      end_time: range.value[1],
    })
    timelineData.value = res.data
  } catch (e) {
    console.error(e)
  }
}
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; }
.chart { height: 400px; }
</style>
