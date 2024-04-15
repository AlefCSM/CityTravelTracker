package com.alefmoreira.citytraveltracker.repositories

import com.alefmoreira.citytraveltracker.other.Resource
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mockito.Mockito.mock


class FakeAutoCompleteRepository : AutoCompleteRepository {

    val predictionsList = mutableListOf<AutocompletePrediction>()

    val cidades = listOf("Maringa", "Curitiba", "Joinville")

    private var _predictions = MutableStateFlow<Resource<List<AutocompletePrediction>>>(Resource.init())
    override val predictions: StateFlow<Resource<List<AutocompletePrediction>>> = _predictions

    override fun sendPredictionRequest(text: String) {
        predictionsList.clear()


        cidades
            .filter { it.contains(text) }
            .forEach { predictionsList.add(mock(FakeAutoComplete::class.java)) }
            .run {
                if( predictionsList.isEmpty()){
                    _predictions.value = Resource.error("Place not found!", emptyList())
                }else{
                    _predictions.value = Resource.success(predictionsList)
                }
            }
    }
}