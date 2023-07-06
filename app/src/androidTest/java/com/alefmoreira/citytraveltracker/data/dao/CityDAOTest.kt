package com.alefmoreira.citytraveltracker.data.dao

import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.alefmoreira.citytraveltracker.data.CTTDatabase
import com.alefmoreira.citytraveltracker.data.City
import com.alefmoreira.citytraveltracker.data.Connection
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class CityDAOTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("test_db")
    lateinit var database: CTTDatabase
    private lateinit var dao: CityDAO

    @Before
    fun setup() {
        hiltRule.inject()
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

            dao.getAllCities().test {
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

            dao.getAllCities().test {
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

            dao.getAllCities().test {
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

            dao.getCityConnectionsByCityId(1).test {
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

            dao.getCityConnectionsByCityId(1).test {
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

            dao.getCityConnectionsByCityId(1).test {
                val connectionList = awaitItem()
                assertThat(connectionList.size).isEqualTo(0)
            }
        }
    }
}