1. 安装nodejs npm
2. 安装 gulp： npm install --global gulp
3. 切换到static文件夹  
    执行 npm install 下载 package.json 里面的相关依赖
4. 打包命令  
    开发: gulp 或者执行 dev.bat  
    发布: gulp deploy 或者执行 deploy.bat
5. static 文件目录结构

目录 | 作用
---|---
BJUI | 存放BJUI的插件等的目录
js | BJUI的某些js插件需要使用该文件夹的js
dev | 开始时期 gulp打包的js css 目标文件夹 (gulp会自动生成)
dist | gulp 发布js css的目标文件夹 (gulp会自动生成)
images | 公共的图片存储位置
file | 需要下载的文件的文件夹
~~rev~~ | gulp 打包版本管理需要的文件夹 (gulp会自动生成)（git ignore 不用提交）
src | freemaker引入的公共js css的 文件 目录 备份
~~node_modules~~ | node 通过npm 下载的工具 （git ignore 不用提交）
**js** | js分模块开发 通过gulp可统一打包到 dev 或者 dist
**css** | 开发css的存放文件夹
gulpfile.js | 打包配置
package.json | 该工程依赖的打包工具
deploy.bat | 打包带版本号的js css
dev.bat | 开发时期，监听 js  css 变化，即时编译打包 js css到 dev目录下