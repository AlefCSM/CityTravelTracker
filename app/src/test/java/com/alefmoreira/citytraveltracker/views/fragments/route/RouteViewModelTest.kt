package com.alefmoreira.citytraveltracker.views.fragments.route

import app.cash.turbine.test
import com.alefmoreira.citytraveltracker.MainCoroutineRule
import com.alefmoreira.citytraveltracker.coroutines.TestDispatchers
import com.alefmoreira.citytraveltracker.network.NetworkObserver
import com.alefmoreira.citytraveltracker.network.NetworkObserverTest
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.repositories.FakeCTTRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RouteViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var routeViewModel: RouteViewModel
    private lateinit var testDispatcher: TestDispatchers
    private lateinit var networkObserver: NetworkObserver

    @Before
    fun setup() {
        val repository = FakeCTTRepository()
        testDispatcher = TestDispatchers()
        networkObserver = NetworkObserverTest()
        routeViewModel = RouteViewModel(repository, testDispatcher, networkObserver)
    }

    @Test
    fun `when saveRoute, isLoading returns true`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "123")
        routeViewModel.saveRoute()

        routeViewModel.routeStatus.test {
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.LOADING)
            assertThat(routeViewModel.isLoading).isEqualTo(true)
        }
    }
    @Test
    fun `setOrigin without name should return error`() = runTest {
        routeViewModel.setOrigin("", "123")

        routeViewModel.originStatus.test {

            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.ERROR)
            assertThat(value.message).isEqualTo("The fields must not be empty!")
        }
    }

    @Test
    fun `setOrigin without placeId should return error`() = runTest {
        routeViewModel.setOrigin("Curitiba", "")

        routeViewModel.originStatus.test {

            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.ERROR)
            assertThat(value.message).isEqualTo("The fields must not be empty!")
        }
    }

    @Test
    fun `setOrigin should return success`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")

        routeViewModel.originStatus.test {
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.SUCCESS)
            assertThat(routeViewModel.currentOrigin.city.name).isEqualTo("Curitiba")
            assertThat(routeViewModel.currentOrigin.city.placeId).isEqualTo("123")
        }
    }

    @Test
    fun `setDestination without name should return error`() = runTest {
        routeViewModel.setDestination("", "123")

        routeViewModel.destinationStatus.test {

            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.ERROR)
            assertThat(value.message).isEqualTo("The fields must not be empty!")
        }
    }

    @Test
    fun `setDestination without placeId should return error`() = runTest {
        routeViewModel.setDestination("Curitiba", "")

        routeViewModel.destinationStatus.test {

            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.ERROR)
            assertThat(value.message).isEqualTo("The fields must not be empty!")
        }
    }

    @Test
    fun `setDestination should return success`() = runTest {
        routeViewModel.setDestination("Curitiba", "123")

        routeViewModel.destinationStatus.test {
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.SUCCESS)
            assertThat(routeViewModel.currentDestination.city.name).isEqualTo("Curitiba")
            assertThat(routeViewModel.currentDestination.city.placeId).isEqualTo("123")
        }
    }

    @Test
    fun `saveRoute emits loading status`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "123")
        routeViewModel.saveRoute()

        routeViewModel.routeStatus.test {
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.LOADING)
        }
    }

    @Test
    fun `insert first route without origin, returns error`() = runTest {
        routeViewModel.setDestination("Curitiba", "123")
        routeViewModel.saveRoute()

        routeViewModel.routeStatus.test {
            var value = awaitItem()
            assertThat(value.status).isEqualTo(Status.LOADING)
            value = awaitItem()
            assertThat(value.status).isEqualTo(Status.ERROR)
            assertThat(value.message).isEqualTo("The origin must not be empty.")
        }
    }

    @Test
    fun `insert first route without destination, returns error`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.saveRoute()

        routeViewModel.routeStatus.test {
            var value = awaitItem()
            assertThat(value.status).isEqualTo(Status.LOADING)
            value = awaitItem()
            assertThat(value.status).isEqualTo(Status.ERROR)
            assertThat(value.message).isEqualTo("The destination must not be empty.")
        }
    }

    @Test
    fun `insert first route, returns success`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "123")
        routeViewModel.saveRoute()

        routeViewModel.routeStatus.test {
            var value = awaitItem()
            assertThat(value.status).isEqualTo(Status.LOADING)
            value = awaitItem()
            assertThat(value.status).isEqualTo(Status.SUCCESS)
        }
    }

    @Test
    fun `insert second route, returns success`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "123")
        routeViewModel.saveRoute()
        advanceUntilIdle()
        routeViewModel.setDestination("Florianopolis", "472683")
        routeViewModel.saveRoute()

        routeViewModel.routeStatus.test {
            var value = awaitItem()
            assertThat(value.status).isEqualTo(Status.LOADING)
            value = awaitItem()
            assertThat(value.status).isEqualTo(Status.SUCCESS)
            assertThat(routeViewModel.currentDestination.city.name).isEqualTo("Florianopolis")
        }
    }

    @Test
    fun ` isFirstRoute returns false after saving first route`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "123")
        routeViewModel.saveRoute()

        routeViewModel.isFirstRoute.test {
            awaitItem()
            val value = awaitItem()
            assertThat(value).isFalse()
        }
    }

    @Test
    fun `insert connection without destination, returns error`() = runTest {
        routeViewModel.addConnection("curitiba", "123", 0)

        routeViewModel.destinationStatus.test {
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.ERROR)
            assertThat(value.message).isEqualTo("The city must not be empty!")
        }
    }

    @Test
    fun `insert connection without name, returns error`() = runTest {
        routeViewModel.addConnection("", "123", 0)

        routeViewModel.destinationStatus.test {
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.ERROR)
            assertThat(value.message).isEqualTo("The fields must not be empty!")
        }
    }

    @Test
    fun `insert connection without placeId, returns error`() = runTest {
        routeViewModel.addConnection("Curitiba", "", 0)

        routeViewModel.destinationStatus.test {
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.ERROR)
            assertThat(value.message).isEqualTo("The fields must not be empty!")
        }
    }

    @Test
    fun `insert correct connection with destination, returns success`() = runTest {
        routeViewModel.setDestination("Florianopolis", "234")
        advanceUntilIdle()
        routeViewModel.addConnection("Curitiba", "123", 0)

        routeViewModel.destinationStatus.test {
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.SUCCESS)
        }
    }

    @Test
    fun `insert connection with position out of index, returns error`() = runTest {
        routeViewModel.setDestination("Joinville", "123")
        routeViewModel.addConnection("Curitiba", "123", 0)
        advanceUntilIdle()
        routeViewModel.addConnection("Curitiba", "123", 3)

        routeViewModel.destinationStatus.test {
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.ERROR)
            assertThat(value.message).isEqualTo("Connection out of index!")
        }
    }

    @Test
    fun `delete route, returns success`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "123")
        routeViewModel.saveRoute()
        advanceUntilIdle()

        routeViewModel.deleteRoute(routeViewModel.currentDestination)
        routeViewModel.routeStatus.test {
            var value = awaitItem()
            assertThat(value.status).isEqualTo(Status.LOADING)
            value = awaitItem()
            assertThat(value.status).isEqualTo(Status.SUCCESS)
        }
    }

    @Test
    fun `empty destination, returns true`() {
        val value = routeViewModel.isDestinationEmpty()
        assertThat(value).isTrue()
    }

    @Test
    fun `added destination, returns false`() = runTest {
        routeViewModel.setDestination("Joinville", "12456")
        advanceUntilIdle()
        val value = routeViewModel.isDestinationEmpty()
        assertThat(value).isFalse()
    }

    @Test
    fun `isButtonEnabled without Origin and Destination, returns false`() {
        assertThat(routeViewModel.isButtonEnabled()).isEqualTo(false)
    }

    @Test
    fun `isButtonEnabled without Destination, returns false`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        advanceUntilIdle()
        assertThat(routeViewModel.isButtonEnabled()).isEqualTo(false)
    }

    @Test
    fun `isButtonEnabled without Origin, returns false`() = runTest {
        routeViewModel.setDestination("Curitiba", "123")
        advanceUntilIdle()
        assertThat(routeViewModel.isButtonEnabled()).isEqualTo(false)
    }

    @Test
    fun `isButtonEnabled with Origin and Destination, returns true`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "1233")
        advanceUntilIdle()
        assertThat(routeViewModel.isButtonEnabled()).isEqualTo(true)
    }

    @Test
    fun `isButtonEnabled with saved Route, returns true`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "1233")
        routeViewModel.saveRoute()
        advanceUntilIdle()
        assertThat(routeViewModel.isButtonEnabled()).isEqualTo(true)
    }

    @Test
    fun `clearRoutes, returns empty Origin and Destination`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "1233")
        advanceUntilIdle()
        routeViewModel.clearRoutes()
        assertThat(routeViewModel.currentOrigin.city.name).isEqualTo("")
        assertThat(routeViewModel.currentDestination.city.name).isEqualTo("")
    }

    @Test
    fun `getRoute returns Route`() = runTest {
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "1233")
        routeViewModel.saveRoute()
        advanceUntilIdle()

        routeViewModel.getRoute(2L)
        routeViewModel.destinationStatus.test {
            var value = awaitItem()
            assertThat(value.status).isEqualTo(Status.LOADING)
            value = awaitItem()
            assertThat(value.status).isEqualTo(Status.SUCCESS)
            assertThat(value.data?.city?.name).isEqualTo("Joinville")
        }
    }

    @Test
    fun `removeConnection returns empty connection`() = runTest {
        routeViewModel.setDestination("Joinville", "1233")
        routeViewModel.addConnection("Curitiba", "123", 0)
        advanceUntilIdle()

        routeViewModel.removeConnection(routeViewModel.currentDestination.connections[0])
        routeViewModel.destinationStatus.test {
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.SUCCESS)
            assertThat(value.data?.city?.name).isEqualTo("Joinville")
            assertThat(value.data?.connections?.isEmpty()).isEqualTo(true)
        }
    }
}