package com.alefmoreira.citytraveltracker.views.fragments.searchroute

import com.alefmoreira.citytraveltracker.MainCoroutineRule
import com.alefmoreira.citytraveltracker.coroutines.TestDispatchers
import com.alefmoreira.citytraveltracker.other.Status
import com.alefmoreira.citytraveltracker.repositories.FakePlacesClient
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
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
        testDispatcher = TestDispatchers()
        viewModel = SearchRouteViewModel(FakePlacesClient(), testDispatcher)
    }

    @Test
    fun `insert empty text, returns status INIT`() = runTest {

        viewModel.validateText("", AutocompleteSessionToken.newInstance())
        advanceUntilIdle()
        assertThat(viewModel.predictionStatus.value.status).isEqualTo(Status.INIT)
    }

    @Test
    fun `insert text with length smaller than 3, returns status INIT`() = runTest {

        viewModel.validateText("ab", AutocompleteSessionToken.newInstance())
        advanceUntilIdle()
        assertThat(viewModel.predictionStatus.value.status).isEqualTo(Status.INIT)
    }

    @Test
    fun `insert text with length bigger than 2, returns status different than INIT`() = runTest {

        viewModel.validateText("abb", AutocompleteSessionToken.newInstance())
        advanceUntilIdle()
        assertThat(viewModel.predictionStatus.value.status).isNotEqualTo(Status.INIT)
    }


}