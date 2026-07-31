import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, GraphChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  MarkLineComponent,
} from 'echarts/components'
import VChart from 'vue-echarts'
import type { App } from 'vue'

use([
  CanvasRenderer,
  LineChart,
  PieChart,
  GraphChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  MarkLineComponent,
])

export function setupEcharts(app: App) {
  app.component('VChart', VChart)
}
