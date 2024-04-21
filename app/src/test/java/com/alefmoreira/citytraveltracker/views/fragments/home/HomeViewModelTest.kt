package com.alefmoreira.citytraveltracker.views.fragments.home

import app.cash.turbine.test
import com.alefmoreira.citytraveltracker.MainCoroutineRule
import com.alefmoreira.citytraveltracker.coroutines.TestDispatchers
import com.alefmoreira.citytraveltracker.network.NetworkObserver
import com.alefmoreira.citytraveltracker.network.NetworkObserverTest
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.repositories.FakeCTTRepository
import com.alefmoreira.citytraveltracker.views.fragments.route.RouteViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var testDispatcher: TestDispatchers
    private lateinit var networkObserverTest: NetworkObserver

    @Before
    fun setup() {
        val repository = FakeCTTRepository()
        testDispatcher = TestDispatchers()
        networkObserverTest = NetworkObserverTest()
        homeViewModel = HomeViewModel(repository, testDispatcher,networkObserverTest)
        routeViewModel = RouteViewModel(repository, testDispatcher,networkObserverTest)
    }

    private fun saveRoute(){
        routeViewModel.setOrigin("Curitiba", "123")
        routeViewModel.setDestination("Joinville", "123")
        routeViewModel.saveRoute()
    }

    @Test
    fun `getRoutes, should return routes`()= runTest {
        saveRoute()
        advanceUntilIdle()
        homeViewModel.getRoutes()
        homeViewModel.routes.test {
            awaitItem()
            awaitItem()
            val value =awaitItem()

            assertThat(value.status).isEqualTo(Status.SUCCESS)
            assertThat(value.data?.size).isEqualTo(2)
        }
    }

    @Test
    fun `when empty routes, isFirstRoute should return true`() {
        assertThat(homeViewModel.isFirstRoute).isEqualTo(true)
    }
    @Test
    fun `when no empty routes, isFirstRoute should return false`()= runTest {
        saveRoute()
        advanceUntilIdle()
        homeViewModel.getRoutes()
        advanceUntilIdle()
        assertThat(homeViewModel.isFirstRoute).isEqualTo(false)
    }
}