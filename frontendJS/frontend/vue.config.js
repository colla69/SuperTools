
module.exports = {
  publicPath: process.env.NODE_ENV === 'production' ? '/sigma-vue' : '/',
  devServer: {
    disableHostCheck: true,
    proxy: {
      "/backend": {
        target: "https://dash.colarietitosti.info",
        changeOrigin: true
      },
      "/visitors": {
        target: "https://cv.colarietitosti.info",
        changeOrigin: true
      },
    }
  }
};