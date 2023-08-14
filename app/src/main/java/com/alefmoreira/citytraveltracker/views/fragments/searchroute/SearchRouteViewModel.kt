package com.alefmoreira.citytraveltracker.views.fragments.searchroute

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.other.Constants
import com.alefmoreira.citytraveltracker.other.Constants.SEARCH_DEBOUNCE_TIME
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.other.Status
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchRouteViewModel @Inject constructor(
    private val placesClient: PlacesClient,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    var query = ""

    private var _predictionStatus =
        MutableStateFlow<Resource<List<AutocompletePrediction>>>(Resource.init())
    val predictionStatus: StateFlow<Resource<List<AutocompletePrediction>>> = _predictionStatus

    private fun findPredictions(text: String, token: AutocompleteSessionToken) {
        val request =
            FindAutocompletePredictionsRequest.builder()
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

    private fun clearPredictions() {
        _predictionStatus.value = Resource.init()
    }

    private fun debounce(text: String, token: AutocompleteSessionToken) =
        viewModelScope.launch(dispatcher.io) {
            query = text
            delay(SEARCH_DEBOUNCE_TIME)
            if (text != query) {
                return@launch
            }
            _predictionStatus.value = Resource.loading(null)
            findPredictions(query, token)
        }

    fun validateText(text: String, token: AutocompleteSessionToken) {
        if (text.isEmpty()) {
            clearPredictions()
            return
        }
        if (text.length > Constants.MINIMUM_SEARCH_LENGTH) {
            debounce(text, token)
        }
    }
}