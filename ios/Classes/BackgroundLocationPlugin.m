#import "BackgroundLocationPlugin.h"
#import <background_location_2/background_location_2-Swift.h>

@implementation BackgroundLocationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBackgroundLocationPlugin registerWithRegistrar:registrar];
}
@end
