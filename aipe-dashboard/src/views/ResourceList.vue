<template>
  <div>
    <el-card>
      <template #header>
        <div class="header">
          <span>资源列表</span>
          <el-button type="primary" @click="dialogVisible = true">
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
            <el-option label="APPLICATION" value="APPLICATION" />
            <el-option label="SERVICE" value="SERVICE" />
            <el-option label="DATABASE" value="DATABASE" />
            <el-option label="REDIS" value="REDIS" />
            <el-option label="HOST" value="HOST" />
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
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/resources/${row.resourceId}`)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="创建资源" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="资源名称">
          <el-input v-model="form.resourceName" />
        </el-form-item>
        <el-form-item label="资源类型">
          <el-select v-model="form.resourceType">
            <el-option v-for="t in types" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务系统">
          <el-input v-model="form.businessSystem" />
        </el-form-item>
        <el-form-item label="环境">
          <el-select v-model="form.environment">
            <el-option label="prod" value="prod" />
            <el-option label="staging" value="staging" />
            <el-option label="test" value="test" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { resourceApi, type Resource } from '@/api'

const resources = ref<Resource[]>([])
const loading = ref(false)
const dialogVisible = ref(false)

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
})

const types = ['APPLICATION', 'SERVICE', 'DATABASE', 'REDIS', 'HOST', 'CLUSTER']

function statusTag(status: string) {
  const map: Record<string, string> = {
    RUNNING: 'success',
    STOPPED: 'danger',
    MAINTENANCE: 'warning',
  }
  return map[status] || 'info'
}

async function loadData() {
  loading.value = true
  try {
    const res = await resourceApi.list(queryForm)
    resources.value = res.data || []
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  try {
    await resourceApi.create(form)
    ElMessage.success('创建成功')
    dialogVisible = false
    loadData()
  } catch (e) {
    // error handled by interceptor
  }
}

onMounted(loadData)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; }
</style>
