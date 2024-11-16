package com.almoullim.background_location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import java.util.prefs.Preferences
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.loader.FlutterLoader

class BootBroadcastReceiver : BroadcastReceiver() {
    private val TAG = BootBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "BOOT detected: ${intent.action}")
        val pref = context.getSharedPreferences("backgroundLocationPreferences", Context.MODE_PRIVATE)
        Log.i(TAG, "BOOT Post Execute: ${pref.getBoolean("locationActive", false)}")
        if (pref.getBoolean("startOnBoot", false) && pref.getBoolean("locationActive", false)) {
            try {
                initializeFlutterEngine(context)
            } catch (tr: Throwable) {
                Log.w(TAG, "Error starting engine", tr)
            }
        }
    }

    private fun initializeFlutterEngine(context: Context) {
        Log.i("BootCompleteReceiver", "initializeFlutterEngine...")
        val flutterLoader = FlutterLoader()

        if(!flutterLoader.initialized()){
            flutterLoader.let {
                it.startInitialization(context.applicationContext)
                it.ensureInitializationCompleteAsync(context.applicationContext, null, Handler(Looper.getMainLooper())){
                    Log.i(TAG, "ensureInitializationComplete, completed..")
                    try {
                        val plugin = BackgroundLocationPlugin()
                        val serviceIntent = Intent(context, LocationUpdatesService::class.java)
                        serviceIntent.action = LocationUpdatesService.ACTION_ON_BOOT
                        ContextCompat.startForegroundService(context, serviceIntent)
                    } catch (tr: Throwable) {
                        Log.w(TAG, "Error starting service", tr)
                    }
                }
            }
        }
    }
}