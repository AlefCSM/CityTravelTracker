package com.alefmoreira.citytraveltracker.repositories

import android.os.Bundle
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AutoCompleteRepositoryImpl @Inject constructor(
    private val placesClient: PlacesClient,
    private val token: AutocompleteSessionToken,
    private val firebaseAnalytics: FirebaseAnalytics
) : AutoCompleteRepository {
    private var _predictionStatus =
        MutableStateFlow<Resource<List<AutocompletePrediction>>>(Resource.init())
    override val predictions: StateFlow<Resource<List<AutocompletePrediction>>> = _predictionStatus


    override fun sendPredictionRequest(text: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(text)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                if (response.autocompletePredictions.isEmpty()) {
                    val bundle = Bundle().apply {
                        this.putString("exception_message", "Place not found!")
                    }
                    firebaseAnalytics.logEvent("sendPredictionRequest", bundle)
                    _predictionStatus.value = Resource.error("Place not found!", emptyList())
                } else {
                    _predictionStatus.value =
                        Resource(Status.SUCCESS, response.autocompletePredictions, text)
                }

            }.addOnFailureListener {
                val bundle = Bundle().apply {
                    this.putString("exception_message", "Request Failure!")
                }
                firebaseAnalytics.logEvent("sendPredictionRequest", bundle)
                _predictionStatus.value = Resource.error("Request Failure!", emptyList())
            }
    }
}