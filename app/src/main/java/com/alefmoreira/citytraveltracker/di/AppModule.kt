package com.alefmoreira.citytraveltracker.di

import android.content.Context
import androidx.room.Room
import com.alefmoreira.citytraveltracker.coroutines.DefaultDispatchers
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.data.CTTDatabase
import com.alefmoreira.citytraveltracker.data.dao.CityDAO
import com.alefmoreira.citytraveltracker.other.Constants.BASE_URL
import com.alefmoreira.citytraveltracker.other.Constants.DATABASE_NAME
import com.alefmoreira.citytraveltracker.remote.DistanceMatrixAPI
import com.alefmoreira.citytraveltracker.repositories.CTTRepository
import com.alefmoreira.citytraveltracker.repositories.DefaultCTTRepository
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
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

    @Singleton
    @Provides
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient =
        Places.createClient(context)
}