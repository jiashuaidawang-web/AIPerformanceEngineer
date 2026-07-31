import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import path from 'path'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api/v1/resources': { target: 'http://localhost:8082', changeOrigin: true },
      '/api/v1/observations': { target: 'http://localhost:8083', changeOrigin: true },
      '/api/v1/relationships': { target: 'http://localhost:8084', changeOrigin: true },
      '/api/v1/topology': { target: 'http://localhost:8084', changeOrigin: true },
      '/api/v1/timelines': { target: 'http://localhost:8085', changeOrigin: true },
      '/api/v1/evidences': { target: 'http://localhost:8086', changeOrigin: true },
      '/api/v1/knowledge': { target: 'http://localhost:8087', changeOrigin: true },
      '/api/v1/recommendations': { target: 'http://localhost:8088', changeOrigin: true },
      '/api/v1/executions': { target: 'http://localhost:8089', changeOrigin: true },
      '/api/v1/alerts': { target: 'http://localhost:8090', changeOrigin: true },
    },
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    chunkSizeWarningLimit: 1500,
  },
})
