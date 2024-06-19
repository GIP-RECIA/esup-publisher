import { fileURLToPath, URL } from 'node:url';

import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite';
import ckeditor5 from '@ckeditor/vite-plugin-ckeditor5';

import { createRequire } from 'node:module';
const require = createRequire(import.meta.url);

export default ({ mode }) => {
  process.env = { ...process.env, ...loadEnv(mode, process.cwd()) };

  return defineConfig({
    base: process.env.VITE_BACK_BASE_URL + 'ui',
    root: './src/main/webapp',
    envDir: '../../../',
    plugins: [
      vue({
        template: {
          compilerOptions: {
            isCustomElement: (tag) => tag.includes('esup-'),
          },
        },
      }),
      VueI18nPlugin(),
      ckeditor5({ theme: require.resolve('@ckeditor/ckeditor5-theme-lark') }),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src/main/webapp/src', import.meta.url)),
      },
    },
    server: {
      port: 3000,
      proxy: {
        '^(?:/[^/]*)?/api': process.env.VITE_PROXY_URL,
        '^(?:/[^/]*)?/management': process.env.VITE_PROXY_URL,
        '^(?:/[^/]*)?/api-docs': process.env.VITE_PROXY_URL,
        '^(?:/[^/]*)?/published': process.env.VITE_PROXY_URL,
        '^(?:/[^/]*)?/feed': process.env.VITE_PROXY_URL,
        '^(?:/[^/]*)?/app': process.env.VITE_PROXY_URL,
        '^(?:/[^/]*)?/files': process.env.VITE_PROXY_URL,
        '^(?:/[^/]*)?/view': process.env.VITE_PROXY_URL,
      },
    },
  });
};
