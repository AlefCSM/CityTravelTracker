package com.example.citytraveltracker.views

import com.example.citytraveltracker.MainCoroutineRule
import com.example.citytraveltracker.coroutines.TestDispatchers
import com.example.citytraveltracker.other.Status
import com.example.citytraveltracker.repositories.FakeCTTRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CTTViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: CTTViewModel
    private lateinit var testDispatcher: TestDispatchers

    @Before
    fun setup() {
        testDispatcher = TestDispatchers()
        viewModel = CTTViewModel(FakeCTTRepository(), testDispatcher)
    }

    @Test
    fun `insert city without placeId, returns error`() = runTest {
        viewModel.createDestination("curitiba", "")
        advanceUntilIdle()
        val value = viewModel.insertDestinationStatus.value.getContentIfNotHandled()
        assertThat(value?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert valid city, returns success`() = runTest {
        viewModel.createDestination("curitiba", "423")
        advanceUntilIdle()
        val value = viewModel.insertDestinationStatus.value.getContentIfNotHandled()
        assertThat(value?.status).isEqualTo(Status.SUCCESS)
        assertThat(value?.data?.city?.name).isEqualTo("curitiba")

    }

    @Test
    fun `empty destination list, should return true`() {
        val test = viewModel.isFirstRoute()
        assertThat(test).isTrue()
    }

    @Test
    fun `not empty destination list, should return false`() = runTest {
        viewModel.createDestination("curitiba", "423")
        advanceUntilIdle()
        assertThat(viewModel.isFirstRoute()).isEqualTo(false)
    }
}