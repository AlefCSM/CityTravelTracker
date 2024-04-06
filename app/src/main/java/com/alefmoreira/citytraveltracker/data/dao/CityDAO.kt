package com.alefmoreira.citytraveltracker.data.dao

import androidx.room.*
import com.alefmoreira.citytraveltracker.data.City
import com.alefmoreira.citytraveltracker.data.Connection

@Dao
interface CityDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCity(city: City): Long

    @Delete
    fun deleteCity(city: City)

    @Query("SELECT * FROM cities WHERE id = :id")
    fun getCityById(id: Long): City

    @Query("SELECT * FROM cities")
    fun getAllCities(): List<City>

    @Insert(onConflict = OnConflictStrategy.NONE)
    fun insertConnectionsList(connections: List<Connection>)

    @Delete
    fun deleteConnection(connection: Connection)

    @Transaction
    @Query("Select * from connections where cityId = :cityId")
    fun getCityConnectionsByCityId(cityId: Long): List<Connection>
}