package com.alefmoreira.citytraveltracker.util

import androidx.annotation.Keep

@Keep
enum class CitySelectionTypeEnum {
    ORIGIN,
    DESTINATION,
    CONNECTION
}

@Keep
enum class AdapterLayoutEnum(i: Int) {
    SINGLE(0),
    FIRST(1),
    MIDDLE(2),
    LAST(3)
}

@Keep
enum class DialogType {
    LEAVE,
    DELETE,
    CUSTOM
}