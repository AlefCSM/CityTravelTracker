package com.alefmoreira.citytraveltracker.util

import com.google.android.libraries.places.api.net.PlacesClient
import javax.inject.Inject

class PlacesUtil @Inject constructor(private val placesClient: PlacesClient) {


    fun createRequest(query: String) {
//        return placesClient.findAutocompletePredictions()
    }
}