package com.example.citytraveltracker.data.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.example.citytraveltracker.data.CTTDatabase
import com.example.citytraveltracker.data.City
import com.example.citytraveltracker.data.Connection
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
    fun insertCity() {
        runBlocking {
            val city = City(345, "joinville", "123")

            dao.insertCity(city)

            dao.observeAllCities().test {
                val allCities = awaitItem()
                assertThat(allCities).contains(city)
            }
        }
    }

    @Test
    fun insertCityWithNullId() {
        runBlocking {
            val city = City(name = "joinville", placeId = "123")

            dao.insertCity(city)

            dao.observeAllCities().test {
                val allCities = awaitItem()
                assertThat(allCities.size).isEqualTo(1)
            }
        }
    }


    @Test
    fun insertCitiesWithSameIdShouldThrowError() {
        runBlocking {
            val city = City(1, "joinville", "123")
            val city2 = City(1, "joinville", "123")

            dao.insertCity(city)

            try {
                dao.insertCity(city2)
            } catch (e: Exception) {
                assertThat(e.javaClass.canonicalName).isEqualTo("android.database.sqlite.SQLiteConstraintException")
            }
        }
    }

    @Test
    fun deleteCity() {
        runBlocking {
            val city = City(1, "joinville", "123")

            dao.insertCity(city)
            dao.deleteCity(city)

            dao.observeAllCities().test {
                val allCities = awaitItem()

                assertThat(allCities).doesNotContain(city)
            }
        }
    }

    @Test
    fun insertConnection() {
        runBlocking {
            val city = City(1, "joinville", "123")
            val connection = Connection(1, 1, "florianopolis", "243")

            dao.insertCity(city)
            dao.insertConnectionsList(listOf(connection))

            dao.observeCityConnectionsByCityId(1).test {
                val connectionList = awaitItem()
                assertThat(connectionList[0]).isEqualTo(connection)
            }
        }
    }

    @Test
    fun insert2Connections() {
        runBlocking {
            val city = City(1, "joinville", "123")
            val connection = Connection(1, 1, "florianopolis", "243")
            val connection2 = Connection(2, 1, "itajai", "443")

            dao.insertCity(city)
            dao.insertConnectionsList(listOf(connection, connection2))

            dao.observeCityConnectionsByCityId(1).test {
                val connectionList = awaitItem()
                assertThat(connectionList.size).isEqualTo(2)
            }
        }
    }

    @Test
    fun deleteCityShouldRemoveConnections() {
        runBlocking {
            val city = City(1, "joinville", "123")
            val connection = Connection(1, 1, "florianopolis", "243")
            val connection2 = Connection(2, 1, "itajai", "443")

            dao.insertCity(city)
            dao.insertConnectionsList(listOf(connection, connection2))
            dao.deleteCity(city)

            dao.observeCityConnectionsByCityId(1).test {
                val connectionList = awaitItem()
                assertThat(connectionList.size).isEqualTo(0)
            }
        }
    }
}