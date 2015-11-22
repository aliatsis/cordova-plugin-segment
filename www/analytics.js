var exec = require('cordova/exec');

var analytics = {};

analytics.identify = function() {
  var args = Array.prototype.slice.call(arguments);
  args.length = 3;

  if (typeof args[0] !== 'string') {
    args.unshift(null);
    args = args.slice(0, 3);
  }

  exec(null, null, "AnalyticsPlugin", "identify", args);
};

analytics.group = function() {
  var args = Array.prototype.slice.call(arguments);
  args.length = 3;

  exec(null, null, "AnalyticsPlugin", "group", args);
};

analytics.track = function() {
  var args = Array.prototype.slice.call(arguments);
  args.length = 3;

  exec(null, null, "AnalyticsPlugin", "track", args);
};


// alias `screen` as `page` for consistent with Analytics.js interface
analytics.screen = analytics.page = function() {
  var args = Array.prototype.slice.call(arguments);
  args.length = 4;

  if (typeof args[1] !== 'string') {
    args.unshift(null);
    args = args.slice(0, 4);
  }

  exec(null, null, "AnalyticsPlugin", "screen", args);
};

analytics.alias = function() {
  var args = Array.prototype.slice.call(arguments);
  args.length = 2;

  exec(null, null, "AnalyticsPlugin", "alias", args);
};

analytics.reset = function() {
  exec(null, null, "AnalyticsPlugin", "reset", []);
};

analytics.flush = function() {
  exec(null, null, "AnalyticsPlugin", "flush", []);
};

analytics.getSnapshot = function(callbackFn) {
  exec(function(result) {
    callbackFn(result);
  }, null, "AnalyticsPlugin", "getSnapshot", []);
};

module.exports = analytics;