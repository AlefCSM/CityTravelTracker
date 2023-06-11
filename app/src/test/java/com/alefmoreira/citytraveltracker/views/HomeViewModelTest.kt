package com.alefmoreira.citytraveltracker.views

import com.alefmoreira.citytraveltracker.MainCoroutineRule
import com.alefmoreira.citytraveltracker.coroutines.TestDispatchers
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
class HomeViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var testDispatcher: TestDispatchers

    @Before
    fun setup() {
        testDispatcher = TestDispatchers()
        viewModel = HomeViewModel(FakeCTTRepository(), testDispatcher)
    }

    @Test
    fun `insert first route without origin, returns error`() {
        viewModel.setDestination("Curitiba", "123")
        viewModel.saveRoute()
        val value = viewModel.routeStatus.value.getContentIfNotHandled()
        assertThat(value?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert first route without destination, returns error`() {
        viewModel.setOrigin("Curitiba", "123")
        viewModel.saveRoute()
        val value = viewModel.routeStatus.value.getContentIfNotHandled()
        assertThat(value?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert first route, returns success`() = runTest {
        viewModel.setOrigin("Curitiba", "123")
        viewModel.setDestination("Joinville", "123")
        viewModel.saveRoute()
        advanceUntilIdle()
        val value = viewModel.routeStatus.value.getContentIfNotHandled()
        assertThat(value?.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `insert second route, returns success`() = runTest {
        viewModel.setOrigin("Curitiba", "123")
        viewModel.setDestination("Joinville", "123")
        viewModel.saveRoute()
        advanceUntilIdle()
        viewModel.setDestination("Florianopolis", "472683")
        viewModel.saveRoute()
        advanceUntilIdle()

        val value = viewModel.routeStatus.value.getContentIfNotHandled()
        val routes = viewModel.routes.value.getContentIfNotHandled()
        assertThat(value?.status).isEqualTo(Status.SUCCESS)
        assertThat(routes?.data?.size).isEqualTo(3)

    }

    @Test
    fun ` isFirstRoute returns false after saving first route`() = runTest {
        viewModel.setOrigin("Curitiba", "123")
        viewModel.setDestination("Joinville", "123")
        viewModel.saveRoute()
        advanceUntilIdle()

        val value = viewModel.isFirstRoute()
        assertThat(value).isFalse()
    }

    @Test
    fun `delete route, returns success`() = runTest {
        viewModel.setOrigin("Curitiba", "123")
        viewModel.setDestination("Joinville", "123")
        viewModel.saveRoute()
        advanceUntilIdle()
        var routes = viewModel.routes.value.getContentIfNotHandled()

        viewModel.deleteRoute(routes?.data?.last()!!)
        advanceUntilIdle()

        val value = viewModel.routeStatus.value.getContentIfNotHandled()
        routes = viewModel.routes.value.peekContent()
        assertThat(value?.status).isEqualTo(Status.SUCCESS)
        assertThat(routes.data?.size).isEqualTo(1)
    }

    @Test
    fun `one route on DistanceMatrix, returns error`() = runTest {
        viewModel.setOrigin("Curitiba", "123")
        viewModel.setDestination("Joinville", "123")
        viewModel.saveRoute()
        advanceUntilIdle()
        val routes = viewModel.routes.value.getContentIfNotHandled()

        viewModel.deleteRoute(routes?.data?.last()!!)
        advanceUntilIdle()

        viewModel.getDistanceMatrix()

        val value = viewModel.distanceMatrixStatus

        assertThat(value.value.peekContent().status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `DistanceMatrix, returns success`() = runTest {
        viewModel.setOrigin("Curitiba", "123")
        viewModel.setDestination("Joinville", "123")
        viewModel.saveRoute()

        advanceUntilIdle()
        val routes = viewModel.routes.value.getContentIfNotHandled()
        viewModel.getDistanceMatrix()

        val value = viewModel.distanceMatrixStatus

        assertThat(value.value.peekContent().status).isEqualTo(Status.SUCCESS)
        assertThat(routes).isNotNull()
    }

    @Test
    fun `empty destination list, should return true`() {
        val test = viewModel.isFirstRoute()
        assertThat(test).isTrue()
    }


    @Test
    fun `insert incorrect destination, returns error`() {
        viewModel.setDestination("curitiba", "")

        assertThat(
            viewModel.destinationStatus.value.getContentIfNotHandled()?.status
        ).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert correct destination, returns success`() {
        viewModel.setDestination("curitiba", "324")

        assertThat(
            viewModel.destinationStatus.value.getContentIfNotHandled()?.status
        ).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `insert incorrect origin, returns error`() {
        viewModel.setOrigin("curitiba", "")

        assertThat(
            viewModel.originStatus.value.getContentIfNotHandled()?.status
        ).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert correct origin, returns success`() {
        viewModel.setOrigin("curitiba", "324")

        assertThat(
            viewModel.originStatus.value.getContentIfNotHandled()?.status
        ).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `insert connection without destination, returns error`() {
        viewModel.addConnection("curitiba", "123")

        assertThat(
            viewModel.destinationStatus.value.getContentIfNotHandled()?.status
        ).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert correct connection with destination, returns success`() {
        viewModel.setDestination("Florianopolis", "234")
        viewModel.addConnection("Curitiba", "123")

        assertThat(
            viewModel.destinationStatus.value.getContentIfNotHandled()?.status
        ).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `save first Route without origin, returns error`() = runTest {
        viewModel.setDestination("Joinville", "234")
        viewModel.saveRoute()
        advanceUntilIdle()

    }

    @Test
    fun `empty origin, returns false`() {
        val test = viewModel.isOriginEmpty()
        assertThat(test).isTrue()
    }

    @Test
    fun `added origin, returns true`() {
        viewModel.setOrigin("Joinville", "12456")
        val test = viewModel.isOriginEmpty()
        assertThat(test).isFalse()
    }

    @Test
    fun `empty destination, returns false`() {
        val test = viewModel.isDestinationEmpty()
        assertThat(test).isTrue()
    }

    @Test
    fun `added destination, returns true`() {
        viewModel.setDestination("Joinville", "12456")
        val test = viewModel.isDestinationEmpty()
        assertThat(test).isFalse()
    }
}