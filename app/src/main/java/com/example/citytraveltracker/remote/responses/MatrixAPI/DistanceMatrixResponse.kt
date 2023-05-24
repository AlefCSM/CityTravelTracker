package com.example.citytraveltracker.remote.responses.MatrixAPI

import com.google.gson.annotations.SerializedName

data class DistanceMatrixResponse(
    @SerializedName("destination_addresses")
    val destinationAddresses: List<String>,
    @SerializedName("origin_addresses")
    val originAddresses: List<String>,
    val rows: List<DistanceMatrixRow>,
    val status: String

)