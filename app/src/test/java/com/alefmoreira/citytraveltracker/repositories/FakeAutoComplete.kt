package com.alefmoreira.citytraveltracker.repositories

import android.os.Parcel
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place

@Suppress("DEPRECATION")
abstract class FakeAutoComplete: AutocompletePrediction() {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {

    }

    override fun getDistanceMeters(): Int? {
        return 0
    }

    override fun getPlaceId(): String {
        return ""
    }

    @Deprecated("Deprecated in Java", ReplaceWith("mutableListOf()"))
    override fun getPlaceTypes(): MutableList<Place.Type> {
        return mutableListOf()
    }

    override fun getTypes(): MutableList<String> {
        return mutableListOf()
    }
}