(function(global) {
  var Segment = function() {};

  Segment.prototype.initSdk = function(args) {
    var self = this;
    cordova.exec(
      function(conversionData) {
        self.onInstallConversionDataLoaded(conversionData);
      }, null, "SegmentPlugin", "initSdk", args);
  };

  Segment.prototype.setCurrencyCode = function(currencyId) {
    cordova.exec(null, null, "SegmentPlugin", "setCurrencyCode", [currencyId]);
  };

  Segment.prototype.setCustomerUserId = function(customerUserId) {
    cordova.exec(null, null, "SegmentPlugin", "setCustomerUserId", [customerUserId]);
  };

  Segment.prototype.setUserEmails = function(emails, cryptMethod) {
    cordova.exec(null, null, "SegmentPlugin", "setUserEmails", [emails, cryptMethod]);
  };

  Segment.prototype.setMeasureSessionDuration = function(measureSessionDuration) {
    cordova.exec(null, null, "SegmentPlugin", "setMeasureSessionDuration", [!!measureSessionDuration]);
  };

  Segment.prototype.setDeviceTrackingDisabled = function(disabled) {
    cordova.exec(null, null, "SegmentPlugin", "setDeviceTrackingDisabled", [!!disabled]);
  };

  Segment.prototype.disableAppleAdSupportTracking = function(disabled) {
    cordova.exec(null, null, "SegmentPlugin", "disableAppleAdSupportTracking", [!!disabled]);
  };

  Segment.prototype.getSegmentUID = function(callbackFn) {
    cordova.exec(function(result) {
        callbackFn(result);
      }, null,
      "SegmentPlugin",
      "getSegmentUID", []);
  };

  Segment.prototype.trackEvent = function(eventName, eventValues) {
    cordova.exec(null, null, "SegmentPlugin", "trackEvent", [eventName, eventValues]);
  };

  Segment.prototype.onInstallConversionDataLoaded = function(conversionData) {
    var data = conversionData;
    if (typeof data === "string") {
      data = JSON.parse(conversionData);
    }
    global.Segment._conversionData = data;

    if (global.Segment._cbFuncConversionData) {
      global.Segment._cbFuncConversionData(global.Segment._conversionData);
    }
  };

  Segment.prototype.setCallbackConversionData = function(callbackFn) {
    global.Segment._cbFuncConversionData = callbackFn;

    if (global.Segment._conversionData) {
      global.Segment._cbFuncConversionData(global.Segment._conversionData);
    }
  };

  global.cordova.addConstructor(function() {
    if (!global.Cordova) {
      global.Cordova = global.cordova;
    };

    if (!global.plugins) {
      global.plugins = {};
    }

    global.Segment = new Segment();
  });
}(window));