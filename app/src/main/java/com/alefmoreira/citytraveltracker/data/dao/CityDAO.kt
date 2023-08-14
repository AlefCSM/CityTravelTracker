package com.alefmoreira.citytraveltracker.data.dao

import androidx.room.*
import com.alefmoreira.citytraveltracker.data.City
import com.alefmoreira.citytraveltracker.data.Connection

@Dao
interface CityDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: City): Long

    @Delete
    suspend fun deleteCity(city: City)

    @Query("SELECT * FROM cities WHERE id = :id")
    suspend fun getCityById(id: Long): City

    @Query("SELECT * FROM cities")
    fun getAllCities(): List<City>

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertConnectionsList(connections: List<Connection>)

    @Delete
    suspend fun deleteConnection(connection: Connection)

    @Transaction
    @Query("Select * from connections where cityId = :cityId")
    fun getCityConnectionsByCityId(cityId: Long): List<Connection>
}