package com.alefmoreira.citytraveltracker.model

import android.content.SharedPreferences
import com.alefmoreira.citytraveltracker.other.Constants
import com.alefmoreira.citytraveltracker.other.Constants.INITIAL_LONG
import com.alefmoreira.citytraveltracker.other.Constants.INITIAL_TIME
import com.alefmoreira.citytraveltracker.other.Constants.MATRIX_MILEAGE
import com.alefmoreira.citytraveltracker.other.Constants.MATRIX_TIME
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixRow
import kotlin.math.ceil

class Dashboard(private val sharedPreferences: SharedPreferences) {
    private val prefs = sharedPreferences.edit()
    private var mileage: Long = INITIAL_LONG
    var time: String = INITIAL_TIME
        private set


    fun getMileage(): String = mileage.toString()

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
        prefs.putString(MATRIX_MILEAGE, getMileage())
        prefs.putString(MATRIX_TIME, time)
        prefs.commit()
    }

    fun getDashboardFromPrefs(): Dashboard {
        mileage = sharedPreferences.getString(MATRIX_MILEAGE, "0")?.toLong() ?: 0L
        time = sharedPreferences.getString(MATRIX_TIME, INITIAL_TIME) ?: INITIAL_TIME
        return this
    }
}