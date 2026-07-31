<template>
  <div v-loading="loading">
    <el-card>
      <template #header>
        <div class="header">
          <span>服务拓扑</span>
          <div>
            <el-button size="small" type="primary" @click="openCreateDialog">创建关系</el-button>
            <span class="meta" v-if="edgeCount">节点 {{ nodeCount }} · 关系 {{ edgeCount }}</span>
          </div>
        </div>
      </template>
      <div v-if="hasData" ref="topologyChart" style="height: 600px"></div>
      <el-empty v-else description="暂无拓扑关系，请先在 Relationship 中建立资源关联" />
    </el-card>
  </div>

  <!-- 创建关系对话框 -->
  <el-dialog v-model="dialogVisible" title="创建关系" width="500px">
    <el-form :model="form" label-width="100px">
      <el-form-item label="源资源" required>
        <el-select v-model="form.sourceResourceId" filterable placeholder="选择源资源" style="width: 100%">
          <el-option v-for="r in resources" :key="r.resourceId" :label="`${r.resourceName} (${r.resourceId})`" :value="r.resourceId" />
        </el-select>
      </el-form-item>
      <el-form-item label="目标资源" required>
        <el-select v-model="form.targetResourceId" filterable placeholder="选择目标资源" style="width: 100%">
          <el-option v-for="r in resources" :key="r.resourceId" :label="`${r.resourceName} (${r.resourceId})`" :value="r.resourceId" />
        </el-select>
      </el-form-item>
      <el-form-item label="关系类型" required>
        <el-select v-model="form.relationshipType" style="width: 100%">
          <el-option label="调用 (CALLS)" value="CALLS" />
          <el-option label="依赖 (DEPENDS_ON)" value="DEPENDS_ON" />
          <el-option label="使用 (USES)" value="USES" />
          <el-option label="部署 (DEPLOYED_ON)" value="DEPLOYED_ON" />
        </el-select>
      </el-form-item>
      <el-form-item label="置信度">
        <el-input-number v-model="form.confidence" :min="0" :max="100" style="width: 100%" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="createRelationship">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { relationshipApi, resourceApi, type Relationship, type Resource } from '@/api'

const topologyChart = ref<HTMLElement>()
const loading = ref(false)
const hasData = ref(false)
const nodeCount = ref(0)
const edgeCount = ref(0)
const dialogVisible = ref(false)
const form = reactive({
  sourceResourceId: '',
  targetResourceId: '',
  relationshipType: 'CALLS',
  confidence: 90,
})
let chart: echarts.ECharts | null = null

const CATEGORY_MAP: Record<string, number> = {
  SERVICE: 0,
  APPLICATION: 0,
  REDIS: 1,
  DATABASE: 2,
  MYSQL: 2,
  HOST: 3,
  NGINX: 3,
  JVM: 0,
}

const CATEGORIES = [
  { name: 'Service' },
  { name: 'Cache' },
  { name: 'Database' },
  { name: 'Infra' },
]

function categoryForType(type?: string): number {
  return CATEGORY_MAP[type || ''] ?? 0
}

function buildGraph(relationships: Relationship[], resources: Resource[]) {
  const nameMap = new Map(resources.map(r => [r.resourceId, r.resourceName || r.resourceId]))
  const typeMap = new Map(resources.map(r => [r.resourceId, r.resourceType]))

  const nodeIds = new Set<string>()
  relationships.forEach(r => {
    nodeIds.add(r.sourceResourceId)
    nodeIds.add(r.targetResourceId)
  })

  const nodes = Array.from(nodeIds).map(id => ({
    id,
    name: nameMap.get(id) || id,
    category: categoryForType(typeMap.get(id)),
    symbolSize: 40,
    label: { show: true },
  }))

  const links = relationships.map(r => ({
    source: r.sourceResourceId,
    target: r.targetResourceId,
    value: r.relationshipType,
    lineStyle: { width: Math.max(1, (r.confidence || 50) / 25) },
  }))

  return { nodes, links }
}

async function loadTopology() {
  loading.value = true
  try {
    const [relRes, resRes] = await Promise.all([
      relationshipApi.list(),
      resourceApi.list(),
    ])
    const relationships: Relationship[] = relRes.data || []
    const resources: Resource[] = resRes.data || []

    if (!relationships.length) {
      hasData.value = false
      return
    }

    hasData.value = true
    const { nodes, links } = buildGraph(relationships, resources)
    nodeCount.value = nodes.length
    edgeCount.value = links.length

    await new Promise(r => setTimeout(r, 0))
    if (!topologyChart.value) return

    chart?.dispose()
    chart = echarts.init(topologyChart.value)
    chart.setOption({
      tooltip: {
        formatter: (params: any) => {
          if (params.dataType === 'edge') {
            return `${params.data.source} → ${params.data.target}<br/>${params.data.value || ''}`
          }
          return params.data.name
        },
      },
      legend: [{ data: CATEGORIES.map(c => c.name) }],
      series: [{
        type: 'graph',
        layout: 'force',
        roam: true,
        categories: CATEGORIES,
        label: { show: true, position: 'right' },
        force: { repulsion: 320, edgeLength: 120 },
        data: nodes,
        links,
        lineStyle: { color: '#aaa', curveness: 0.2 },
        emphasis: { focus: 'adjacency' },
      }],
    })
  } catch (e) {
    console.error(e)
    hasData.value = false
  } finally {
    loading.value = false
  }
}

function handleResize() {
  chart?.resize()
}

function openCreateDialog() {
  form.sourceResourceId = ''
  form.targetResourceId = ''
  form.relationshipType = 'CALLS'
  form.confidence = 90
  dialogVisible.value = true
}

async function createRelationship() {
  if (!form.sourceResourceId || !form.targetResourceId) {
    ElMessage.warning('请选择源资源和目标资源')
    return
  }
  try {
    await relationshipApi.create({
      sourceResourceId: form.sourceResourceId,
      targetResourceId: form.targetResourceId,
      relationshipType: form.relationshipType,
      confidence: form.confidence,
    })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadTopology()
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

onMounted(() => {
  loadTopology()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; }
.meta { color: #909399; font-size: 13px; }
</style>
