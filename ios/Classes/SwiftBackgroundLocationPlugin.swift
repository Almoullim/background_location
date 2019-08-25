import Flutter
import UIKit
import CoreLocation

public class SwiftBackgroundLocationPlugin: NSObject, FlutterPlugin, CLLocationManagerDelegate {
    static var locationManager: CLLocationManager?
    static var channel: FlutterMethodChannel?
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let instance = SwiftBackgroundLocationPlugin()
        
        SwiftBackgroundLocationPlugin.channel = FlutterMethodChannel(name: "almoullim.com/background_location", binaryMessenger: registrar.messenger())
        registrar.addMethodCallDelegate(instance, channel: SwiftBackgroundLocationPlugin.channel!)
        SwiftBackgroundLocationPlugin.channel?.setMethodCallHandler(instance.handle)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        SwiftBackgroundLocationPlugin.locationManager = CLLocationManager()
        SwiftBackgroundLocationPlugin.locationManager?.delegate = self
        SwiftBackgroundLocationPlugin.locationManager?.requestAlwaysAuthorization()

        SwiftBackgroundLocationPlugin.locationManager?.allowsBackgroundLocationUpdates = true
        SwiftBackgroundLocationPlugin.locationManager?.pausesLocationUpdatesAutomatically = false

        SwiftBackgroundLocationPlugin.channel?.invokeMethod("location", arguments: "method")

        if (call.method == "start_location_service") {
            SwiftBackgroundLocationPlugin.channel?.invokeMethod("location", arguments: "start_location_service")            
            SwiftBackgroundLocationPlugin.locationManager?.startUpdatingLocation() 
        } else if (call.method == "stop_location_service") {
            SwiftBackgroundLocationPlugin.channel?.invokeMethod("location", arguments: "stop_location_service")
            SwiftBackgroundLocationPlugin.locationManager?.stopUpdatingLocation()
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

        SwiftBackgroundLocationPlugin.channel?.invokeMethod("location", arguments: location)
    }
}
