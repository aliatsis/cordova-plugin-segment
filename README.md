#cordova-plugin-segment

##WARNING: This project is no longer maintained by the author.

Cordova plugin for Segment Analytics.

Supports Segment's iOS and Android SDKs.

##Usage
Implements (mostly) the same API interface on `window.analytics` as [Analytics.js][].

##Segment Write Keys
In config.xml, you can put the following preferences:
* \<preference name="analytics_write_key" value="{Segment write key}" />
* \<preference name="analytics_debug_write_key" value="{Segment write key}" />

##iOS Integrations Setup
Use CocoaPods:
The default plugin configuration does not bundle any Segment integrations or core analytics SDKs.
To add more your custom integrations, create a `Podfile` file in your iOS platform root directory and add your segment integration dependencies. See the [iOS Quickstart][] for examples.

##Android Integrations Setup
Use Gradle:
By default, the plugin builds with the `analytics-core` SDK for Android.
To add more your custom integrations, create a `build-extras.gradle` file in your Android platform root directory and add your segment integration dependencies. See the [Android Custom Build Docs][] for examples.

[Analytics.js]: https://segment.io/docs/libraries/analytics.js
[iOS Quickstart]: https://segment.com/docs/libraries/ios/quickstart/
[Android Custom Build Docs]: https://segment.com/docs/libraries/android/#custom-builds
