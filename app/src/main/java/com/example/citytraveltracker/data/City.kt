package com.example.citytraveltracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class City(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String,
    var placeId: String,
)
