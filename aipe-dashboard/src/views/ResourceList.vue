<template>
  <div>
    <el-card>
      <template #header>
        <div class="header">
          <span>资源列表</span>
          <el-button type="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>新建资源
          </el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm">
        <el-form-item label="业务系统">
          <el-input v-model="queryForm.business_system" placeholder="搜索" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryForm.type" clearable>
            <el-option v-for="t in types" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable>
            <el-option label="RUNNING" value="RUNNING" />
            <el-option label="STOPPED" value="STOPPED" />
            <el-option label="MAINTENANCE" value="MAINTENANCE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="resources" v-loading="loading" stripe>
        <el-table-column prop="resourceId" label="ID" width="220" />
        <el-table-column prop="resourceName" label="名称" />
        <el-table-column prop="resourceType" label="类型" width="120" />
        <el-table-column prop="businessSystem" label="业务系统" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="environment" label="环境" width="100" />
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/resources/${row.resourceId}`)">
              详情
            </el-button>
            <el-button size="small" @click="openStatusDialog(row)">
              状态
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="创建资源" width="560px" @closed="resetForm">
      <el-form :model="form" label-width="110px">
        <el-form-item label="资源名称" required>
          <el-input v-model="form.resourceName" />
        </el-form-item>
        <el-form-item label="资源类型" required>
          <el-select v-model="form.resourceType" @change="onTypeChange">
            <el-option v-for="t in types" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务系统" required>
          <el-input v-model="form.businessSystem" />
        </el-form-item>
        <el-form-item label="环境">
          <el-select v-model="form.environment">
            <el-option label="prod" value="prod" />
            <el-option label="staging" value="staging" />
            <el-option label="test" value="test" />
          </el-select>
        </el-form-item>

        <template v-if="typeFields.length">
          <el-divider content-position="left">连接配置</el-divider>
          <el-form-item
            v-for="field in typeFields"
            :key="field.key"
            :label="field.label"
            :required="field.required"
          >
            <el-input
              v-model="form.attributes[field.key]"
              :type="field.type"
              :placeholder="field.placeholder"
              :show-password="field.type === 'password'"
            />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>

    <!-- 状态变更对话框 -->
    <el-dialog v-model="statusDialogVisible" title="变更资源状态" width="400px">
      <el-form label-width="80px">
        <el-form-item label="当前状态">
          <el-tag :type="statusTag(currentResource?.status)">{{ currentResource?.status }}</el-tag>
        </el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="newStatus" style="width: 100%">
            <el-option label="RUNNING" value="RUNNING" />
            <el-option label="STOPPED" value="STOPPED" />
            <el-option label="MAINTENANCE" value="MAINTENANCE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateStatus">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { resourceApi, type Resource } from '@/api'
import { RESOURCE_TYPES, getFieldsForType } from '@/config/resourceFormFields'

const resources = ref<Resource[]>([])
const loading = ref(false)
const creating = ref(false)
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const currentResource = ref<Resource | null>(null)
const newStatus = ref('RUNNING')

const queryForm = reactive({
  business_system: '',
  type: '',
  status: '',
})

const form = reactive({
  resourceName: '',
  resourceType: 'SERVICE',
  businessSystem: '',
  environment: 'test',
  attributes: {} as Record<string, string>,
})

const types = RESOURCE_TYPES
const typeFields = computed(() => getFieldsForType(form.resourceType))

function statusTag(status: string) {
  const map: Record<string, string> = {
    RUNNING: 'success',
    STOPPED: 'danger',
    MAINTENANCE: 'warning',
  }
  return map[status] || 'info'
}

function onTypeChange() {
  form.attributes = {}
}

function openCreateDialog() {
  resetForm()
  dialogVisible.value = true
}

function resetForm() {
  form.resourceName = ''
  form.resourceType = 'SERVICE'
  form.businessSystem = ''
  form.environment = 'test'
  form.attributes = {}
}

async function loadData() {
  loading.value = true
  try {
    const params: Record<string, string> = {}
    if (queryForm.business_system) params.business_system = queryForm.business_system
    if (queryForm.type) params.type = queryForm.type
    if (queryForm.status) params.status = queryForm.status
    const res = await resourceApi.list(params)
    resources.value = res.data || []
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  if (!form.resourceName || !form.businessSystem) {
    ElMessage.warning('请填写资源名称和业务系统')
    return
  }
  for (const field of typeFields.value) {
    if (field.required && !form.attributes[field.key]) {
      ElMessage.warning(`请填写 ${field.label}`)
      return
    }
  }
  creating.value = true
  try {
    await resourceApi.create({
      resourceName: form.resourceName,
      resourceType: form.resourceType,
      businessSystem: form.businessSystem,
      environment: form.environment,
      attributes: { ...form.attributes },
    })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } finally {
    creating.value = false
  }
}

const statusDialogVisible = ref(false)
const currentResource = ref<Resource | null>(null)
const newStatus = ref('RUNNING')

function openStatusDialog(row: Resource) {
  currentResource.value = row
  newStatus.value = row.status
  statusDialogVisible.value = true
}

async function updateStatus() {
  if (!currentResource.value) return
  try {
    await resourceApi.updateStatus(currentResource.value.resourceId, newStatus.value)
    ElMessage.success('状态更新成功')
    statusDialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('状态更新失败')
  }
}

async function handleDelete(row: Resource) {
  await ElMessageBox.confirm(`确定删除资源 "${row.resourceName}"？`, '提示', { type: 'warning' })
  try {
    await resourceApi.delete(row.resourceId)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(loadData)
</script>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { resourceApi, type Resource } from '@/api'
import { RESOURCE_TYPES, getFieldsForType } from '@/config/resourceFormFields'

// ... existing code ...

function openStatusDialog(row: Resource) {
  currentResource.value = row
  newStatus.value = row.status
  statusDialogVisible.value = true
}

async function updateStatus() {
  if (!currentResource.value) return
  try {
    await resourceApi.updateStatus(currentResource.value.resourceId, newStatus.value)
    ElMessage.success('状态更新成功')
    statusDialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('状态更新失败')
  }
}

async function handleDelete(row: Resource) {
  await ElMessageBox.confirm(`确定删除资源 "${row.resourceName}"？`, '提示', { type: 'warning' })
  try {
    await resourceApi.delete(row.resourceId)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

<!-- 状态变更对话框 -->
<el-dialog v-model="statusDialogVisible" title="变更资源状态" width="400px">
  <el-form label-width="80px">
    <el-form-item label="当前状态">
      <el-tag :type="statusTag(currentResource?.status)">{{ currentResource?.status }}</el-tag>
    </el-form-item>
    <el-form-item label="新状态">
      <el-select v-model="newStatus" style="width: 100%">
        <el-option label="RUNNING" value="RUNNING" />
        <el-option label="STOPPED" value="STOPPED" />
        <el-option label="MAINTENANCE" value="MAINTENANCE" />
      </el-select>
    </el-form-item>
  </el-form>
  <template #footer>
    <el-button @click="statusDialogVisible = false">取消</el-button>
    <el-button type="primary" @click="updateStatus">确认</el-button>
  </template>
</el-dialog>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; }
</style>
