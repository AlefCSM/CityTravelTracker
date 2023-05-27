package com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI

data class DistanceMatrixElement(
    val distance: TextValueObject,
    val duration: TextValueObject,
    val status: String
)
