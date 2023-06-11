package com.alefmoreira.citytraveltracker.views.fragments

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.alefmoreira.citytraveltracker.R
import com.alefmoreira.citytraveltracker.lauchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class RouteFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun clickButton_navigatesToSearchRouteFragment() {
        val navController = mock(NavController::class.java)
        lauchFragmentInHiltContainer<RouteFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.btn_search_route)).perform(click())

        verify(navController).navigate(
            RouteFragmentDirections.actionRouteFragmentToSearchRouteFragment()
        )
    }

    @Test
    fun pressBackButton_popBackStack() {
        val navController = mock(NavController::class.java)
        lauchFragmentInHiltContainer<RouteFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        pressBack()
        verify(navController).popBackStack()
    }
}