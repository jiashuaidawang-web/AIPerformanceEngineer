<template>
  <div>
    <el-card>
      <template #header>知识库</template>
      <el-row :gutter="20">
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
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { knowledgeApi, type Knowledge } from '@/api'

const knowledgeList = ref<Knowledge[]>([])

async function loadKnowledge() {
  try {
    const res = await knowledgeApi.list()
    knowledgeList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

onMounted(loadKnowledge)
</script>

<style scoped>
.knowledge-card { margin-bottom: 20px; }
.meta { display: flex; justify-content: space-between; color: #999; font-size: 12px; margin-top: 10px; }
</style>
