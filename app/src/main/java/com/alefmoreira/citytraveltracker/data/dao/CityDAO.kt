package com.alefmoreira.citytraveltracker.data.dao

import androidx.room.*
import com.alefmoreira.citytraveltracker.data.City
import com.alefmoreira.citytraveltracker.data.Connection
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCity(city: City): Long

    @Delete
    suspend fun deleteCity(city: City)

    @Query("SELECT * FROM cities")
    fun observeAllCities(): Flow<List<City>>

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertConnectionsList(connections: List<Connection>)

    @Delete
    suspend fun deleteConnection(connection: Connection)

    @Transaction
    @Query("Select * from connections where cityId = :cityId")
    fun observeCityConnectionsByCityId(cityId: Long): Flow<List<Connection>>
}