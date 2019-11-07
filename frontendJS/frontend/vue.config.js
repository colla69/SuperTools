
module.exports = {
  "publicPath": process.env.NODE_ENV === 'production' ? '/sigma-vue' : '/',
  devServer: {
    proxy: {
      "/dashApps": {
        target: 'https://192.168.1.247:8443',

        changeOrigin: true
      }
    }
  }
};