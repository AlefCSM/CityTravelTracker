package com.alefmoreira.citytraveltracker.util

import androidx.annotation.Keep

enum class CitySelectionTypeEnum {
    ORIGIN,
    DESTINATION,
    CONNECTION
}

enum class AdapterLayoutEnum(i: Int) {
    SINGLE(0),
    FIRST(1),
    MIDDLE(2),
    LAST(3)
}

enum class DialogType {
    LEAVE,
    DELETE,
    CUSTOM
}