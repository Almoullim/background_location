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
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.NewIntentListener





class BackgroundLocationPlugin : FlutterPlugin, ActivityAware {

    companion object {

        /**
        Legacy for v1 embedding
         */
        @SuppressWarnings("deprecation")
        fun registerWith(registrar: PluginRegistry.Registrar) {
            val service = BackgroundLocationService.getInstance()
            service.onAttachedToEngine(registrar.context(), registrar.messenger())
        }

        const val TAG = "com.almoullim.Log.Tag"
        const val PLUGIN_ID = "com.almoullim.background_location"
    }


    override fun onAttachedToEngine(binding: FlutterPluginBinding) {
        BackgroundLocationService.getInstance().onAttachedToEngine(binding.applicationContext, binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        BackgroundLocationService.getInstance().onDetachedFromEngine()
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        BackgroundLocationService.getInstance().setActivity(binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivity() {
        BackgroundLocationService.getInstance().setActivity(null)
    }

}
