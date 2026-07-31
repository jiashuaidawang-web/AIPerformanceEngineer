<template>
  <div>
    <el-card>
      <template #header>
        <div class="header">
          <span>告警记录</span>
          <el-radio-group v-model="filterStatus" size="small" @change="loadRecords">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="FIRING">触发中</el-radio-button>
            <el-radio-button label="RESOLVED">已恢复</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="records" v-loading="loading" stripe>
        <el-table-column prop="alertId" label="告警ID" width="150" />
        <el-table-column prop="resourceId" label="资源" width="150" />
        <el-table-column prop="metricName" label="指标" width="120" />
        <el-table-column label="触发值/阈值" width="150">
          <template #default="{ row }">
            {{ row.triggerValue?.toFixed(2) }} / {{ row.threshold }}
          </template>
        </el-table-column>
        <el-table-column prop="severity" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="severityType(row.severity)">{{ row.severity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="消息" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'FIRING' ? 'danger' : 'success'">
              {{ row.status === 'FIRING' ? '触发中' : '已恢复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="触发时间" width="180">
          <template #default="{ row }">
            {{ row.triggeredAt }}
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && !records.length" description="暂无告警记录" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { alertApi } from '@/api'

interface AlertRecord {
  alertId: string
  ruleId: string
  resourceId: string
  metricName: string
  triggerValue: number
  threshold: number
  severity: string
  message: string
  status: string
  triggeredAt: string
  resolvedAt?: string
}

const records = ref<AlertRecord[]>([])
const loading = ref(false)
const filterStatus = ref('')

async function loadRecords() {
  loading.value = true
  try {
    const res = await alertApi.listRecords(filterStatus.value || undefined)
    records.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function severityType(severity: string) {
  const map: Record<string, string> = { P0: 'danger', P1: 'warning', P2: 'info', P3: 'success' }
  return map[severity] || 'info'
}

onMounted(() => {
  loadRecords()
})
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; }
</style>
