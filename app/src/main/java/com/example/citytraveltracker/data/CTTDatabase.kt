package com.example.citytraveltracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.citytraveltracker.data.dao.CityDAO
import com.example.citytraveltracker.data.dao.ConnectionDAO

@Database(
    entities = [City::class, Connection::class],
    version = 1
)
abstract class CTTDatabase : RoomDatabase() {

    abstract fun cityDAO(): CityDAO
    abstract fun connectionDAO(): ConnectionDAO
}