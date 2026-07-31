<template>
  <div>
    <el-card v-loading="loading">
      <template #header>
        <div class="header">
          <span>Evidence 证据链</span>
          <el-button type="primary" size="small" @click="openGenerateDialog">生成 Evidence</el-button>
        </div>
      </template>
      <el-table :data="evidences" stripe>
        <el-table-column prop="evidenceId" label="ID" width="200" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="rootResourceId" label="根资源" width="180" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.evidenceType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置信度" width="120">
          <template #default="{ row }">
            <el-progress :percentage="Math.round(row.confidence || 0)" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'VERIFIED' ? 'success' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="showExplain(row)">解释</el-button>
            <el-button v-if="row.status === 'NEW'" size="small" type="success" @click="handleVerify(row, true)">确认</el-button>
            <el-button v-if="row.status === 'NEW'" size="small" type="danger" @click="handleVerify(row, false)">拒绝</el-button>
            <el-tag v-else size="small" :type="row.status === 'VERIFIED' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && !evidences.length" description="暂无 Evidence 数据" />
    </el-card>

    <!-- 生成 Evidence 对话框 -->
    <el-dialog v-model="generateVisible" title="生成 Evidence" width="500px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="资源" required>
          <el-select v-model="generateForm.resourceId" filterable placeholder="选择资源" style="width: 100%">
            <el-option v-for="r in resources" :key="r.resourceId" :label="`${r.resourceName} (${r.resourceId})`" :value="r.resourceId" />
          </el-select>
        </el-form-item>
        <el-form-item label="指标">
          <el-select v-model="generateForm.metricName" clearable placeholder="全部指标" style="width: 100%">
            <el-option label="CPU 使用率" value="cpu.usage" />
            <el-option label="内存使用率" value="memory.usage" />
            <el-option label="JVM 堆内存" value="jvm.memory.heap.used" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateVisible = false">取消</el-button>
        <el-button type="primary" @click="generateEvidence">生成</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="explainVisible" title="Evidence 推理链" width="760px">
      <div v-if="currentEvidence">
        <p><strong>{{ currentEvidence.title }}</strong></p>
        <p class="desc">{{ currentEvidence.description }}</p>
        <el-divider />
        <pre v-if="explainText" class="explain-text">{{ explainText }}</pre>
        <el-timeline v-if="currentEvidence.reasoningSteps?.length">
          <el-timeline-item
            v-for="step in currentEvidence.reasoningSteps"
            :key="step.step"
            :timestamp="`Step ${step.step}`"
          >
            <p><strong>{{ step.action }}</strong></p>
            <p>{{ step.result }}</p>
            <el-tag size="small">置信度 {{ step.confidence }}%</el-tag>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { evidenceApi, resourceApi, type Evidence, type Resource } from '@/api'

const evidences = ref<Evidence[]>([])
const resources = ref<Resource[]>([])
const loading = ref(false)
const explainVisible = ref(false)
const generateVisible = ref(false)
const explainText = ref('')
const currentEvidence = ref<Evidence | null>(null)
const generateForm = reactive({
  resourceId: '',
  metricName: '',
})

async function loadEvidences() {
  loading.value = true
  try {
    const res = await evidenceApi.list()
    evidences.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function showExplain(evidence: Evidence) {
  currentEvidence.value = evidence
  explainText.value = ''
  explainVisible.value = true
  try {
    const res = await evidenceApi.explain(evidence.evidenceId)
    explainText.value = res.data || ''
  } catch {
    explainText.value = ''
  }
}

onMounted(() => {
  loadEvidences()
  loadResources()
})

async function loadResources() {
  try {
    const res = await resourceApi.list()
    resources.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

function openGenerateDialog() {
  generateForm.resourceId = ''
  generateForm.metricName = ''
  generateVisible.value = true
}

async function generateEvidence() {
  if (!generateForm.resourceId) {
    ElMessage.warning('请选择资源')
    return
  }
  try {
    const params: Record<string, unknown> = {
      resourceId: generateForm.resourceId,
      startTime: 0,
      endTime: Date.now(),
    }
    if (generateForm.metricName) {
      params.metricName = generateForm.metricName
    }
    await evidenceApi.generate(params)
    ElMessage.success('生成成功')
    generateVisible.value = false
    loadEvidences()
  } catch (e) {
    ElMessage.error('生成失败')
  }
}

async function handleVerify(evidence: Evidence, approved: boolean) {
  try {
    await evidenceApi.verify(evidence.evidenceId, approved)
    ElMessage.success(approved ? '已确认' : '已拒绝')
    loadEvidences()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}
</script>

<style scoped>
.desc { color: #606266; margin: 8px 0; }
.explain-text {
  white-space: pre-wrap;
  font-family: monospace;
  background: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
  margin-bottom: 16px;
}
</style>
