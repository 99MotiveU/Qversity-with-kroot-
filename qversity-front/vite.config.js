import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react-swc'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const apiBaseUrl = env.VITE_API_BASE_URL || 'http://localhost:18080'
  const devPort = Number(env.VITE_DEV_PORT || 4000)

  return {
    plugins: [react(), tailwindcss()],
    server: {
      port: devPort,
      proxy: {
        '/api': {
          target: apiBaseUrl,
          changeOrigin: true,
        },
        '/oauth2': {
          target: apiBaseUrl,
          changeOrigin: true,
        },
        '/login': {
          target: apiBaseUrl,
          changeOrigin: true,
        },
        '/ws': {
          target: apiBaseUrl,
          changeOrigin: true,
          ws: true,
        },
      },
    },
  }
})
