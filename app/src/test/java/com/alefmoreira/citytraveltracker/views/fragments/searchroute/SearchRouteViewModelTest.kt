package com.alefmoreira.citytraveltracker.views.fragments.searchroute

import com.alefmoreira.citytraveltracker.MainCoroutineRule
import com.alefmoreira.citytraveltracker.coroutines.TestDispatchers
import com.alefmoreira.citytraveltracker.repositories.FakePlacesClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    fun `insert empty text`() {

//        viewModel.validateText("", AutocompleteSessionToken.newInstance())
    }


}