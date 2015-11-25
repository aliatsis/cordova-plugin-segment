#import "AnalyticsPlugin.h"
#import <Cordova/CDV.h>
#import <Analytics.h>

@implementation AnalyticsPlugin

- (void)pluginInitialize
{
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(finishLaunching:) name:UIApplicationDidFinishLaunchingNotification object:nil];
}

- (void)finishLaunching:(NSNotification *)notification
{
    NSString* writeKeyPreferenceName;
    NSString* writeKeyPListName;
    
    //Get app credentials from config.xml or the info.plist if they can't be found
    #ifdef DEBUG
        [SEGAnalytics debug:YES];
        writeKeyPreferenceName = @"analytics_debug_write_key";
        writeKeyPListName = @"AnalyticsDebugWriteKey";
    #else
        [SEGAnalytics debug:NO];
        writeKeyPreferenceName = @"analytics_write_key";
        writeKeyPListName = @"AnalyticsWriteKey";
    #endif
    
    NSString* writeKey = self.commandDelegate.settings[writeKeyPreferenceName] ?: [[NSBundle mainBundle] objectForInfoDictionaryKey:writeKeyPListName];
    
    if (writeKey.length) {
        NSString* useLocationServices = self.commandDelegate.settings[@"analytics_use_location_services"] ?: [[NSBundle mainBundle] objectForInfoDictionaryKey:@"AnalyticsUserLocationServices"]

        SEGAnalyticsConfiguration *configuration = [SEGAnalyticsConfiguration configurationWithWriteKey:writeKey];
        configuration.shouldUseLocationServices = [useLocationServices boolValue];
        [SEGAnalytics setupWithConfiguration:configuration];
    } else {
        NSLog([NSString stringWithFormat:@"[cordova-plugin-segment] ERROR - Invalid write key: %@", writeKey]);
    }
}

- (void)identify:(CDVInvokedUrlCommand*)command
{
    NSString* userId = command.arguments[0];
    NSDictionary* traits = command.arguments[1];
    
    [[SEGAnalytics sharedAnalytics] identify:userId traits:traits];
}

- (void)group:(CDVInvokedUrlCommand*)command
{
    NSString* groupId = command.arguments[0];
    NSDictionary* traits = command.arguments[1];
    
    [[SEGAnalytics sharedAnalytics] group:groupId traits:traits];
}

- (void)track:(CDVInvokedUrlCommand*)command
{
    NSString* event = command.arguments[0];
    NSDictionary* properties = command.arguments[1];
    
    [[SEGAnalytics sharedAnalytics] track:event properties:properties];
}

- (void)screen:(CDVInvokedUrlCommand*)command
{
    NSString* name = command.arguments[0];
    NSDictionary* properties = command.arguments[1];
    
    [[SEGAnalytics sharedAnalytics] screen:name properties:properties];
}

- (void)alias:(CDVInvokedUrlCommand*)command
{
    NSString* newId = command.arguments[0];
    
    [[SEGAnalytics sharedAnalytics] alias:newId];
}

- (void)reset:(CDVInvokedUrlCommand*)command
{
    [[SEGAnalytics sharedAnalytics] reset];
}

- (void)flush:(CDVInvokedUrlCommand*)command
{
    [[SEGAnalytics sharedAnalytics] flush];
}

- (void)enable:(CDVInvokedUrlCommand*)command
{
    [[SEGAnalytics sharedAnalytics] enable];
}

- (void)disable:(CDVInvokedUrlCommand*)command
{
    [[SEGAnalytics sharedAnalytics] disable];
}

@end