<template>
  <div>
    <el-card v-loading="loading">
      <template #header>
        <div class="header">
          <span>Agent 管理</span>
          <el-button type="primary" size="small" @click="loadAgents">刷新</el-button>
        </div>
      </template>

      <el-table :data="agents" stripe>
        <el-table-column prop="agentId" label="Agent ID" width="150" />
        <el-table-column prop="serverId" label="服务器" width="150" />
        <el-table-column prop="hostname" label="主机名" width="150" />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ONLINE' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后心跳" width="180">
          <template #default="{ row }">
            {{ formatTime(row.lastHeartbeat) }}
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.registeredAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openConfigDialog(row)">配置</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && !agents.length" description="暂无注册的 Agent" />
    </el-card>

    <!-- Agent 详情对话框 -->
    <el-dialog v-model="detailVisible" title="Agent 详情" width="600px">
      <div v-if="currentAgent">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="Agent ID">{{ currentAgent.agentId }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentAgent.status === 'ONLINE' ? 'success' : 'danger'">{{ currentAgent.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="主机名">{{ currentAgent.hostname }}</el-descriptions-item>
          <el-descriptions-item label="IP">{{ currentAgent.ip }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatTime(currentAgent.registeredAt) }}</el-descriptions-item>
          <el-descriptions-item label="最后心跳">{{ formatTime(currentAgent.lastHeartbeat) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- Agent 配置对话框 -->
    <el-dialog v-model="configVisible" title="Agent 配置" width="500px">
      <el-form :model="configForm" label-width="120px">
        <el-form-item label="Agent ID">
          <el-input v-model="currentAgent.agentId" disabled />
        </el-form-item>
        <el-form-item label="采集间隔 (秒)">
          <el-input-number v-model="configForm.intervalMs" :min="5" :max="300" style="width: 100%" />
        </el-form-item>
        <el-form-item label="JVM 采集">
          <el-switch v-model="configForm.jvmEnabled" />
        </el-form-item>
        <el-form-item label="Linux 采集">
          <el-switch v-model="configForm.linuxEnabled" />
        </el-form-item>
        <el-form-item label="Redis 采集">
          <el-switch v-model="configForm.redisEnabled" />
        </el-form-item>
        <el-form-item label="MySQL 采集">
          <el-switch v-model="configForm.mysqlEnabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configVisible = false">取消</el-button>
        <el-button type="primary" @click="saveConfig">保存并下发</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { agentApi } from '@/api'

interface Agent {
  agentId: string
  serverId: string
  hostname: string
  ip: string
  status: string
  lastHeartbeat: string
  registeredAt: string
}

const agents = ref<Agent[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const configVisible = ref(false)
const currentAgent = ref<Agent | null>(null)
const configForm = reactive({
  intervalMs: 30,
  jvmEnabled: true,
  linuxEnabled: true,
  redisEnabled: false,
  mysqlEnabled: false,
})

async function loadAgents() {
  loading.value = true
  try {
    const res = await agentApi.list()
    agents.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function handleDelete(row: Agent) {
  await ElMessageBox.confirm(`确定移除 Agent "${row.agentId}"？`, '提示', { type: 'warning' })
  try {
    await agentApi.delete(row.agentId)
    ElMessage.success('移除成功')
    loadAgents()
  } catch (e) {
    ElMessage.error('移除失败')
  }
}

function formatTime(time: string) {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

function openConfigDialog(row: Agent) {
  currentAgent.value = row
  configForm.intervalMs = 30
  configForm.jvmEnabled = true
  configForm.linuxEnabled = true
  configForm.redisEnabled = false
  configForm.mysqlEnabled = false
  configVisible.value = true
}

async function saveConfig() {
  if (!currentAgent.value) return
  try {
    await configApi.publish(currentAgent.value.agentId, configForm)
    ElMessage.success('配置已下发')
    configVisible.value = false
  } catch (e) {
    ElMessage.error('配置下发失败')
  }
}

onMounted(loadAgents)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; }
</style>
