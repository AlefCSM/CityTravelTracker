package com.alefmoreira.citytraveltracker.other

object Constants {
    const val DATABASE_NAME = "ctt_db"
    const val DATABASE_VERSION = 1
    const val BASE_URL = "https://maps.googleapis.com"
    const val MINIMUM_SEARCH_LENGTH = 2
    const val SEARCH_DEBOUNCE_TIME = 400L
    const val DEFAULT_CONNECTION_POSITION = -1
    const val ADD_CONNECTION_ICON_POSITION = 0
    const val ADD_CONNECTION_TEXT_POSITION = 1
    const val SECONDS_IN_HOUR = 3600
    const val SECONDS_IN_MINUTE = 60
    const val METERS_IN_KM = 1000
    const val MATRIX_MILEAGE = "mileage"
    const val MATRIX_TIME = "time"
    const val ROUTE_LIST = "route list"
    const val TWO_ELEMENTS = 2
    const val INITIAL_LONG = 0L
    const val INITIAL_TIME = "0h 0 min"
    const val FEW_ELEMENTS_ERROR = "There must be at least 2 routes."
    const val CALCULUS_ERROR = "Error calculating routes"
    const val CTT_PREFS = "_CTT_Prefs"
    const val STRING_SEPARATOR = "|"
    const val PREDICTION_REQUEST_FAILURE = "Request Failure!"
    const val PREDICTION_PLACE_NOT_FOUND = "Place not found!"
}