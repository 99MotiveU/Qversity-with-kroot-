import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react-swc'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  // PC에서 백엔드로 프록시할 주소 (모바일 IP 접속 시에도 유지)
  const proxyTarget = env.VITE_PROXY_TARGET || 'http://127.0.0.1:18080'
  const devPort = Number(env.VITE_DEV_PORT || 4000)
  const host =
    env.VITE_HOST === 'true' || env.VITE_HOST === '0.0.0.0'
      ? true
      : env.VITE_HOST || false

  return {
    plugins: [react(), tailwindcss()],
    // sockjs-client가 Node 전역 `global`을 참조함
    define: {
      global: 'globalThis',
    },
    server: {
      host,
      port: devPort,
      proxy: {
        '/api': {
          target: proxyTarget,
          changeOrigin: true,
        },
        '/oauth2': {
          target: proxyTarget,
          changeOrigin: true,
        },
        '/login': {
          target: proxyTarget,
          changeOrigin: true,
        },
        '/ws': {
          target: proxyTarget,
          changeOrigin: true,
          ws: true,
        },
      },
    },
  }
})
