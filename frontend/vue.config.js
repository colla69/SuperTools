
module.exports = {
  publicPath: '/',

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