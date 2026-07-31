<template>
  <div>
    <el-card v-loading="loading">
      <template #header>
        <div class="header">
          <span>优化推荐</span>
          <el-button type="primary" size="small" @click="openGenerateDialog">生成推荐</el-button>
        </div>
      </template>
      <el-table :data="recommendations" stripe>
        <el-table-column prop="recommendationId" label="ID" width="200" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="targetResourceId" label="目标资源" width="200" />
        <el-table-column label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="priorityType(row.priority)">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置信度" width="120">
          <template #default="{ row }">
            <el-progress :percentage="Math.round(row.confidence || 0)" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING'"
              size="small"
              type="success"
              @click="handleApprove(row)"
            >
              采纳
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              size="small"
              type="danger"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button
              v-if="row.status === 'APPROVED'"
              size="small"
              type="primary"
              @click="handleExecute(row)"
            >
              执行
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && !recommendations.length" description="暂无推荐数据" />
    </el-card>

    <!-- 生成推荐对话框 -->
    <el-dialog v-model="generateVisible" title="生成推荐" width="500px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="知识" required>
          <el-select v-model="generateForm.knowledgeId" filterable placeholder="选择知识" style="width: 100%">
            <el-option v-for="k in knowledgeList" :key="k.knowledgeId" :label="k.title" :value="k.knowledgeId" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标资源" required>
          <el-select v-model="generateForm.targetResourceId" filterable placeholder="选择目标资源" style="width: 100%">
            <el-option v-for="r in resourceList" :key="r.resourceId" :label="`${r.resourceName} (${r.resourceId})`" :value="r.resourceId" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="generateForm.title" placeholder="推荐标题" />
        </el-form-item>
        <el-form-item label="置信度">
          <el-input-number v-model="generateForm.confidence" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" @click="generateRecommendation">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { recommendationApi, knowledgeApi, resourceApi, type Recommendation, type Knowledge, type Resource } from '@/api'

const recommendations = ref<Recommendation[]>([])
const knowledgeList = ref<Knowledge[]>([])
const resourceList = ref<Resource[]>([])
const loading = ref(false)
const generateVisible = ref(false)
const generateForm = reactive({
  knowledgeId: '',
  targetResourceId: '',
  title: '',
  confidence: 80,
})

function priorityType(p: string) {
  return { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }[p] || 'info'
}

function statusType(s: string) {
  const map: Record<string, string> = {
    PENDING: 'info',
    APPROVED: 'success',
    REJECTED: 'danger',
    EXECUTED: 'warning',
  }
  return map[s] || 'info'
}

async function loadData() {
  loading.value = true
  try {
    const statuses = ['PENDING', 'APPROVED', 'EXECUTED', 'REJECTED']
    const results = await Promise.all(
      statuses.map(s => recommendationApi.list({ status: s }).catch(() => ({ data: [] })))
    )
    const merged = results.flatMap(r => r.data || [])
    const seen = new Set<string>()
    recommendations.value = merged.filter(r => {
      if (seen.has(r.recommendationId)) return false
      seen.add(r.recommendationId)
      return true
    })
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleApprove(row: Recommendation) {
  await recommendationApi.approve(row.recommendationId)
  ElMessage.success('已采纳')
  loadData()
}

async function handleReject(row: Recommendation) {
  await recommendationApi.reject(row.recommendationId)
  ElMessage.success('已拒绝')
  loadData()
}

async function handleExecute(row: Recommendation) {
  await recommendationApi.execute(row.recommendationId)
  ElMessage.success('已标记执行')
  loadData()
}

onMounted(() => {
  loadData()
  loadKnowledge()
  loadResources()
})

async function loadKnowledge() {
  try {
    const res = await knowledgeApi.list()
    knowledgeList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function loadResources() {
  try {
    const res = await resourceApi.list()
    resourceList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

function openGenerateDialog() {
  generateForm.knowledgeId = ''
  generateForm.targetResourceId = ''
  generateForm.title = ''
  generateForm.confidence = 80
  generateVisible.value = true
}

async function generateRecommendation() {
  if (!generateForm.knowledgeId || !generateForm.targetResourceId) {
    ElMessage.warning('请选择知识和目标资源')
    return
  }
  try {
    await recommendationApi.generate({
      knowledgeId: generateForm.knowledgeId,
      targetResourceId: generateForm.targetResourceId,
      title: generateForm.title,
      confidence: generateForm.confidence,
    })
    ElMessage.success('生成成功')
    generateVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('生成失败')
  }
}
</script>
</el-dialog>
