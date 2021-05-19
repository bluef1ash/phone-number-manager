const AntdDayjsWebpackPlugin = require('antd-dayjs-webpack-plugin')

module.exports = {
  future: {
    webpack5: true
  },
  webpack (config) {
    config.plugins.push(new AntdDayjsWebpackPlugin())
    config.resolve.extensions = ['.jsx', '.js', '.json', '.scss', '.ts', '.tsx']
    return config
  }
}
