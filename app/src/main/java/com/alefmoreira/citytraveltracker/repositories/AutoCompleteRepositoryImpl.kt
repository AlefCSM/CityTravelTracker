package com.alefmoreira.citytraveltracker.repositories

import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AutoCompleteRepositoryImpl @Inject constructor(
    private val placesClient: PlacesClient,
    private val token: AutocompleteSessionToken,
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
                        _predictionStatus.value = Resource.error("Place not found!", emptyList())
                    } else {
                        _predictionStatus.value =
                            Resource(Status.SUCCESS, response.autocompletePredictions, text)
                    }

                }.addOnFailureListener {
                    _predictionStatus.value = Resource.error("Error", emptyList())
                }
        }
}