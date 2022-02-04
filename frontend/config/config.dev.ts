import { defineConfig } from 'umi';

export default defineConfig({
  devtool: 'eval-source-map',
  plugins: ['react-dev-inspector/plugins/umi/react-inspector'],
  inspectorConfig: {
    exclude: [],
    babelPlugins: [],
    babelOptions: {},
  },
});
