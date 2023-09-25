const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  pages:{
    index:{
      entry: "src/main.js",
      title:"極旬ログポース",
    }
  },
  devServer: {
    port: 8080,
  },
  transpileDependencies: true
})
