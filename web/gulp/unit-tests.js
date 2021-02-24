'use strict';

var path = require('path');
var gulp = require('gulp');
var conf = require('./conf');

var ts = require('gulp-typescript');
var karma = require('karma');

var pathSrcHtml = [
  path.join(conf.paths.src, '/**/*.html')
];

var pathSrcJs = [
  path.join(conf.paths.tmp, '/serve/app/index.module.js')
];

function runTests (singleRun, done) {
  var reporters = ['progress'];
  var preprocessors = {};

  pathSrcHtml.forEach(function(path) {
    preprocessors[path] = ['ng-html2js'];
  });

  // remove coverage report as not working and causing disconnect issues
  /*if (singleRun) {
    pathSrcJs.forEach(function(path) {
      preprocessors[path] = ['coverage'];
    });
    reporters.push('coverage');
  }*/

  var localConfig = {
    configFile: path.join(__dirname, '/../karma.conf.js'),
    singleRun: singleRun,
    autoWatch: !singleRun,
    reporters: reporters,
    preprocessors: preprocessors
  };

  var server = new karma.Server(localConfig, function(failCount) {
    done(failCount ? new Error("Failed " + failCount + " tests.") : null);
  })
  server.start();
}

// typescript:test will fail and crash gulp if an error occurs
gulp.task('typescript:test', function() {
  var gutil = require('gulp-util');

  // to update to es6, understand this https://github.com/DefinitelyTyped/DefinitelyTyped/issues/11700#issuecomment-257951878
  var tsProject = ts.createProject('tsconfig.json');

  return gulp.src(
      [
        './src/**/*.ts',
        './typings/**/*.ts',
        '!./**/node_modules/**/*.ts',
        '!./**/typescript/**/*.ts'
      ]
    )
    .pipe(tsProject())
    .on('error', function (error) {
      var log = gutil.log, colors = gutil.colors;
      log('Typescript compilation exited with ' + colors.red(error));
      log(colors.red('There may be more Typescript errors as well'));
      process.exit(1);
    });
});

gulp.task('test', ['scripts:test'], function(done) {
  runTests(true, done);
});

gulp.task('test:auto', ['scripts:test-watch'], function(done) {
  runTests(false, done);
});
