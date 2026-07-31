<template>
  <div>
    <el-card>
      <template #header>
        <div class="header">
          <span>告警规则</span>
          <el-button type="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>新建规则
          </el-button>
        </div>
      </template>

      <el-table :data="rules" v-loading="loading" stripe>
        <el-table-column prop="ruleName" label="规则名称" />
        <el-table-column prop="resourceId" label="资源" width="150">
          <template #default="{ row }">
            {{ row.resourceId || '全局' }}
          </template>
        </el-table-column>
        <el-table-column label="条件" width="180">
          <template #default="{ row }">
            {{ row.metricName }} {{ conditionText(row.condition) }} {{ row.threshold }}
          </template>
        </el-table-column>
        <el-table-column prop="severity" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="severityType(row.severity)">{{ row.severity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="通知方式" width="120">
          <template #default="{ row }">
            {{ notifyText(row.notifyType) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.enabled" @change="toggleRule(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteRule(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editing ? '编辑规则' : '新建规则'" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="规则名称" required>
          <el-input v-model="form.ruleName" placeholder="例如: CPU 过高告警" />
        </el-form-item>
        <el-form-item label="资源">
          <el-select v-model="form.resourceId" clearable placeholder="全局规则" style="width: 100%">
            <el-option
              v-for="r in resources"
              :key="r.resourceId"
              :label="`${r.resourceName} (${r.resourceId})`"
              :value="r.resourceId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="指标" required>
          <el-select v-model="form.metricName" placeholder="选择指标" style="width: 100%">
            <el-option label="CPU 使用率" value="cpu.usage" />
            <el-option label="内存使用率" value="memory.usage" />
            <el-option label="JVM 堆内存" value="jvm.memory.heap.used" />
            <el-option label="TPS" value="tps" />
            <el-option label="P99 延迟" value="latency.p99" />
          </el-select>
        </el-form-item>
        <el-form-item label="条件" required>
          <el-select v-model="form.condition" style="width: 120px">
            <el-option label="大于" value="GT" />
            <el-option label="大于等于" value="GTE" />
            <el-option label="小于" value="LT" />
            <el-option label="小于等于" value="LTE" />
          </el-select>
          <el-input-number v-model="form.threshold" :min="0" :max="100000" style="margin-left: 10px" />
        </el-form-item>
        <el-form-item label="级别" required>
          <el-select v-model="form.severity" style="width: 100%">
            <el-option label="P0 - 紧急" value="P0" />
            <el-option label="P1 - 严重" value="P1" />
            <el-option label="P2 - 警告" value="P2" />
            <el-option label="P3 - 提示" value="P3" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知方式" required>
          <el-select v-model="form.notifyType" style="width: 100%">
            <el-option label="Webhook" value="webhook" />
            <el-option label="钉钉" value="dingtalk" />
            <el-option label="企业微信" value="wecom" />
            <el-option label="邮件" value="email" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知地址" required>
          <el-input v-model="form.notifyTarget" placeholder="Webhook URL / 邮箱 / 群机器人地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRule">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { alertApi } from '@/api'
import { resourceApi } from '@/api'

interface AlertRule {
  ruleId?: string
  ruleName: string
  resourceId?: string
  metricName: string
  condition: string
  threshold: number
  severity: string
  notifyType: string
  notifyTarget: string
  enabled?: boolean
}

const rules = ref<AlertRule[]>([])
const resources = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const editing = ref(false)

const form = reactive<AlertRule>({
  ruleName: '',
  resourceId: '',
  metricName: 'cpu.usage',
  condition: 'GT',
  threshold: 80,
  severity: 'P1',
  notifyType: 'webhook',
  notifyTarget: '',
})

async function loadRules() {
  loading.value = true
  try {
    const res = await alertApi.listRules()
    rules.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function loadResources() {
  try {
    const res = await resourceApi.list()
    resources.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

function openCreateDialog() {
  editing.value = false
  Object.assign(form, {
    ruleName: '',
    resourceId: '',
    metricName: 'cpu.usage',
    condition: 'GT',
    threshold: 80,
    severity: 'P1',
    notifyType: 'webhook',
    notifyTarget: '',
  })
  dialogVisible.value = true
}

function openEditDialog(row: AlertRule) {
  editing.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

async function saveRule() {
  if (!form.ruleName || !form.metricName || !form.notifyTarget) {
    ElMessage.warning('请填写必填项')
    return
  }
  try {
    if (editing.value && form.ruleId) {
      await alertApi.updateRule(form.ruleId, form)
      ElMessage.success('更新成功')
    } else {
      await alertApi.createRule(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadRules()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

async function deleteRule(row: AlertRule) {
  await ElMessageBox.confirm(`确定删除规则 "${row.ruleName}"？`, '提示', { type: 'warning' })
  await alertApi.deleteRule(row.ruleId!)
  ElMessage.success('删除成功')
  loadRules()
}

async function toggleRule(row: AlertRule) {
  await alertApi.updateRule(row.ruleId!, row)
}

function conditionText(cond: string) {
  const map: Record<string, string> = { GT: '>', GTE: '≥', LT: '<', LTE: '≤' }
  return map[cond] || cond
}

function severityType(severity: string) {
  const map: Record<string, string> = { P0: 'danger', P1: 'warning', P2: 'info', P3: 'success' }
  return map[severity] || 'info'
}

function notifyText(type: string) {
  const map: Record<string, string> = { webhook: 'Webhook', dingtalk: '钉钉', wecom: '企业微信', email: '邮件' }
  return map[type] || type
}

onMounted(() => {
  loadRules()
  loadResources()
})
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; }
</style>
