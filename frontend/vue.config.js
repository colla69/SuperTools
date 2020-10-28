
module.exports = {
  publicPath: '/',
  devServer: {
    disableHostCheck: true,
    proxy: {
      "/backend": {
        target: "https://localhost:8443",
        pathRewrite: { '^/backend': '' },
        changeOrigin: true
      },
      "/mangas": {
        target: "https://www.mangareader.net/",
        pathRewrite: { '^/mangas': '' },
        changeOrigin: true
      },
    }
  }
};