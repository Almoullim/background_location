package com.example.background_location_example

import io.flutter.embedding.android.FlutterActivity
import android.os.Bundle
import io.flutter.plugins.GeneratedPluginRegistrant


/**
 * This activity's purpose is to test, wether backwards compatability to v1 embedding still works
 */
class EmbeddingV1Activity: FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this.flutterEngine!!)
    }
}