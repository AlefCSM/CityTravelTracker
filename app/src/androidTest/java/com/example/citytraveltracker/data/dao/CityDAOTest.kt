package com.example.citytraveltracker.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.example.citytraveltracker.data.CTTDatabase
import com.example.citytraveltracker.data.City
import com.example.citytraveltracker.data.Connexion
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class CityDAOTest {
    private lateinit var database: CTTDatabase
    private lateinit var dao: CityDAO

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CTTDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.cityDAO()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertCity() = runBlocking {
        val city = City(1, "joinville", "123")

        dao.insertCity(city)

        dao.observeAllCities().test {
            val allCities = awaitItem()
            assertThat(allCities).contains(city)
        }
    }
    @Test
    fun insertCityWithNullId() = runBlocking {
        val city = City( name="joinville", placeId =  "123")

        dao.insertCity(city)

        dao.observeAllCities().test {
            val allCities = awaitItem()
            assertThat(allCities.size).isEqualTo(1)
        }
    }


    @Test
    fun insertCitiesWithSameIdShouldThrowError() = runBlocking {
        val city = City(1, "joinville", "123")
        val city2 = City(1, "joinville", "123")

        dao.insertCity(city)

        try{
            dao.insertCity(city2)
        }catch (e:Exception){
            assertThat(e.javaClass.canonicalName).isEqualTo("android.database.sqlite.SQLiteConstraintException")
        }
    }

    @Test
    fun deleteCity() = runBlocking {
        val city = City(1, "joinville", "123")

        dao.insertCity(city)
        dao.deleteCity(city)

        dao.observeAllCities().test {
            val allCities = awaitItem()

            assertThat(allCities).doesNotContain(city)
        }
    }

    @Test
    fun insertConnexion() = runBlocking {
        val city = City(1, "joinville", "123")
        val connexion = Connexion(1,"florianopolis","243")

        dao.insertCity(city)
        dao.insertConnexionList(listOf(connexion))

        dao.observeCityConnexionsByCityId(1).test {
            val connexionList = awaitItem()
            assertThat(connexionList[0]).isEqualTo(connexion)
        }
    }

    @Test
    fun insert2Connexions() = runBlocking {
        val city = City(1, "joinville", "123")
        val connexion = Connexion(1,"florianopolis","243")
        val connexion2 = Connexion(1,"itajai","443")

        dao.insertCity(city)
        dao.insertConnexionList(listOf(connexion,connexion2) )

        dao.observeCityConnexionsByCityId(1).test {
            val connexionList = awaitItem()
            assertThat(connexionList.size).isEqualTo(2)
        }
    }
}