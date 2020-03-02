package com.almoullim.background_location

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.almoullim.background_location.Utils


class LocationUpdatesService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    private val mBinder = LocalBinder()
    private var mNotificationManager: NotificationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var mLocation: Location? = null


    companion object {

        private val PACKAGE_NAME = "com.google.android.gms.location.sample.locationupdatesforegroundservice"
        private val TAG = LocationUpdatesService::class.java.simpleName
        private val CHANNEL_ID = "channel_01"
        internal val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"
        internal val EXTRA_LOCATION = "$PACKAGE_NAME.location"
        private val EXTRA_STARTED_FROM_NOTIFICATION = "$PACKAGE_NAME.started_from_notification"
        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
        private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
        private val NOTIFICATION_ID = 12345678
        private lateinit var broadcastReceiver: BroadcastReceiver

        private val STOP_SERVICE = "stop_service"
    }


    private val notification: Notification
        get() {
            val intent = Intent(this, LocationUpdatesService::class.java)
            intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true)
            
            val builder = NotificationCompat.Builder(this)
                    .setContentTitle("Background service is running")
                    .setOngoing(true)
                    .setSound(null)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.navigation_empty_icon)
                    .setWhen(System.currentTimeMillis())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID)
            }

            return builder.build()
        }

    private var mServiceHandler: Handler? = null

    override fun onCreate() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)



                onNewLocation(locationResult!!.lastLocation)
            }
        }
        createLocationRequest()
        getLastLocation()


        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)


        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Application Name"
            val mChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)
            mChannel.setSound(null, null)
            mNotificationManager!!.createNotificationChannel(mChannel)
        }

        startForeground(NOTIFICATION_ID, notification)


        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                if (intent?.action == "stop_service") {
                    removeLocationUpdates()
                }

            }
        }


        val filter = IntentFilter()
        filter.addAction(STOP_SERVICE)
        registerReceiver(broadcastReceiver, filter)

    }


    fun requestLocationUpdates() {
        Utils.setRequestingLocationUpdates(this, true)
        try {
            mFusedLocationClient!!.requestLocationUpdates(mLocationRequest,
                    mLocationCallback!!, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, false)
        }
    }


    fun removeLocationUpdates() {
        try {
            mFusedLocationClient!!.removeLocationUpdates(mLocationCallback!!)
            Utils.setRequestingLocationUpdates(this, false)
            mNotificationManager!!.cancel(NOTIFICATION_ID)
            stopSelf()
            stopForeground(true)
        } catch (unlikely: SecurityException) {
            Utils.setRequestingLocationUpdates(this, true)
        }

    }


    private fun getLastLocation() {
        try {
            mFusedLocationClient!!.lastLocation
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            mLocation = task.result
                        } else {
                        }
                    }
        } catch (unlikely: SecurityException) {
        }

    }

    private fun onNewLocation(location: Location) {
        mLocation = location
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }


    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    inner class LocalBinder : Binder() {
        internal val service: LocationUpdatesService
            get() = this@LocationUpdatesService
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)

    }

}
