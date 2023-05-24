package com.example.citytraveltracker.di

import android.content.Context
import androidx.room.Room
import com.example.citytraveltracker.coroutines.DefaultDispatchers
import com.example.citytraveltracker.coroutines.DispatcherProvider
import com.example.citytraveltracker.data.CTTDatabase
import com.example.citytraveltracker.data.dao.CityDAO
import com.example.citytraveltracker.other.Constants.BASE_URL
import com.example.citytraveltracker.other.Constants.DATABASE_NAME
import com.example.citytraveltracker.remote.DistanceMatrixAPI
import com.example.citytraveltracker.repositories.CTTRepository
import com.example.citytraveltracker.repositories.DefaultCTTRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCTTDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, CTTDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideDefaultCTTRepository(
        dao: CityDAO,
        api: DistanceMatrixAPI
    ) = DefaultCTTRepository(dao, api) as CTTRepository

    @Singleton
    @Provides
    fun provideCityDAO(database: CTTDatabase) = database.cityDAO()


    @Singleton
    @Provides
    fun provideDistanceMatrixAPI(): DistanceMatrixAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(DistanceMatrixAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideDispatcher(): DispatcherProvider {
        return DefaultDispatchers()
    }
}