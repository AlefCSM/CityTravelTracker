package com.example.citytraveltracker.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citytraveltracker.coroutines.DispatcherProvider
import com.example.citytraveltracker.data.City
import com.example.citytraveltracker.model.Destination
import com.example.citytraveltracker.model.Route
import com.example.citytraveltracker.other.Event
import com.example.citytraveltracker.other.Resource
import com.example.citytraveltracker.other.Status
import com.example.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixResponse
import com.example.citytraveltracker.repositories.CTTRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CTTViewModel @Inject constructor(
    private val repository: CTTRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {


    private lateinit var currentDestination: Destination

    private var _destinations =
        MutableStateFlow<Event<Resource<List<Destination>>>>(Event(Resource.init()))
    val observableDestinations: StateFlow<Event<Resource<List<Destination>>>> = _destinations

    private var _insertDestinationStatus =
        MutableStateFlow<Event<Resource<Destination>>>(Event(Resource.init()))

    val insertDestinationStatus: StateFlow<Event<Resource<Destination>>> = _insertDestinationStatus


    private val _distanceMatrix = MutableStateFlow<Event<Resource<DistanceMatrixResponse>>>(
        Event(Resource.init())
    )

    val distaceMatric: StateFlow<Event<Resource<DistanceMatrixResponse>>> = _distanceMatrix


    init {
        viewModelScope.launch(dispatcher.main) {
            repository.observeAllDestinations().collect {
                _destinations.value = Event(Resource.success(it))
            }
        }
    }

    fun createDestination(name: String, placeId: String) {
        _insertDestinationStatus.value = Event(Resource.loading(null))

        if (name.isEmpty() || placeId.isEmpty()) {
            _insertDestinationStatus.value =
                Event(Resource.error("The fields must not be empty!", null))
            return
        }

        val city = City(name = name, placeId = placeId)

        val destination = Destination(city, emptyList())

        insertDestinationIntoDB(destination)

        _insertDestinationStatus.value = Event(Resource(Status.SUCCESS, destination, ""))
    }

    fun insertDestinationIntoDB(destination: Destination) = viewModelScope.launch(dispatcher.main) {
        repository.insertDestination(destination)
    }

    fun deleteDestination(destination: Destination) = viewModelScope.launch(dispatcher.main) {
        repository.deleteDestination(destination)
    }

    fun getDistanceMatrix(origins: List<Route>, destinations: List<Route>) {
        if (origins.isEmpty() || destinations.isEmpty()) {
            return
        }


    }

    fun isFirstRoute(): Boolean = _destinations.value.peekContent().data?.isEmpty() ?: true
}