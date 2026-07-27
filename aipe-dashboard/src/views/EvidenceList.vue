<template>
  <div>
    <el-card>
      <template #header>Evidence 证据链</template>
      <el-table :data="evidences" stripe>
        <el-table-column prop="evidenceId" label="ID" width="200" />
        <el-table-column prop="title" label="标题" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.evidenceType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置信度" width="120">
          <template #default="{ row }">
            <el-progress :percentage="Math.round(row.confidence)" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'VERIFIED' ? 'success' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="showExplain(row)">解释</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="explainVisible" title="Evidence 解释" width="700px">
      <pre class="explain-text">{{ currentExplain }}</pre>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { evidenceApi, type Evidence } from '@/api'

const evidences = ref<Evidence[]>([])
const explainVisible = ref(false)
const currentExplain = ref('')

async function loadEvidences() {
  try {
    const res = await evidenceApi.list()
    evidences.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function showExplain(evidence: Evidence) {
  try {
    const res = await evidenceApi.explain(evidence.evidenceId)
    currentExplain.value = res.data || JSON.stringify(evidence.reasoningSteps, null, 2)
    explainVisible.value = true
  } catch (e) {
    currentExplain.value = JSON.stringify(evidence.reasoningSteps, null, 2)
    explainVisible.value = true
  }
}

onMounted(loadEvidences)
</script>

<style scoped>
.explain-text {
  white-space: pre-wrap;
  font-family: monospace;
  background: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
}
</style>
