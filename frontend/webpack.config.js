
module.exports = {
    devServer: {
        allowedHosts: [
            'localhost:8443',
        ],
        disableHostCheck: true,
        proxy: {
            "/backend": {
                target: "https://localhost:8443",
                changeOrigin: true
            },
            "/visitors": {
                target: "https://cv.colarietitosti.info",
                changeOrigin: true
            },
        }
    }
};