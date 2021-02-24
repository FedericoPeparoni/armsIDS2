'use strict';

var gulp = require('gulp'),
  fs = require('fs'),
  dotenv = require('dotenv').config(),
  gutil = require('gulp-util');

var npm_package = require('../package.json');

gulp.task('serve:config', ['clean', 'config:inject'], function() {
  gulp.start('serve');
});

gulp.task('serve:dist:config', ['clean', 'config:inject'], function() {
  gulp.start('serve:dist');
});

gulp.task('build:config', ['clean', 'config:inject'], function() {
  gulp.start('build');
});

// Creates the Config File
gulp.task('config:inject', function() {

  var config = {};

  let mandatoryFields = ['API_HOST', 'AUTH_TOKEN', 'YANDEX_API'];
  let optionalFields = ['INIT_ZOOM', 'BOUNDING_BOX', 'BASE_PROVIDER', 'BASE_LOCAL_URL', 'SCENE_MODE', 'CESIUM_KEY', 'GEOSERVER_NAVDB', 'GEOSERVER_ABMSDB', 'GEOSERVER_BASE_PT1'];

  gutil.log("Collecting mandatory environment vars...");
  for (let i = 0; i < mandatoryFields.length; i++) { // append the mandatory environment variables to the config
    if (process.env[mandatoryFields[i]]) {
      config[mandatoryFields[i]] = process.env[mandatoryFields[i]];
      gutil.log("  " + mandatoryFields[i] + " = " + process.env[mandatoryFields[i]]);
    } else {
      throw(`ERROR: the following Environment parameter is missing: ${mandatoryFields[i]}`);
    }
  }

  gutil.log("Collecting optional environment vars...");
  for (let i = 0; i < optionalFields.length; i++) { // append the optional environment variables to the config
    if (process.env[optionalFields[i]]) {
      config[optionalFields[i]] = process.env[optionalFields[i]];
      gutil.log("  " + optionalFields[i] + " = " + process.env[optionalFields[i]]);
    }
  }

  gutil.log("Collecting build date and version...");
  config.BUILD_DATE = new Date();
  gutil.log("  BUILD_DATE = " + config.BUILD_DATE);
  config.BUILD_VERSION = npm_package.version || 'dev';
  gutil.log("  BUILD_VERSION = " + config.BUILD_VERSION);

  var sourceDir = './src/app/angular-ids-project/src/components/services/config';

  fs.readFile(`${sourceDir}/config.default`, 'utf-8', (err, data) => {

    var stringToLookFor = 'private config: Object = {};';

    var index = data.indexOf(stringToLookFor);

    var configStr = JSON.stringify(config);

    configStr = configStr
      .replace(/\"\:/g, '": ')
      .replace(/\'/g, "\'")
      .replace(/\"/g, "'")
      .replace(/\'\,\'/g, "', '"); // to make typescript happy

    var output = [ data.slice(0, index), `private config: Object = ${configStr};`, data.slice(index + stringToLookFor.length) ].join('');

    fs.writeFile(`${sourceDir}/config.service.ts`, output);
  });
});
