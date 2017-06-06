var gulp = require('gulp'),
    uglify = require('gulp-uglify'),
    del = require('del'),
    sass = require('gulp-ruby-sass'),
    autoprefixer = require('gulp-autoprefixer'),
    concat = require('gulp-concat'),
    jshint = require('gulp-jshint'),
    minifyCss = require("gulp-minify-css"),
    concatCss = require('gulp-concat-css'),
    rev = require('gulp-rev'),       //- 对文件名加MD5后缀
    revCollector = require('gulp-rev-collector'), //- 路径替换
    rename = require('gulp-rename');

//视图 引入 js css的模板文件 目录
var commonResDir = '../views/common';

var paths = {
    baseTarget: './dist',
    targetJs: './dist/js', //生成的js文件目录
    targetCss: './dist/css', //生成css文件目录
    sourceJs: ['js/**/*.js'], //需要打包的js目录下的所有js
    sourceCss: './css/**.css' //需要打包的css目录下的所有css
};

//清除 已经生成的文件
gulp.task('clean', function(cb) {
    return del(['dist/js/**', 'dist/css/**'], cb);
});

//将文件里面 需要替代的版本的路径 替代
gulp.task('rev', function() {
    gulp.src(['./rev/*.json', './src/*.html'])      //- 读取 rev-manifest.json 文件以及需要进行css名替换的文件
        .pipe(revCollector())                       //- 执行文件内css名的替换
        .pipe(gulp.dest(paths.baseTarget));                //- 替换后的文件输出的目录
});

gulp.task('distCss', function () {
  return gulp.src(paths.sourceCss) // 要压缩的css文件
    .pipe(concatCss('dist.min.css'))
    .pipe(minifyCss()) //压缩css
    .pipe(rev())
    .pipe(gulp.dest(paths.targetCss))
    .pipe(rev.manifest())                                   //- 生成一个rev-manifest.json
    .pipe(gulp.dest('./rev/css'));
});

gulp.task('distJs', function() {
  return gulp.src(paths.sourceJs)
    .pipe(jshint())
    .pipe(jshint.reporter('default'))
    .pipe(uglify()) //压缩js
    .pipe(concat('dist.min.js'))
    .pipe(rev())
    .pipe(gulp.dest(paths.targetJs))
    .pipe(rev.manifest())                                   //- 生成一个rev-manifest.json
    .pipe(gulp.dest('./rev/js'));
});


//发布 js css 带版本号
gulp.task('deploy', ['clean','distCss', 'distJs'], function() {
    gulp.src(['./rev/**/*.json', './src/dist/*.html'])          //- 读取 rev-manifest.json 文件以及需要进行css名替换的文件
        .pipe(revCollector())                                   //- 执行文件内css名的替换
        .pipe(gulp.dest(commonResDir));                //- 替换后的文件输出的目录
});


/*****************开发*****************/
var devPath = {
    baseTarget: 'dev',
    targetJs: 'dev/js', //生成的js文件目录
    targetCss: 'dev/css', //生成css文件目录
    sourceJs: ['js/**/*.js'], //需要打包的js目录下的所有js 排除common js
    sourceCss: 'css/**.css' //需要打包的css目录下的所有css
};
gulp.task('devJs', function() {
  return gulp.src(devPath.sourceJs)
    .pipe(jshint())
    .pipe(jshint.reporter('default'))
    .pipe(concat('dev.js'))
    .pipe(gulp.dest(devPath.targetJs));
});

gulp.task('devCss', function () {
  return gulp.src(devPath.sourceCss) // 要压缩的css文件
    .pipe(concatCss('dev.css'))
    .pipe(gulp.dest(devPath.targetCss));
});


//开发环境不用使用版本号
gulp.task('devCopy', function () {
    gulp.src(['./src/dev/*.html'])
        .pipe(gulp.dest(commonResDir));
});

//默认是开发模式
gulp.task('default', ['devCopy', 'devCss', 'devJs'], function () {
    gulp.watch(devPath.sourceCss, ['devCss']);
    gulp.watch(devPath.sourceJs, ['devJs']);
});
