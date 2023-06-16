package com.alefmoreira.citytraveltracker.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.other.Constants.SEARCH_DEBOUNCE_TIME
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchRouteViewModel @Inject constructor(
    private val placesClient: PlacesClient
) : ViewModel() {

    var query = ""
    private var _predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val predictions: StateFlow<List<AutocompletePrediction>> = _predictions

    private fun findPredictions(text: String, token: AutocompleteSessionToken) {
        query = text
        val request =
            FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                _predictions.value = response.autocompletePredictions
            }.addOnFailureListener {
                _predictions.value = emptyList<AutocompletePrediction>().toMutableList()
            }
    }

    fun clearPredictions() {
        _predictions.value = emptyList()
    }

    fun debounce(text: String, token: AutocompleteSessionToken) =
        viewModelScope.launch(Dispatchers.Main) {
            query = text
            delay(SEARCH_DEBOUNCE_TIME)
            if (text != query) {
                return@launch
            }
            findPredictions(query, token)
        }
}