package com.alefmoreira.citytraveltracker.model

import android.content.SharedPreferences
import com.alefmoreira.citytraveltracker.other.Constants.INITIAL_LONG
import com.alefmoreira.citytraveltracker.other.Constants.INITIAL_TIME
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixElement
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.DistanceMatrixRow
import com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.TextValueObject
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class DashboardTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefsEditor: SharedPreferences.Editor
    private lateinit var dashboard: Dashboard
    private val distanceMatrixRow: DistanceMatrixRow = mock(DistanceMatrixRow::class.java)
    private val distanceMatrixElement: DistanceMatrixElement =
        mock(DistanceMatrixElement::class.java)
    private val textValueObject: TextValueObject = mock(TextValueObject::class.java)

    @Before
    fun setUp() {
        sharedPreferences = mock(SharedPreferences::class.java)
        prefsEditor = mock(SharedPreferences.Editor::class.java)
        `when`(sharedPreferences.edit()).thenReturn(prefsEditor)
        `when`(prefsEditor.commit()).thenReturn(true)
        `when`(distanceMatrixElement.distance).thenReturn(textValueObject)
        `when`(distanceMatrixElement.duration).thenReturn(textValueObject)
        val elementsList: List<DistanceMatrixElement> =
            listOf(distanceMatrixElement, distanceMatrixElement, distanceMatrixElement)
        `when`(distanceMatrixRow.elements).thenReturn(elementsList)


        dashboard = Dashboard(FakeSharedPreferences())
    }

    @Test
    fun `calculateDistanceMatrix should return values`() {
        `when`(textValueObject.value).thenReturn(3000)
        val list = mutableListOf<DistanceMatrixRow>()
        for (i in 1..3) {
            list.add(distanceMatrixRow)
        }

        dashboard.calculateDistanceMatrix(list)
        assertThat(dashboard.mileage).isEqualTo(9)
        assertThat(dashboard.time).isEqualTo("2h 30 min")
    }

    @Test
    fun `calculateDistanceMatrix with empty values`() {
        `when`(textValueObject.value).thenReturn(0)
        val list = mutableListOf<DistanceMatrixRow>()
        for (i in 1..3) {
            list.add(distanceMatrixRow)
        }

        dashboard.calculateDistanceMatrix(list)
        assertThat(dashboard.mileage).isEqualTo(INITIAL_LONG)
        assertThat(dashboard.time).isEqualTo(INITIAL_TIME)
    }

    @Test
    fun `calculateDistanceMatrix with less than 1 hour`() {
        `when`(textValueObject.value).thenReturn(600)
        val list = mutableListOf<DistanceMatrixRow>()
        for (i in 1..3) {
            list.add(distanceMatrixRow)
        }

        dashboard.calculateDistanceMatrix(list)
        assertThat(dashboard.mileage).isEqualTo(2L)
        assertThat(dashboard.time).isEqualTo("0h 30 min")
    }

    @Test
    fun `dashboard reset should return initial values`() {
        `when`(textValueObject.value).thenReturn(600)
        val list = mutableListOf<DistanceMatrixRow>()
        for (i in 1..3) {
            list.add(distanceMatrixRow)
        }

        dashboard.calculateDistanceMatrix(list)
        dashboard.reset()
        assertThat(dashboard.mileage).isEqualTo(INITIAL_LONG)
        assertThat(dashboard.time).isEqualTo(INITIAL_TIME)
    }

    @Test
    fun `save to prefs and restore`() {
        `when`(textValueObject.value).thenReturn(600)
        val list = mutableListOf<DistanceMatrixRow>()
        for (i in 1..3) {
            list.add(distanceMatrixRow)
        }

        dashboard.calculateDistanceMatrix(list)
        dashboard.saveToPrefs()
        val test = dashboard.getDashboardFromPrefs()
        assertThat(test.mileage).isEqualTo(2L)
        assertThat(test.time).isEqualTo("0h 30 min")
    }
}