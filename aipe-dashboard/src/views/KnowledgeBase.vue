<template>
  <div>
    <el-card v-loading="loading">
      <template #header>
        <div class="header">
          <span>知识库</span>
          <el-button type="primary" size="small" @click="openCreateDialog">创建知识</el-button>
        </div>
      </template>
      <el-row :gutter="20" v-if="knowledgeList.length">
        <el-col v-for="item in knowledgeList" :key="item.knowledgeId" :span="8">
          <el-card class="knowledge-card" shadow="hover">
            <template #header>
              <span>{{ item.title }}</span>
              <el-tag size="small" style="float: right">{{ item.knowledgeType }}</el-tag>
            </template>
            <p>{{ item.description }}</p>
            <div class="meta">
              <span>置信度: {{ item.confidence }}%</span>
              <span>成功率: {{ item.successRate }}%</span>
            </div>
            <div v-if="item.evidenceId" class="evidence-link">
              来源 Evidence: {{ item.evidenceId }}
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无 Knowledge 数据" />
    </el-card>

    <!-- 创建知识对话框 -->
    <el-dialog v-model="dialogVisible" title="创建知识" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="例如: CPU 过高时扩容服务实例" />
        </el-form-item>
        <el-form-item label="描述" required>
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="知识描述" />
        </el-form-item>
        <el-form-item label="知识类型" required>
          <el-select v-model="form.knowledgeType" style="width: 100%">
            <el-option label="瓶颈 (BOTTLENECK)" value="BOTTLENECK" />
            <el-option label="依赖 (DEPENDENCY)" value="DEPENDENCY" />
            <el-option label="部署 (DEPLOYMENT)" value="DEPLOYMENT" />
            <el-option label="业务 (BUSINESS)" value="BUSINESS" />
            <el-option label="优化 (OPTIMIZATION)" value="OPTIMIZATION" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源 Evidence">
          <el-input v-model="form.evidenceId" placeholder="Evidence ID (可选)" />
        </el-form-item>
        <el-form-item label="置信度">
          <el-input-number v-model="form.confidence" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="成功率">
          <el-input-number v-model="form.successRate" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="推荐操作">
          <el-input v-model="form.recommendedAction" placeholder="例如: 扩容服务实例数量" />
        </el-form-item>
        <el-form-item label="预期效果">
          <el-input v-model="form.expectedEffect" placeholder="例如: CPU 使用率降低到 60% 以下" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createKnowledge">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { knowledgeApi, type Knowledge } from '@/api'

const knowledgeList = ref<Knowledge[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = reactive({
  title: '',
  description: '',
  knowledgeType: 'BOTTLENECK',
  evidenceId: '',
  confidence: 80,
  successRate: 70,
  recommendedAction: '',
  expectedEffect: '',
})

async function loadKnowledge() {
  loading.value = true
  try {
    const res = await knowledgeApi.list()
    knowledgeList.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  Object.assign(form, {
    title: '',
    description: '',
    knowledgeType: 'BOTTLENECK',
    evidenceId: '',
    confidence: 80,
    successRate: 70,
    recommendedAction: '',
    expectedEffect: '',
  })
  dialogVisible.value = true
}

async function createKnowledge() {
  if (!form.title || !form.description) {
    ElMessage.warning('请填写标题和描述')
    return
  }
  try {
    await knowledgeApi.create({
      title: form.title,
      description: form.description,
      knowledgeType: form.knowledgeType,
      evidenceId: form.evidenceId || undefined,
      confidence: form.confidence,
      successRate: form.successRate,
      recommendedAction: form.recommendedAction,
      expectedEffect: form.expectedEffect,
    })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadKnowledge()
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

onMounted(loadKnowledge)
</script>

<style scoped>
.header { display: flex; justify-content: space-between; align-items: center; }
.knowledge-card { margin-bottom: 20px; }
.meta { display: flex; justify-content: space-between; color: #999; font-size: 12px; margin-top: 10px; }
.evidence-link { margin-top: 8px; font-size: 12px; color: #909399; }
</style>
