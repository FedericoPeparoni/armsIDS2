'use strict';

var path = require('path');
var gulp = require('gulp');
var conf = require('./conf');
var fs = require("fs");
var $ = require('gulp-load-plugins')();
var wiredep = require('wiredep').stream;
var _ = require('lodash');
var browserSync = require('browser-sync');

gulp.task('inject-reload', ['inject'], function () {
  browserSync.reload();
});

gulp.task('inject', ['scripts', 'styles'], function () {
  var injectStyles = gulp.src([
    path.join(conf.paths.tmp, '/serve/app/**/*.css'),
    path.join('!' + conf.paths.tmp, '/serve/app/vendor.css')
  ], { read: false });

  var injectScripts = gulp.src([
    path.join(conf.paths.tmp, '/serve/app/**/*.module.js')
  ], { read: false });

  var injectOptions = {
    ignorePath: [conf.paths.src, path.join(conf.paths.tmp, '/serve')],
    addRootSlash: false
  };

  return gulp.src(path.join(conf.paths.src, '/*.html'))
    .pipe($.inject(injectStyles, injectOptions))
    .pipe($.inject(injectScripts, injectOptions))
    .pipe(wiredep(_.extend({}, conf.wiredep)))
    .pipe(gulp.dest(path.join(conf.paths.tmp, '/serve')));
});

// to invoke: gulp makeunique --input <filename> --output <filename> --oaert frontend|backend
gulp.task('translate', function () {
  const tokens = JSON.parse(fs.readFileSync('./po/' + process.argv[4] + '.json'));
  const fname = process.argv[6];
  const part = process.argv[8];

  fs.readFile(`./src/app/angular-ids-project/src/components/services/config/config.service.ts`, 'utf-8', (err, data) => {
    const yandex_key = data.substring(data.indexOf('YANDEX_API') + 14, data.indexOf('YANDEX_API') + 98);

    translate_text(tokens, fname, part, yandex_key, []);
  });
});

function translate_text(tokens, fname, part, yandex_key, output) {
  const token = tokens.shift();
  const translate = require('yandex-translate')(yandex_key);

  translate.translate(token, { to: 'es' }, function (err, res) {
    if (token) {
      let en_obj = {
        token: token,
        code: "en",
        val: token,
        created_by: "system",
        part: part
      };

      let es_obj = {
        token: token,
        code: "es",
        val: (res ? res.text[0] : err),
        created_by: "system",
        part: part
      };

      output.push(en_obj, es_obj);

      console.log(tokens.length);

      translate_text(tokens, fname, part, yandex_key, output)
    } else {
      write_translations(output, fname);
    };
  });
};

function write_translations(output, fname) {
  fs.writeFile('./po/' + fname + '.json', JSON.stringify(output));
}

// to invoke: gulp makeunique --input <filename>
gulp.task('testfordups', function () {
  const tokens = JSON.parse(fs.readFileSync('./po/' + process.argv[4] + '.json'));
  const names = {};

  for (let i = 0, len = tokens.length; i < len; i++) {
    if (!names[tokens[i].token]) {
      names[tokens[i].token] = 1;
    } else {
      names[tokens[i].token] = names[tokens[i].token] + 1;     
    }
  }

  for (var key in names) {
    if (names[key] !==2 && names[key] !== 4 ) {
      console.log(key, names[key]);
    }
  }  
});

// to invoke: gulp makeunique --input <filename> --output <filename>
gulp.task('makeunique', function () {
  const tokens = JSON.parse(fs.readFileSync('./po/' + process.argv[4] + '.json'));
  const output = [];

  for (let i = 0, len = tokens.length; i < len; i++) {
    let found = false;

    for (let j = 0, len = output.length; j < len; j++) {
      
      if (output[j].token === tokens[i].token && output[j].code === tokens[i].code && output[j].part === tokens[i].part ) {
        found = true;
        break;
      }
    }

    if (!found) {
      output.push(tokens[i]);
    }

  }

  write_translations(output, process.argv[6]);
});