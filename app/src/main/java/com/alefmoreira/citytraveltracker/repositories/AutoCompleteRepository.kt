package com.alefmoreira.citytraveltracker.repositories

import com.alefmoreira.citytraveltracker.other.Resource
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.flow.StateFlow

interface AutoCompleteRepository {
    val predictions: StateFlow<Resource<List<AutocompletePrediction>>>
     fun sendPredictionRequest(text: String)
}