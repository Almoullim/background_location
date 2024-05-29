package com.almoullim.background_location

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry

class BackgroundLocationService : MethodChannel.MethodCallHandler,
    PluginRegistry.RequestPermissionsResultListener {
    companion object {
        val METHOD_CHANNEL_NAME = "${BackgroundLocationPlugin.PLUGIN_ID}/methods"
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
    private lateinit var channel: MethodChannel
    private var activity: Activity? = null
    private var isAttached = false
    private var receiver: MyReceiver? = null
    private var service: LocationUpdatesService? = null

    /**
     * Signals whether the LocationUpdatesService is bound
     */
    private var bound: Boolean = false

    private val serviceConnection = object : ServiceConnection {
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

    fun onAttachedToEngine(@NonNull context: Context, @NonNull messenger: BinaryMessenger) {
        this.context = context
        isAttached = true
        channel = MethodChannel(messenger, METHOD_CHANNEL_NAME)
        channel.setMethodCallHandler(this)

        receiver = MyReceiver()

        LocalBroadcastManager.getInstance(context).registerReceiver(
            receiver!!, IntentFilter(LocationUpdatesService.ACTION_BROADCAST)
        )
    }

    fun onDetachedFromEngine() {
        channel.setMethodCallHandler(null)
        context = null
        isAttached = false
    }

    fun setActivity(binding: ActivityPluginBinding?) {
        this.activity = binding?.activity

        if (this.activity != null) {
            if (Utils.requestingLocationUpdates(context!!)) {
                if (!checkPermissions()) {
                    requestPermissions()
                }
            }
        }
    }

    private fun startLocationService(
        startOnBoot: Boolean?,
        interval: Int?,
        fastestInterval: Int?,
        priority: Int?,
        distanceFilter: Double?,
        forceLocationManager: Boolean?,
        callbackHandle: Long?,
        locationCallback: Long?,
    ): Int {
        LocalBroadcastManager.getInstance(context!!).registerReceiver(
            receiver!!, IntentFilter(LocationUpdatesService.ACTION_BROADCAST)
        )
            val intent = Intent(context, LocationUpdatesService::class.java)
            intent.setAction(LocationUpdatesService.ACTION_START_FOREGROUND_SERVICE)
            intent.putExtra("startOnBoot", startOnBoot)
            intent.putExtra("interval", interval?.toLong())
            intent.putExtra("fastest_interval", fastestInterval?.toLong())
            intent.putExtra("priority", priority)
            intent.putExtra("distance_filter", distanceFilter)
            intent.putExtra("force_location_manager", forceLocationManager)
            intent.putExtra("callbackHandle", callbackHandle)
            intent.putExtra("locationCallback", locationCallback)

            ContextCompat.startForegroundService(context!!, intent)
            context!!.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        return 0
    }

    private fun isLocationServiceRunning(): Boolean {
        val manager: ActivityManager =
            context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationUpdatesService::class.java.getName() == service.service.className) {
                return service.foreground
            }
        }
        return false
    }

    private fun stopLocationService(): Int {
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(receiver!!)

        val intent = Intent(context!!, LocationUpdatesService::class.java)
        intent.setAction("${context!!.packageName}.service_requests")
        intent.putExtra(
            LocationUpdatesService.ACTION_SERVICE_REQUEST,
            LocationUpdatesService.ACTION_STOP_FOREGROUND_SERVICE
        )
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)

        if (bound) {
            context!!.unbindService(serviceConnection)
            bound = false
        }

        return 0
    }

    private fun setAndroidNotification(
        channelID: String?,
        title: String?,
        message: String?,
        icon: String?,
        actionText: String?,
        callback: Long,
    ): Int {
        if (channelID != null) LocationUpdatesService.NOTIFICATION_CHANNEL_ID = channelID
        if (title != null) LocationUpdatesService.NOTIFICATION_TITLE = title
        if (message != null) LocationUpdatesService.NOTIFICATION_MESSAGE = message
        if (icon != null) LocationUpdatesService.NOTIFICATION_ICON = icon
        if (actionText != null) {
            LocationUpdatesService.NOTIFICATION_ACTION = actionText
            LocationUpdatesService.NOTIFICATION_ACTION_CALLBACK = callback
        }

        if (service != null) {
            service?.updateNotification()
        }

        return 0
    }

    private fun setConfiguration(timeInterval: Long?): Int {
        if (timeInterval != null) {
            LocationUpdatesService.UPDATE_INTERVAL_IN_MILLISECONDS = timeInterval
            LocationUpdatesService.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = timeInterval / 2
        }

        return 0
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        when (call.method) {
            "stop_location_service" -> result.success(stopLocationService())
            "start_location_service" -> {
                if (!checkPermissions()) {
                    requestPermissions()
                    return
                }

                var locationCallback: Long? = 0L
                try {
                    locationCallback = call.argument("locationCallback")
                } catch (ex: Throwable) {
                }

                var callbackHandle: Long? = 0L
                try {
                    callbackHandle = call.argument("callbackHandle")
                } catch (ex: Throwable) {
                }

                val startOnBoot: Boolean = call.argument("startOnBoot") ?: false
                val interval: Int? = call.argument("interval")
                val fastestInterval: Int? = call.argument("fastest_interval")
                val priority: Int? = call.argument("priority")
                val distanceFilter: Double? = call.argument("distance_filter")
                val forceLocationManager: Boolean? = call.argument("force_location_manager")

                result.success(
                    startLocationService(
                        startOnBoot,
                        interval,
                        fastestInterval,
                        priority,
                        distanceFilter,
                        forceLocationManager,
                        callbackHandle,
                        locationCallback,
                    )
                )
            }

            "is_service_running" -> result.success(isLocationServiceRunning())
            "set_android_notification" -> {
                val channelID: String? = call.argument("channelID")
                val notificationTitle: String? = call.argument("title")
                val notificationMessage: String? = call.argument("message")
                val notificationIcon: String? = call.argument("icon")
                val actionText: String? = call.argument("actionText")
                var callback: Long = 0L
                try {
                    callback = call.argument("actionCallback") ?: 0L
                } catch (ex: Throwable) {
                }

                result.success(
                    setAndroidNotification(
                        channelID,
                        notificationTitle,
                        notificationMessage,
                        notificationIcon,
                        actionText,
                        callback,
                    )
                )
            }

            "set_configuration" -> result.success(
                setConfiguration(
                    call.argument<String>("interval")?.toLongOrNull()
                )
            )

            else -> result.notImplemented()
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
        Log.i(BackgroundLocationPlugin.TAG, "Check permission")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.i(BackgroundLocationPlugin.TAG, "Check permission > Tiramisu")
            var allowed = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                context!!, Manifest.permission.ACCESS_FINE_LOCATION
            ) && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                context!!, Manifest.permission.FOREGROUND_SERVICE_LOCATION
            )

            if (allowed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                Log.i(BackgroundLocationPlugin.TAG, "Check permission > Upside down cake")
                allowed = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    context!!, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }

            return allowed
        } else {
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                context!!, Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }


    /**
     * Requests permission for location.
     * Depending on the current activity, displays a rationale for the request.
     */
    private fun requestPermissions() {
        if (activity == null) {
            return
        }

        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            activity!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(
                BackgroundLocationPlugin.TAG,
                "Displaying permission rationale to provide additional context."
            )
            Toast.makeText(context, R.string.permission_rationale, Toast.LENGTH_LONG).show()

        } else {
            Log.i(BackgroundLocationPlugin.TAG, "Requesting permission")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val actionType = intent.getStringExtra(LocationUpdatesService.ACTION_BROADCAST_TYPE)

            val location =
                intent.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
            val locationMap = HashMap<String, Any>()
            if (location != null) {
                locationMap["latitude"] = location.latitude
                locationMap["longitude"] = location.longitude
                locationMap["altitude"] = location.altitude
                locationMap["accuracy"] = location.accuracy.toDouble()
                locationMap["bearing"] = location.bearing.toDouble()
                locationMap["speed"] = location.speed.toDouble()
                locationMap["time"] = location.time.toDouble()
                locationMap["is_mock"] = location.isFromMockProvider
            }

            when (actionType) {
                LocationUpdatesService.ACTION_BROADCAST_LOCATION -> channel.invokeMethod(
                    "location",
                    locationMap,
                    null
                )

                LocationUpdatesService.ACTION_NOTIFICATION_ACTIONED -> {
                    val result = HashMap<String, Any>()
                    result["ARG_LOCATION"] = locationMap
                    result["ARG_CALLBACK"] =
                        intent.getLongExtra(LocationUpdatesService.EXTRA_ACTION_CALLBACK, 0L)
                    channel.invokeMethod("notificationAction", result, null)
                }
            }
        }
    }

    /**
     * Handle the response from a permission request
     * @return true if the result has been handled.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        Log.i(BackgroundLocationPlugin.TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> Log.i(
                    BackgroundLocationPlugin.TAG,
                    "User interaction was cancelled."
                )

                grantResults[0] == PackageManager.PERMISSION_GRANTED -> service?.requestLocationUpdates()
                else -> Toast.makeText(
                    context,
                    R.string.permission_denied_explanation,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return true
    }
}
