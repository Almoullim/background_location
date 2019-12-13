package com.almoullim.background_location

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar


class BackgroundLocationPlugin() : MethodCallHandler, PluginRegistry.RequestPermissionsResultListener {


    private lateinit var registrar: Registrar
    private lateinit var channel: MethodChannel
    private var myReceiver: MyReceiver? = null
    private var mService: LocationUpdatesService? = null
    private var mBound: Boolean = false

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "almoullim.com/background_location")
            channel.setMethodCallHandler(BackgroundLocationPlugin(registrar, channel))
        }

        private const val TAG = "com.almoullim.Log.Tag"
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }


    constructor(registrar: Registrar, channel: MethodChannel) : this() {
        this.registrar = registrar
        this.channel = channel


        myReceiver = MyReceiver()

        if (Utils.requestingLocationUpdates(registrar.activeContext())) {
            if (!checkPermissions()) {
                requestPermissions()
            }
        }
        LocalBroadcastManager.getInstance(registrar.activeContext()).registerReceiver(myReceiver!!,
                IntentFilter(LocationUpdatesService.ACTION_BROADCAST))
    }


    override fun onMethodCall(call: MethodCall, result: Result) {


        when {

            call.method == "stop_location_service" -> {

                mService?.removeLocationUpdates()

                LocalBroadcastManager.getInstance(registrar.activeContext()).unregisterReceiver(myReceiver!!)

                if (mBound) {
                    registrar.activeContext().unbindService(mServiceConnection)
                    mBound = false
                }

            }
            call.method == "start_location_service" -> {
                LocalBroadcastManager.getInstance(registrar.activeContext()).registerReceiver(myReceiver!!,
                        IntentFilter(LocationUpdatesService.ACTION_BROADCAST))
                if (!mBound) {
                    registrar.activeContext().bindService(Intent(registrar.activeContext(), LocationUpdatesService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
                }
/*
                if (mService != null) {
                    requestLocation()
                }*/
            }
            else -> result.notImplemented()
        }


    }


    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mBound = true
            val binder = service as LocationUpdatesService.LocalBinder
            mService = binder.service
            requestLocation()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
        }
    }


    private fun requestLocation() {

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            mService!!.requestLocationUpdates()
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?): Boolean {

        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults!!.isEmpty() -> Log.i(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> mService!!.requestLocationUpdates()
                else -> Toast.makeText(registrar.activeContext(), R.string.permission_denied_explanation, Toast.LENGTH_LONG).show()
            }
        }
        return true

    }

    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
            if (location != null) {
                val locationMap = HashMap<String, Double>()
                locationMap["latitude"] = location.latitude
                locationMap["longitude"] = location.longitude
                locationMap["altitude"] = location.altitude
                locationMap["accuracy"] = location.accuracy.toDouble()
                locationMap["bearing"] = location.bearing.toDouble()
                locationMap["speed"] = location.speed.toDouble()
                channel.invokeMethod("location", locationMap, null)
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(registrar.activeContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(registrar.activity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (shouldProvideRationale) {

            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            Toast.makeText(registrar.activeContext(), R.string.permission_rationale, Toast.LENGTH_LONG).show()

        } else {
            Log.i(TAG, "Requesting permission")
            ActivityCompat.requestPermissions(registrar.activity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }


}
