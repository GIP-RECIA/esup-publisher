import { defineConfig } from '@playwright/test'

export default defineConfig({
  testDir: './src/test/javascript/e2e',
  use: {
    baseURL: 'http://127.0.0.1:3000/publisher/ui/',
    headless: true,
  },
  webServer: {
    command: 'npm run dev -- --host 127.0.0.1',
    url: 'http://127.0.0.1:3000/publisher/ui/__e2e/richtext',
    env: { VITE_E2E: 'true' },
    reuseExistingServer: !process.env.CI,
  },
})
