const { defineConfig } = require("@vue/cli-service");
const webpack = require("webpack"); //导入 webpack 模块

module.exports = {
  transpileDependencies: true,
  lintOnSave: false,
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  //在模块中加入
  configureWebpack: {
    plugins: [
      new webpack.ProvidePlugin({
        "window.Quill": "quill/dist/quill.js",
        Quill: "quill/dist/quill.js",
      }),
    ],
  },
  //配置开发服务器
  devServer: {
  port: 5173,
  client: {
    overlay: false,
  },
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      pathRewrite: { '^/api': '' },
    },
    '/test-api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      pathRewrite: { '^/test-api': '' },
    },
  },
},
  css: {
    loaderOptions: {
      sass: {
        // 这里的选项会传递给 sass-loader
      },
    },
  },
};
