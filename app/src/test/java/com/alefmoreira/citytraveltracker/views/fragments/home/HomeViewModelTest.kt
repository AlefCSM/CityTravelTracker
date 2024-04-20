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
        homeViewModel.recyclerList.test {
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

    @Test
    fun `insert first route, returns success`() = runTest {
        saveRoute()
//        advanceUntilIdle()
        homeViewModel.dashboardStatus.test {
            awaitItem()
            awaitItem()
            val value = awaitItem()
            assertThat(value.status).isEqualTo(Status.SUCCESS)
        }
    }

//    @Test
//    fun `insert second route, returns success`() = runTest {
//        viewModel.setOrigin("Curitiba", "123")
//        viewModel.setDestination("Joinville", "123")
//        viewModel.saveRoute()
//        advanceUntilIdle()
//        viewModel.setDestination("Florianopolis", "472683")
//        viewModel.saveRoute()
//        advanceUntilIdle()
//
//        val value = viewModel.routeStatus.value.getContentIfNotHandled()
//        val routes = viewModel.routes.value.getContentIfNotHandled()
//        assertThat(value?.status).isEqualTo(Status.SUCCESS)
//        assertThat(routes?.data?.size).isEqualTo(3)
//
//    }
//
//    @Test
//    fun ` isFirstRoute returns false after saving first route`() = runTest {
//        viewModel.setOrigin("Curitiba", "123")
//        viewModel.setDestination("Joinville", "123")
//        viewModel.saveRoute()
//        advanceUntilIdle()
//
//        val value = viewModel.isFirstRoute()
//        assertThat(value).isFalse()
//    }
//
//    @Test
//    fun `delete route, returns success`() = runTest {
//        viewModel.setOrigin("Curitiba", "123")
//        viewModel.setDestination("Joinville", "123")
//        viewModel.saveRoute()
//        advanceUntilIdle()
//        var routes = viewModel.routes.value.getContentIfNotHandled()
//
//        viewModel.deleteRoute(routes?.data?.last()!!)
//        advanceUntilIdle()
//
//        val value = viewModel.routeStatus.value.getContentIfNotHandled()
//        routes = viewModel.routes.value.peekContent()
//        assertThat(value?.status).isEqualTo(Status.SUCCESS)
//        assertThat(routes.data?.size).isEqualTo(1)
//    }
//
//    @Test
//    fun `one route on DistanceMatrix, returns error`() = runTest {
//        viewModel.setOrigin("Curitiba", "123")
//        viewModel.setDestination("Joinville", "123")
//        viewModel.saveRoute()
//        advanceUntilIdle()
//        val routes = viewModel.routes.value.getContentIfNotHandled()
//
//        viewModel.deleteRoute(routes?.data?.last()!!)
//        advanceUntilIdle()
//
//        viewModel.getDistanceMatrix()
//
//        val value = viewModel.distanceMatrixStatus
//
//        assertThat(value.value.peekContent().status).isEqualTo(Status.ERROR)
//    }
//
//    @Test
//    fun `DistanceMatrix, returns success`() = runTest {
//        viewModel.setOrigin("Curitiba", "123")
//        viewModel.setDestination("Joinville", "123")
//        viewModel.saveRoute()
//
//        advanceUntilIdle()
//        val routes = viewModel.routes.value.getContentIfNotHandled()
//        viewModel.getDistanceMatrix()
//
//        val value = viewModel.distanceMatrixStatus
//
//        assertThat(value.value.peekContent().status).isEqualTo(Status.SUCCESS)
//        assertThat(routes).isNotNull()
//    }
//
//    @Test
//    fun `empty destination list, should return true`() {
//        val test = viewModel.isFirstRoute()
//        assertThat(test).isTrue()
//    }
//
//
//    @Test
//    fun `insert incorrect destination, returns error`() {
//        viewModel.setDestination("curitiba", "")
//
//        assertThat(
//            viewModel.destinationStatus.value.getContentIfNotHandled()?.status
//        ).isEqualTo(Status.ERROR)
//    }
//
//    @Test
//    fun `insert correct destination, returns success`() {
//        viewModel.setDestination("curitiba", "324")
//
//        assertThat(
//            viewModel.destinationStatus.value.getContentIfNotHandled()?.status
//        ).isEqualTo(Status.SUCCESS)
//    }

}