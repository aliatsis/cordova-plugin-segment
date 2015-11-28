var exec = require('cordova/exec');

var analytics = {};

analytics.identify = function() {
  var args = Array.prototype.slice.call(arguments);

  if (typeof args[0] !== 'string') {
    args.unshift(null);
  }

  exec(null, null, 'AnalyticsPlugin', 'identify', getNArgs(args, 3));
};

analytics.group = function() {
  var args = Array.prototype.slice.call(arguments);

  if (typeof args[0] !== 'string') {
    args.unshift(null);
  }

  exec(null, null, 'AnalyticsPlugin', 'group', getNArgs(args, 3));
};

analytics.track = function() {
  var args = Array.prototype.slice.call(arguments);

  exec(null, null, 'AnalyticsPlugin', 'track', getNArgs(args, 3));
};


// alias `screen` as `page` for consistent with Analytics.js interface
analytics.screen = analytics.page = function() {
  var args = Array.prototype.slice.call(arguments);

  if (typeof args[1] !== 'string') {
    args.unshift(null);
  }

  exec(null, null, 'AnalyticsPlugin', 'screen', getNArgs(args, 4));
};

analytics.alias = function() {
  var args = Array.prototype.slice.call(arguments);

  exec(null, null, 'AnalyticsPlugin', 'alias', getNArgs(args, 2));
};

analytics.reset = function() {
  exec(null, null, 'AnalyticsPlugin', 'reset', []);
};

analytics.flush = function() {
  exec(null, null, 'AnalyticsPlugin', 'flush', []);
};

// iOS only
analytics.enable = function() {
  exec(null, null, 'AnalyticsPlugin', 'enable', []);
};

// iOS only
analytics.disable = function() {
  exec(null, null, 'AnalyticsPlugin', 'disable', []);
};

// android only
analytics.getSnapshot = function(callbackFn) {
  exec(function(result) {
    callbackFn(result);
  }, null, 'AnalyticsPlugin', 'getSnapshot', []);
};

function getNArgs(args, n) {
  var result = [];
  args = args || [];

  for (var i = 0; i < n; i++) {
    result[i] = args[i] === undefined ? null : args[i];
  }

  return result;
}

module.exports = analytics;