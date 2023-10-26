package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //provide testing to the RemindersListViewModel and its live data objects

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var fakeDataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        stopKoin()
        fakeDataSource = FakeDataSource()

        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }


    @Test
    fun check_Loading() {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun shouldReturnError() {
        fakeDataSource.setReturnError(true)
        remindersListViewModel.loadReminders()
        val actual = remindersListViewModel.showSnackBar.getOrAwaitValue()

        assertThat(actual, `is`(notNullValue()))
    }

    @Test
    fun checkShowNoData() = runBlocking {
        fakeDataSource.deleteAllReminders()
        remindersListViewModel.loadReminders()
        val result = remindersListViewModel.showNoData.value

        assertThat(result, `is`(true))
    }
}