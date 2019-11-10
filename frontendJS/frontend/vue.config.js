
module.exports = {
  "publicPath": process.env.NODE_ENV === 'production' ? '/sigma-vue' : '/',
  devServer: {
    proxy: {
      "/dashApps": {
        target: 'https://dash.colarietitosti.info/backend',
        changeOrigin: true
      },
      "/visitors": {
        target: 'https://cv.colarietitosti.info',
        changeOrigin: true
      },
    }
  }
};