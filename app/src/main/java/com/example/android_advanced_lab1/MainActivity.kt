package com.example.android_advanced_lab1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android_advanced_lab1.R
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

//        FacebookSdk.setClientToken("252ad9ddb0800e46d639a2d5fb60a5d8")
//        FacebookSdk.sdkInitialize(applicationContext)
//
//        // Инициализация Facebook SDK
//        FacebookSdk.setApplicationId("@string/facebook_app_id")
//        FacebookSdk.sdkInitialize(applicationContext)
//        AppEventsLogger.activateApp(application)
//
//        setContentView(R.layout.activity_main)
    }
}
