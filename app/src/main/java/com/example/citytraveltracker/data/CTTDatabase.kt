package com.example.citytraveltracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.citytraveltracker.data.dao.CityDAO
import com.example.citytraveltracker.data.dao.ConnexionDAO

@Database(
    entities = [City::class, Connexion::class],
    version = 1
)
abstract class CTTDatabase : RoomDatabase() {

    abstract fun cityDAO(): CityDAO
    abstract fun connexionDAO(): ConnexionDAO
}