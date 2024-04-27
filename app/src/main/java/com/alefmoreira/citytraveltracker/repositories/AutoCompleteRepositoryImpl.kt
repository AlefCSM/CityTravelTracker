package com.alefmoreira.citytraveltracker.repositories

import com.alefmoreira.citytraveltracker.other.Constants.PREDICTION_PLACE_NOT_FOUND
import com.alefmoreira.citytraveltracker.other.Constants.PREDICTION_REQUEST_FAILURE
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AutoCompleteRepositoryImpl @Inject constructor(
    private val placesClient: PlacesClient,
    private val token: AutocompleteSessionToken,
    private val firebaseCrashlytics: FirebaseCrashlytics
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

                    firebaseCrashlytics.recordException(Exception(PREDICTION_PLACE_NOT_FOUND))
                    _predictionStatus.value =
                        Resource.error(PREDICTION_PLACE_NOT_FOUND, emptyList())
                } else {
                    _predictionStatus.value =
                        Resource(Status.SUCCESS, response.autocompletePredictions, text)
                }

            }.addOnFailureListener {

                firebaseCrashlytics.recordException(it)
                _predictionStatus.value = Resource.error(PREDICTION_REQUEST_FAILURE, emptyList())
            }
    }
}