import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Proxy the backend routes to Spring during development so the SPA can use
    // relative (same-origin) URLs exactly like in the production single image.
    proxy: {
      '/api': 'http://localhost:8080',
      '/snapshots': 'http://localhost:8080',
    },
  },
})
