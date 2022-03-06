import { defineConfig } from "umi";
import defaultSettings from "./defaultSettings";
import { join } from "path";
import routes from "./routes";

//const { BASE_URL } = process.env;
export default defineConfig({
  hash: true,
  antd: {},
  dva: {
    hmr: true,
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
  /*openAPI: [
    {
      requestLibPath: "import { request } from 'umi'",
      schemaPath: BASE_URL + '/v3/api-docs',
      projectName: 'swagger',
    },
  ],*/
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
});
