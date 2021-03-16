var path = require('path');
var express = require('express');

var app = express();

var staticPath = path.join(__dirname, '/src');
app.use(express.static(staticPath));
app.use('/scripts', express.static(__dirname + '/node_modules/'));

app.listen(3000, function() {
  console.log('listening');
});