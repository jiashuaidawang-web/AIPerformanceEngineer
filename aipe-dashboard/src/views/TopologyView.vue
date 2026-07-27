<template>
  <div>
    <el-card>
      <template #header>服务拓扑</template>
      <div ref="topologyChart" style="height: 600px"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const topologyChart = ref()

onMounted(() => {
  const chart = echarts.init(topologyChart.value)
  chart.setOption({
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      roam: true,
      label: { show: true, position: 'right' },
      force: { repulsion: 300, edgeLength: 100 },
      data: [
        { name: 'Order Service', category: 0, symbolSize: 50 },
        { name: 'Inventory Service', category: 0, symbolSize: 40 },
        { name: 'Redis Cluster', category: 1, symbolSize: 45 },
        { name: 'MySQL Primary', category: 2, symbolSize: 50 },
        { name: 'Nginx LB', category: 3, symbolSize: 40 },
      ],
      links: [
        { source: 'Order Service', target: 'Inventory Service' },
        { source: 'Order Service', target: 'Redis Cluster' },
        { source: 'Order Service', target: 'MySQL Primary' },
        { source: 'Nginx LB', target: 'Order Service' },
      ],
      categories: [
        { name: 'Service' },
        { name: 'Cache' },
        { name: 'Database' },
        { name: 'Gateway' },
      ],
      lineStyle: { color: '#aaa', curveness: 0.3 },
    }],
  })
})
</script>
