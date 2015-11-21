module.exports = function(ctx) {
  if (ctx.opts.platforms.indexOf('android') < 0) {
    return;
  }

  var fs = ctx.requireCordovaModule('fs');
  var path = ctx.requireCordovaModule('path');

  var platformRoot = path.join(ctx.opts.projectRoot, 'platforms/android');
  var fileName = 'analyticsIntegrations.gradle';

  var analyticsIntegrations;

  try {
    analyticsIntegrations = fs.readFileSync(path.join(platformRoot, fileName));
    fs.writeFileSync(path.join(__dirname, '../src/android', fileName), analyticsIntegrations);
  } catch (e) {
    console.error(e);
  }
};