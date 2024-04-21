package com.alefmoreira.citytraveltracker.views.fragments.searchroute

import com.alefmoreira.citytraveltracker.MainCoroutineRule
import com.alefmoreira.citytraveltracker.coroutines.TestDispatchers
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.repositories.FakeAutoCompleteRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRouteViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SearchRouteViewModel
    private lateinit var testDispatcher: TestDispatchers

    @Before
    fun setup() {
        val repository = FakeAutoCompleteRepository()
        testDispatcher = TestDispatchers()
        viewModel = SearchRouteViewModel(testDispatcher,repository)
    }

    @Test
    fun `insert empty text, returns status INIT`() = runTest {
        viewModel.validateText("")
        advanceUntilIdle()
        assertThat(viewModel.predictionStatus.value.status).isEqualTo(Status.INIT)
    }

    @Test
    fun `insert text with length smaller than 3, returns status INIT`() = runTest {

        viewModel.validateText("ab")
        advanceUntilIdle()
        assertThat(viewModel.predictionStatus.value.status).isEqualTo(Status.INIT)
    }

    @Test
    fun `insert text with length bigger than 2, returns SUCCESS`() = runTest {

        viewModel.validateText("Joi")
        advanceUntilIdle()
        assertThat(viewModel.predictionStatus.value.status).isNotEqualTo(Status.INIT)
        assertThat(viewModel.predictionStatus.value.status).isEqualTo(Status.SUCCESS)
    }

    @Test
    fun `insert random text, returns ERROR`() = runTest {

        viewModel.validateText("J,mnb,mbs")
        advanceUntilIdle()
        assertThat(viewModel.predictionStatus.value.status).isEqualTo(Status.ERROR)
        assertThat(viewModel.predictionStatus.value.message).isEqualTo("Place not found!")
    }
}