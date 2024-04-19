package com.alefmoreira.citytraveltracker.views.fragments.searchroute

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.other.Constants
import com.alefmoreira.citytraveltracker.other.Constants.SEARCH_DEBOUNCE_TIME
import com.alefmoreira.citytraveltracker.other.Resource
import com.alefmoreira.citytraveltracker.repositories.AutoCompleteRepository
import com.google.android.libraries.places.api.model.AutocompletePrediction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchRouteViewModel @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val repository: AutoCompleteRepository
) : ViewModel() {

    var query = ""

    private var _predictionStatus =
        MutableStateFlow<Resource<List<AutocompletePrediction>>>(Resource.init())
    val predictionStatus: StateFlow<Resource<List<AutocompletePrediction>>> = _predictionStatus


    init {
        viewModelScope.launch(dispatcher.main){
            observe()
        }
    }

    private suspend fun observe(){
        repository.predictions.collect{
            _predictionStatus.value = it
        }
    }
    private fun findPredictions(text: String) {
        repository.sendPredictionRequest(text)
    }

    private fun clearPredictions() {
        _predictionStatus.value = Resource.init()
    }

    private fun debounce(text: String) =
        viewModelScope.launch(dispatcher.io) {
            query = text
            delay(SEARCH_DEBOUNCE_TIME)
            if (text != query) {
                return@launch
            }
            _predictionStatus.value = Resource.loading()
            findPredictions(query)
        }

    fun validateText(text: String) {
        if (text.isEmpty()) {
            clearPredictions()
            return
        }
        if (text.length > Constants.MINIMUM_SEARCH_LENGTH) {
            debounce(text)
        }
    }
}