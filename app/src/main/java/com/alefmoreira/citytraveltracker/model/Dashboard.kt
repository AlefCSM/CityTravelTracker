package com.alefmoreira.citytraveltracker.model

import android.content.SharedPreferences
import com.alefmoreira.citytraveltracker.other.Constants
import com.alefmoreira.citytraveltracker.other.Constants.INITIAL_LONG
import com.alefmoreira.citytraveltracker.other.Constants.INITIAL_TIME
import com.alefmoreira.citytraveltracker.other.Constants.MATRIX_MILEAGE
import com.alefmoreira.citytraveltracker.other.Constants.MATRIX_TIME
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixRow
import kotlin.math.ceil

private const val SHARED_PREFS_ERROR = "Dashboard: sharedPreferences has not been initialized!"

class Dashboard() {
    constructor(sharedPreferences: SharedPreferences) : this() {
        this.sharedPreferences = sharedPreferences
        this.prefsEditor = sharedPreferences.edit()
    }

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefsEditor: SharedPreferences.Editor
    var mileage: Long = INITIAL_LONG
        private set
    var time: String = INITIAL_TIME
        private set

    fun calculateDistanceMatrix(rows: List<DistanceMatrixRow>) {
        var seconds: Long = INITIAL_LONG
        var meters: Long = INITIAL_LONG
        rows.forEachIndexed { index, distanceMatrixRow ->
            seconds += distanceMatrixRow.elements[index].duration.value
            meters += distanceMatrixRow.elements[index].distance.value
        }
        setMileage(meters)
        setHours(seconds)
    }

    private fun setMileage(meters: Long) {
        mileage = if (meters == 0L) {
            0L
        } else {
            ceil(meters.toDouble() / Constants.METERS_IN_KM).toLong()
        }
    }

    private fun setHours(seconds: Long) {
        if(seconds == 0L) return
        val horas = seconds / Constants.SECONDS_IN_HOUR
        val diff = if (horas > 0) {
            seconds % (horas * Constants.SECONDS_IN_HOUR)
        } else {
            seconds % Constants.SECONDS_IN_HOUR
        }
        val minutes = ceil(diff.toDouble() / Constants.SECONDS_IN_MINUTE).toLong()

        time = "${horas}h $minutes min"
    }

    fun saveToPrefs() {
        if (!this::sharedPreferences.isInitialized) throw Exception(SHARED_PREFS_ERROR)
        prefsEditor.putLong(MATRIX_MILEAGE, mileage)
        prefsEditor.putString(MATRIX_TIME, time)
        prefsEditor.commit()
    }

    fun getDashboardFromPrefs(): Dashboard {
        if (!this::sharedPreferences.isInitialized) throw Exception(SHARED_PREFS_ERROR)
        mileage = sharedPreferences.getLong(MATRIX_MILEAGE, 0L)
        time = sharedPreferences.getString(MATRIX_TIME, INITIAL_TIME) ?: INITIAL_TIME
        return this
    }

    fun reset() {
        mileage = INITIAL_LONG
        time = INITIAL_TIME
    }
}