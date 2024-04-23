package com.alefmoreira.citytraveltracker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.alefmoreira.citytraveltracker.coroutines.DefaultDispatchers
import com.alefmoreira.citytraveltracker.coroutines.DispatcherProvider
import com.alefmoreira.citytraveltracker.data.CTTDatabase
import com.alefmoreira.citytraveltracker.data.dao.CityDAO
import com.alefmoreira.citytraveltracker.network.NetworkObserver
import com.alefmoreira.citytraveltracker.network.NetworkObserverImpl
import com.alefmoreira.citytraveltracker.other.Constants.BASE_URL
import com.alefmoreira.citytraveltracker.other.Constants.CTT_PREFS
import com.alefmoreira.citytraveltracker.other.Constants.DATABASE_NAME
import com.alefmoreira.citytraveltracker.remote.DistanceMatrixAPI
import com.alefmoreira.citytraveltracker.repositories.AutoCompleteRepository
import com.alefmoreira.citytraveltracker.repositories.AutoCompleteRepositoryImpl
import com.alefmoreira.citytraveltracker.repositories.CTTRepository
import com.alefmoreira.citytraveltracker.repositories.CTTRepositoryImpl
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
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
    fun provideCTTRepositoryImpl(
        dao: CityDAO,
        api: DistanceMatrixAPI,
        sharedPreferences: SharedPreferences,
        firebaseAnalytics: FirebaseAnalytics,
        firebaseCrashlytics: FirebaseCrashlytics
    ) = CTTRepositoryImpl(
        dao,
        api,
        sharedPreferences,
        firebaseAnalytics,
        firebaseCrashlytics
    ) as CTTRepository

    @Singleton
    @Provides
    fun provideCityDAO(database: CTTDatabase) = database.cityDAO()

    @Singleton
    @Provides
    fun provideDistanceMatrixAPI(): DistanceMatrixAPI = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
        .create(DistanceMatrixAPI::class.java)


    @Singleton
    @Provides
    fun provideDispatcher(): DispatcherProvider = DefaultDispatchers()

    @Singleton
    @Provides
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient =
        Places.createClient(context)

    @Singleton
    @Provides
    fun provideNetworkStatus(
        @ApplicationContext context: Context,
        dispatcher: DispatcherProvider
    ): NetworkObserver = NetworkObserverImpl(context, dispatcher)

    @Singleton
    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = context.getSharedPreferences(CTT_PREFS, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideAutocompleteToken(): AutocompleteSessionToken =
        AutocompleteSessionToken.newInstance()

    @Singleton
    @Provides
    fun provideAutoCompleteRepositoryImpl(
        placesClient: PlacesClient,
        token: AutocompleteSessionToken,
        firebaseAnalytics: FirebaseAnalytics
    ) = AutoCompleteRepositoryImpl(placesClient, token, firebaseAnalytics) as AutoCompleteRepository

    @Provides
    fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics

    @Provides
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = Firebase.crashlytics

}