package com.alefmoreira.citytraveltracker.views.fragments

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {

    @Inject
    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    fun logEvent(eventName: String, params: Map<String, Any>? = null) {
        val bundle = Bundle()

        params?.let {
            for (param in params) {
                val value = param.value
                if (value is String) {
                    bundle.putString(param.key, value)
                }
                if (value is Boolean) {
                    bundle.putBoolean(param.key, value)
                }
            }
        }
        bundle.putString("event_category", "view_model")
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}