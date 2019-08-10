#import "BackgeoundLocationPlugin.h"
#import <backgeound_location/backgeound_location-Swift.h>

@implementation BackgeoundLocationPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftBackgeoundLocationPlugin registerWithRegistrar:registrar];
}
@end
