package com.alefmoreira.citytraveltracker.data.dao

import androidx.room.*

@Dao
interface ConnectionDAO {

//    @Insert(onConflict = OnConflictStrategy.NONE)
//    suspend fun insertConnection(connection: Connection)

//    @Delete
//    suspend fun deleteConnection(connection: Connection)

//    @Query("Select * FROM connections where cityId = :cityId")
//    fun observeAllConnections(cityId: Int): Flow<Array<Connection>>
}