<template>
  <div v-loading="loading">
    <el-page-header @back="$router.back()" :content="resource?.resourceName || '资源详情'" />

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card>
          <template #header>基本信息</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="ID">{{ resource?.resourceId }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ resource?.resourceType }}</el-descriptions-item>
            <el-descriptions-item label="业务系统">{{ resource?.businessSystem }}</el-descriptions-item>
            <el-descriptions-item label="环境">{{ resource?.environment }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="resource?.status === 'RUNNING' ? 'success' : 'danger'">
                {{ resource?.status }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="版本">{{ resource?.version }}</el-descriptions-item>
          </el-descriptions>

          <template v-if="resource?.attributes && Object.keys(resource.attributes).length">
            <el-divider content-position="left">连接配置</el-divider>
            <el-descriptions :column="1" border size="small">
              <el-descriptions-item
                v-for="(val, key) in resource.attributes"
                :key="String(key)"
                :label="String(key)"
              >
                {{ key === 'password' ? '******' : val }}
              </el-descriptions-item>
            </el-descriptions>
          </template>
        </el-card>

        <el-card v-if="stats" style="margin-top: 20px">
          <template #header>统计特征</template>
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="最小值">{{ stats.min?.toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="最大值">{{ stats.max?.toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="平均值">{{ stats.avg?.toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="标准差">{{ stats.stdDev?.toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="样本数">{{ stats.count }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="header">
              <span>指标趋势</span>
              <el-select v-model="metricName" style="width: 220px" @change="loadTimeline" :disabled="!availableMetrics.length">
                <el-option v-for="m in availableMetrics" :key="m" :label="m" :value="m" />
              </el-select>
            </div>
          </template>
          <v-chart v-if="points.length" class="chart" :option="timelineOption" autoresize />
          <el-empty v-else description="暂无 Timeline 数据，请先确认 Agent 已采集" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { resourceApi, timelineApi, type Resource, type TimelineStats } from '@/api'
import { defaultMetricForType } from '@/config/resourceFormFields'

const route = useRoute()
const resource = ref<Resource>()
const points = ref<Array<{ timestamp: number; value: number }>>([])
const stats = ref<TimelineStats | null>(null)
const loading = ref(false)
const metricName = ref('')
const availableMetrics = ref<string[]>([])

const timelineOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: points.value.map(p => new Date(p.timestamp).toLocaleTimeString()),
  },
  yAxis: { type: 'value' },
  series: [{
    data: points.value.map(p => p.value),
    type: 'line',
    smooth: true,
    areaStyle: { color: 'rgba(64,158,255,0.2)' },
    markLine: stats.value ? {
      data: [{ type: 'average', name: '平均值' }],
    } : undefined,
  }],
}))

async function loadResource() {
  const id = route.params.id as string
  const res = await resourceApi.get(id)
  resource.value = res.data
  metricName.value = defaultMetricForType(resource.value?.resourceType)
}

async function loadAvailableMetrics() {
  const id = route.params.id as string
  try {
    const res = await timelineApi.availableMetrics(id)
    availableMetrics.value = res.data || []
    if (availableMetrics.value.length > 0) {
      metricName.value = availableMetrics.value[0]
      await loadTimeline()
    }
  } catch (e) {
    console.error(e)
  }
}

async function loadTimeline() {
  if (!metricName.value) return
  const id = route.params.id as string
  const end = Date.now()
  const start = end - 3600000
  try {
    const tl = await timelineApi.query({
      resource_id: id,
      metric_name: metricName.value,
      start_time: start,
      end_time: end,
    })
    points.value = tl.data?.points || []
    stats.value = tl.data?.stats || null
  } catch (e) {
    console.error(e)
    points.value = []
    stats.value = null
  }
}

async function loadData() {
  loading.value = true
  try {
    await loadResource()
    await loadAvailableMetrics()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.chart { height: 400px; }
.header { display: flex; justify-content: space-between; align-items: center; }
</style>
