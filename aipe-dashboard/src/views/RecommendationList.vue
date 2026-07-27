<template>
  <div>
    <el-card>
      <template #header>优化推荐</template>
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
            <el-progress :percentage="Math.round(row.confidence)" />
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'APPROVED' ? 'success' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING'"
              size="small"
              type="success"
              @click="handleApprove(row)"
            >
              采纳
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { recommendationApi, type Recommendation } from '@/api'

const recommendations = ref<Recommendation[]>([])

function priorityType(p: string) {
  return { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }[p] || 'info'
}

async function loadData() {
  try {
    const res = await recommendationApi.list()
    recommendations.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

async function handleApprove(row: Recommendation) {
  try {
    await recommendationApi.approve(row.recommendationId)
    ElMessage.success('已采纳')
    loadData()
  } catch (e) {
    // handled
  }
}

onMounted(loadData)
</script>
</template>
