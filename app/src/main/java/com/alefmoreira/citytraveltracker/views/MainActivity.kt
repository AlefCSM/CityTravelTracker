package com.alefmoreira.citytraveltracker.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.alefmoreira.citytraveltracker.BuildConfig.MAPS_API_KEY
import com.alefmoreira.citytraveltracker.R
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        val apiKey = MAPS_API_KEY
        applicationContext?.let { Places.initialize(it, apiKey) }
    }
}