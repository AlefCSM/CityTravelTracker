package com.alefmoreira.citytraveltracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alefmoreira.citytraveltracker.data.dao.CityDAO
import com.alefmoreira.citytraveltracker.data.dao.ConnectionDAO
import com.alefmoreira.citytraveltracker.other.Constants.DATABASE_VERSION

@Database(
    entities = [City::class, Connection::class],
    version = DATABASE_VERSION
)
abstract class CTTDatabase : RoomDatabase() {

    abstract fun cityDAO(): CityDAO
    abstract fun connectionDAO(): ConnectionDAO
}