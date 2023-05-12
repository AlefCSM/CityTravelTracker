package com.example.citytraveltracker.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "connexions", indices = [Index(value = ["id","name", "cityId"], unique = true)])
data class Connexion(
    var cityId: Int,
    var name: String,
    var placeId: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
