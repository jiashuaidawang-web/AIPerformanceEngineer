<template>
  <div>
    <el-card v-loading="loading">
      <template #header>执行记录</template>
      <el-timeline v-if="executions.length">
        <el-timeline-item
          v-for="item in executions"
          :key="item.executionId"
          :timestamp="item.finishedAt || item.startedAt || ''"
          :type="item.status === 'SUCCESS' ? 'success' : item.status === 'FAILED' ? 'danger' : 'primary'"
          placement="top"
        >
          <el-card>
            <div class="exec-header">
              <h4>{{ item.executor }} - {{ item.executionType }}</h4>
              <el-button size="small" link type="primary" @click="showReport(item)">查看报告</el-button>
            </div>
            <p>状态: {{ item.status }}</p>
            <p v-if="item.improvementScore > 0">优化评分: {{ item.improvementScore }}</p>
            <p v-if="item.recommendationId">关联推荐: {{ item.recommendationId }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无执行记录" />
    </el-card>

    <el-dialog v-model="reportVisible" title="执行报告" width="700px">
      <pre class="report-text">{{ reportText }}</pre>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { executionApi, type Execution } from '@/api'

const executions = ref<Execution[]>([])
const loading = ref(false)
const reportVisible = ref(false)
const reportText = ref('')

async function loadData() {
  loading.value = true
  try {
    const statuses = ['PENDING', 'EXECUTING', 'SUCCESS', 'FAILED', 'ROLLED_BACK']
    const results = await Promise.all(
      statuses.map(s => executionApi.list({ status: s }).catch(() => ({ data: [] })))
    )
    const merged = results.flatMap(r => r.data || [])
    const seen = new Set<string>()
    executions.value = merged
      .filter(e => {
        if (seen.has(e.executionId)) return false
        seen.add(e.executionId)
        return true
      })
      .sort((a, b) => {
        const ta = new Date(a.finishedAt || a.startedAt || 0).getTime()
        const tb = new Date(b.finishedAt || b.startedAt || 0).getTime()
        return tb - ta
      })
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function showReport(item: Execution) {
  reportVisible.value = true
  reportText.value = '加载中...'
  try {
    const res = await executionApi.report(item.executionId)
    reportText.value = res.data || '暂无报告'
  } catch {
    reportText.value = '报告加载失败'
  }
}

onMounted(loadData)
</script>

<style scoped>
.exec-header { display: flex; justify-content: space-between; align-items: center; }
.exec-header h4 { margin: 0; }
.report-text {
  white-space: pre-wrap;
  font-family: monospace;
  background: #f5f7fa;
  padding: 16px;
  border-radius: 4px;
}
</style>
