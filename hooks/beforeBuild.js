module.exports = function(ctx) {
  var fs = ctx.requireCordovaModule('fs');
  var path = ctx.requireCordovaModule('path');

  var platformRoot = path.join(ctx.opts.projectRoot, 'platforms/android');
  var fileName = 'analyticsIntegrations.gradle';

  var analyticsIntegrations;

  try {
    analyticsIntegrations = fs.readFileSync(path.join(platformRoot, fileName));
  } catch (e) {
    console.error(e);
  }

  fs.writeFileSync(path.join(platformRoot, fileName), analyticsIntegrations || '');
};