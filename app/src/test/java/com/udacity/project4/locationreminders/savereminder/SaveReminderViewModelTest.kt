package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import org.junit.jupiter.api.Assertions.*
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    // provide testing to the SaveReminderView and its live data objects
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var fakeDataSource : FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        stopKoin()

        fakeDataSource = FakeDataSource()

        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun validateAndSaveReminder_test() {
        val testRem = ReminderDataItem(
            null,
            "near me",
            "ShopRite of Belleville",
            40.80488070528913,
            -74.1456636786461
        )

        saveReminderViewModel.validateAndSaveReminder(testRem)
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_enter_title))

        val testRem2 = ReminderDataItem(
            "supermarket",
            "near me",
            "",
            40.80488070528913,
            -74.1456636786461
        )

        saveReminderViewModel.validateAndSaveReminder(testRem2)
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))
    }

    @Test
    fun onClear_test() {
        saveReminderViewModel.onClear()

        assertEquals(saveReminderViewModel.reminderTitle.getOrAwaitValue(), null)
        assertEquals(saveReminderViewModel.reminderDescription.getOrAwaitValue(), null)
        assertEquals(saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(), null)
        assertEquals(saveReminderViewModel.selectedPOI.getOrAwaitValue(), null)
        assertEquals(saveReminderViewModel.latitude.getOrAwaitValue(), null)
        assertEquals(saveReminderViewModel.longitude.getOrAwaitValue(), null)
    }
}