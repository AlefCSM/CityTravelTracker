package com.example.citytraveltracker.data.dao

import androidx.room.*
import com.example.citytraveltracker.data.City
import com.example.citytraveltracker.data.Connexion
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCity(city: City)

    @Delete()
    suspend fun deleteCity(city: City)

    @Query("SELECT * FROM cities")
    fun observeAllCities(): Flow<List<City>>

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertConnexionList(connexions: List<Connexion>)

    @Delete
    suspend fun deleteConnexion(connexion: Connexion)

    @Transaction
    @Query("Select * from connexions where cityId = :cityId")
    fun observeCityConnexionsByCityId(cityId: Int):Flow<List<Connexion>>
}