'use strict';

var gulp = require('gulp');
var htmllint = require('gulp-htmllint');
var gutil = require('gulp-util');

gulp.task('htmllint:test', function() {
  return gulp.src('./src/**/*.html')
    .pipe(htmllint({}, htmllintReporter));
});

function htmllintReporter(filepath, issues, test) {
  if (issues.length > 0) {
    issues.forEach(function (issue) {
      gutil.log(gutil.colors.cyan('[gulp-htmllint] ') + gutil.colors.white(filepath + ' [' + issue.line + ',' + issue.column + ']: ') + gutil.colors.red('(' + issue.code + ') ' + issue.msg));
    });

    gutil.log(gutil.colors.cyan('[gulp-htmllint] Total number of issues with this file: ' + issues.length));
    process.exitCode = 1;
  }
}
