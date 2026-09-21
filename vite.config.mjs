/* eslint-disable node/prefer-global/process */
import { readFileSync } from 'node:fs'
import { fileURLToPath, URL } from 'node:url'
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite'
import vue from '@vitejs/plugin-vue'
import { defineConfig, loadEnv } from 'vite'
import { parseString } from 'xml2js'

export default ({ mode }) => {
  process.env = { ...process.env, ...loadEnv(mode, process.cwd()) }

  const backVersion = () => {
    let version
    const pomXml = readFileSync('./pom.xml', 'utf8')
    parseString(pomXml, (err, result) => {
      if (err)
        console.error(err)
      else version = result.project.version[0]
    })

    return version
  }

  process.env.VITE_BACK_VERSION = backVersion()

  return defineConfig({
    base: `${process.env.VITE_BACK_BASE_URL}ui`,
    root: './src/main/webapp',
    envDir: '../../../',
    plugins: [
      vue({
        template: {
          compilerOptions: {
            isCustomElement: tag => tag.includes('esup-'),
          },
        },
      }),
      VueI18nPlugin(),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src/main/webapp/src', import.meta.url)),
      },
    },
    server: {
      port: 3000,
      proxy: {
        '^(?:/[^/]*)?/(?=api|management|api-docs|published|feed|app|files|view)': process.env.VITE_PROXY_URL,
      },
    },
  })
}
