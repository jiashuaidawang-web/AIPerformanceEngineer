<template>
  <div>
    <el-card>
      <template #header>执行记录</template>
      <el-timeline>
        <el-timeline-item
          v-for="item in executions"
          :key="item.executionId"
          :timestamp="item.finishedAt || item.startedAt"
          :type="item.status === 'SUCCESS' ? 'success' : item.status === 'FAILED' ? 'danger' : 'primary'"
          placement="top"
        >
          <el-card>
            <h4>{{ item.executor }} - {{ item.executionType }}</h4>
            <p>状态: {{ item.status }}</p>
            <p v-if="item.improvementScore > 0">
              优化评分: <el-rate v-model="item.improvementScore" disabled :max="5" />
            </p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { executionApi, type Execution } from '@/api'

const executions = ref<Execution[]>([])

async function loadData() {
  try {
    const res = await executionApi.list()
    executions.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

onMounted(loadData)
</style>
