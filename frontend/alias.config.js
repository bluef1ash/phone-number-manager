const path = require('path')

module.exports = {
  resolve: {
    alias: {
      '@': path.resolve(__dirname),
      '@library': path.resolve(__dirname, '../', 'library')
    }
  }
}
