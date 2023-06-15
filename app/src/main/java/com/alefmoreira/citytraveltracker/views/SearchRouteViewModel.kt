package com.alefmoreira.citytraveltracker.views

import androidx.lifecycle.ViewModel
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.repositories.CTTRepository
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SearchRouteViewModel @Inject constructor(
    private val repository: CTTRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    @Inject
    lateinit var placesClient: PlacesClient

    private var _predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val predictions: StateFlow<List<AutocompletePrediction>> = _predictions

    fun findPredictions(text: String, token: AutocompleteSessionToken) {
        val request =
            FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(text)
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                _predictions.value = response.autocompletePredictions

            }.addOnFailureListener { exception: Exception? ->
                _predictions.value = emptyList<AutocompletePrediction>().toMutableList()
            }
    }

    fun clearPredictions() {
        _predictions.value = emptyList()
    }
}