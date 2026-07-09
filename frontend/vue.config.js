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
  port: 8081,  // ✅ port 移到外面
  client: {
    // ❌ 删除 port: 5173
    overlay: false,  // 取消编译错误全屏覆盖
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
