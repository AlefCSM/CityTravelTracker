package com.alefmoreira.citytraveltracker.repositories

import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.net.*


class FakePlacesClient : PlacesClient {
    override fun fetchPhoto(p0: FetchPhotoRequest): Task<FetchPhotoResponse> {
        TODO("Not yet implemented")
    }

    override fun fetchPlace(p0: FetchPlaceRequest): Task<FetchPlaceResponse> {
        TODO("Not yet implemented")
    }

    override fun findAutocompletePredictions(p0: FindAutocompletePredictionsRequest): Task<FindAutocompletePredictionsResponse> {
        TODO("Not yet implemented")
    }

    override fun findCurrentPlace(p0: FindCurrentPlaceRequest): Task<FindCurrentPlaceResponse> {
        TODO("Not yet implemented")
    }

    override fun isOpen(p0: IsOpenRequest): Task<IsOpenResponse> {
        TODO("Not yet implemented")
    }


}