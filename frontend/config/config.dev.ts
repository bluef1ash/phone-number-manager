import { defineConfig } from 'umi';

export default defineConfig({
  devtool: 'eval-source-map',
  devServer: {
    port: 80,
  },
  plugins: ['react-dev-inspector/plugins/umi/react-inspector'],
  inspectorConfig: {
    exclude: [],
    babelPlugins: [],
    babelOptions: {},
  },
});
