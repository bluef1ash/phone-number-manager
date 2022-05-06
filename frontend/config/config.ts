import { defineConfig } from 'umi';
import defaultSettings from './defaultSettings';
import { join } from 'path';
import routes from './routes';

export default defineConfig({
  hash: true,
  antd: {},
  dva: {
    hmr: true,
  },
  define: {
    ...process.env,
  },
  layout: {
    locale: true,
    siderWidth: 208,
    ...defaultSettings,
  },
  dynamicImport: {
    loading: '@ant-design/pro-layout/es/PageLoading',
  },
  targets: {
    ie: 11,
  },
  theme: {
    'primary-color': defaultSettings.primaryColor,
    'root-entry-name': 'default',
  },
  esbuild: {},
  title: false,
  ignoreMomentLocale: true,
  routes,
  manifest: {
    basePath: '/',
  },
  fastRefresh: {},
  openAPI: [
    {
      requestLibPath: "import { request } from 'umi'",
      schemaPath: process.env.REACT_APP_API_BASE_URL + '/v3/api-docs',
      projectName: 'swagger',
    },
  ],
  nodeModulesTransform: {
    type: 'none',
  },
  mfsu: {},
  webpack5: {},
  exportStatic: {},
  alias: {
    '@config': join(__dirname),
  },
  locale: {
    default: 'zh-CN',
  },
  access: {},
  scripts: [
    'https://unpkg.com/react@17/umd/react.production.min.js',
    'https://unpkg.com/react-dom@17/umd/react-dom.production.min.js',
    'https://unpkg.com/@ant-design/charts@1.0.5/dist/charts.min.js',
  ],
  externals: {
    react: 'React',
    'react-dom': 'ReactDOM',
    '@ant-design/charts': 'charts',
  },
});
