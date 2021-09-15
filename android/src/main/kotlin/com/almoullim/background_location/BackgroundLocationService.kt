package com.almoullim.background_location

import android.Manifest
import io.flutter.plugin.common.MethodChannel

import io.flutter.plugin.common.BinaryMessenger
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

import io.flutter.plugin.common.MethodCall


class BackgroundLocationService: MethodChannel.MethodCallHandler {
    companion object {
        const val METHOD_CHANNEL_NAME = "${BackgroundLocationPlugin.PLUGIN_ID}/methods"
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34

        private var instance: BackgroundLocationService? = null

        /**
         * Requests the singleton instance of [BackgroundLocationService] or creates it,
         * if it does not yet exist.
         */
        fun getInstance(): BackgroundLocationService {
            if (instance == null) {
                instance = BackgroundLocationService()
            }
            return instance!!
        }
    }


    /**
     * Context that is set once attached to a FlutterEngine.
     * Context should no longer be referenced when detached.
     */
    private var context: Context? = null
    private var channel: MethodChannel? = null
    private var activity: Activity? = null
    private var isAttached = false
    private var receiver: MyReceiver? = null
    private var service: LocationUpdatesService? = null
    private var bound: Boolean = false

    fun onAttachedToEngine(context: Context, messenger: BinaryMessenger) {
        this.context = context
        isAttached = true
        channel = MethodChannel(messenger, METHOD_CHANNEL_NAME)
        channel!!.setMethodCallHandler(this)

        receiver = MyReceiver()

        LocalBroadcastManager.getInstance(context).registerReceiver(receiver!!,
                IntentFilter(LocationUpdatesService.ACTION_BROADCAST))
    }

    fun onDetachedFromEngine() {
        context = null
        isAttached = false
    }

    fun setActivity(binding: ActivityPluginBinding?) {
        this.activity = binding?.activity

        if(this.activity != null){
            if (Utils.requestingLocationUpdates(context!!)) {
                if (!checkPermissions()) {
                    requestPermissions()
                }
            }
        }
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "stop_location_service" -> {
                service?.removeLocationUpdates()
                LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver!!)

                if (bound) {
                    context!!.unbindService(mServiceConnection)
                    bound = false
                }

                result.success(0)
            }
            "start_location_service" -> {
                LocalBroadcastManager.getInstance(context!!).registerReceiver(receiver!!,
                        IntentFilter(LocationUpdatesService.ACTION_BROADCAST))
                if (!bound) {
                    val distanceFilter : Double? = call.argument("distance_filter")
                    val intent = Intent(context, LocationUpdatesService::class.java)
                    intent.putExtra("distance_filter", distanceFilter)
                    context!!.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
                }

                result.success(0)

            }
            "set_android_notification" -> {
                val notificationTitle: String? = call.argument("title")
                val notificationMessage: String? = call.argument("message")
                val notificationIcon: String? = call.argument("icon")

                if (notificationTitle != null) LocationUpdatesService.NOTIFICATION_TITLE = notificationTitle
                if (notificationMessage != null) LocationUpdatesService.NOTIFICATION_MESSAGE = notificationMessage
                if (notificationIcon != null) LocationUpdatesService.NOTIFICATION_ICON = notificationIcon

                if (service != null) {
                    service?.updateNotification()
                }

                result.success(0)
            }
            "set_configuration" -> {
                val timeInterval: Long? = call.argument<String>("interval")?.toLongOrNull()
                if (timeInterval != null) LocationUpdatesService.UPDATE_INTERVAL_IN_MILLISECONDS = timeInterval

                result.success(0)
            }
            else -> result.notImplemented()
        }
    }

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            bound = true
            val binder = service as LocationUpdatesService.LocalBinder
            this@BackgroundLocationService.service = binder.service
            requestLocation()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            service = null
        }
    }

    /**
     * Requests a location updated.
     * If permission is denied, it requests the needed permission
     */
    private fun requestLocation() {
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            service?.requestLocationUpdates()
        }
    }

    /**
     * Checks the current permission for `ACCESS_FINE_LOCATION`
     */
    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
    }


    /**
     * Requests permission for location.
     * Depending on the current activity, displays a rationale for the request.
     */
    private fun requestPermissions() {
        if(activity == null) {
            return
        }

        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.ACCESS_FINE_LOCATION)
        if (shouldProvideRationale) {
            Log.i(BackgroundLocationPlugin.TAG, "Displaying permission rationale to provide additional context.")
            Toast.makeText(context, R.string.permission_rationale, Toast.LENGTH_LONG).show()

        } else {
            Log.i(BackgroundLocationPlugin.TAG, "Requesting permission")
            ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
            if (location != null) {
                val locationMap = HashMap<String, Any>()
                locationMap["latitude"] = location.latitude
                locationMap["longitude"] = location.longitude
                locationMap["altitude"] = location.altitude
                locationMap["accuracy"] = location.accuracy.toDouble()
                locationMap["bearing"] = location.bearing.toDouble()
                locationMap["speed"] = location.speed.toDouble()
                locationMap["time"] = location.time.toDouble()
                locationMap["is_mock"] = location.isFromMockProvider
                channel?.invokeMethod("location", locationMap, null)
            }
        }
    }
}