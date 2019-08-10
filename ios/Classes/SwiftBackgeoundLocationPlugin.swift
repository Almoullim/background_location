import Flutter
import UIKit
import CoreLocation

public class SwiftBackgeoundLocationPlugin: NSObject, FlutterPlugin, CLLocationManagerDelegate {
    static var locationManager: CLLocationManager?
    static var channel: FlutterMethodChannel?
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let instance = SwiftBackgeoundLocationPlugin()
        
        SwiftBackgeoundLocationPlugin.channel = FlutterMethodChannel(name: "almoullim.com/background_location", binaryMessenger: registrar.messenger())
        registrar.addMethodCallDelegate(instance, channel: SwiftBackgeoundLocationPlugin.channel!)
        SwiftBackgeoundLocationPlugin.channel?.setMethodCallHandler(instance.handle)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        SwiftBackgeoundLocationPlugin.locationManager = CLLocationManager()
        SwiftBackgeoundLocationPlugin.locationManager?.delegate = self
        SwiftBackgeoundLocationPlugin.locationManager?.requestAlwaysAuthorization()
        SwiftBackgeoundLocationPlugin.channel?.invokeMethod("location", arguments: "method")

        if (call.method == "start_location_service") {
            SwiftBackgeoundLocationPlugin.channel?.invokeMethod("location", arguments: "start_location_service")            
            SwiftBackgeoundLocationPlugin.locationManager?.startUpdatingLocation() 
        } else if (call.method == "stop_location_service") {
            SwiftBackgeoundLocationPlugin.channel?.invokeMethod("location", arguments: "stop_location_service")
            SwiftBackgeoundLocationPlugin.locationManager?.stopUpdatingLocation()
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .authorizedAlways {
           
        }
    }
    
    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = [
            "speed": locations.last!.speed,
            "altitude": locations.last!.altitude,
            "latitude": locations.last!.coordinate.latitude,
            "longitude": locations.last!.coordinate.longitude,
            "accuracy": locations.last!.horizontalAccuracy,
            "bearing": locations.last!.course
        ] as [String : Any]

        SwiftBackgeoundLocationPlugin.channel?.invokeMethod("location", arguments: location)
    }
}
