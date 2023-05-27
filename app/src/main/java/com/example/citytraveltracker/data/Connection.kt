package com.example.citytraveltracker.data

import androidx.room.*
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(
    tableName = "connections",
    foreignKeys = [
        ForeignKey(
            entity = City::class,
            parentColumns = ["id"],
            childColumns = ["cityId"],
            onDelete = CASCADE
        )
    ]
)
data class Connection(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo(index = true)
    var cityId: Long,
    var name: String,
    var placeId: String
)
