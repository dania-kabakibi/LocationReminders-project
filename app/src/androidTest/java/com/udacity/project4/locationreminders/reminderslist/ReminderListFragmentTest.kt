package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {

    //private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) }
            single<ReminderDataSource> { get<RemindersLocalRepository>() }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            androidContext(appContext)
            modules(listOf(myModule))
        }
        //Get our real repository
        //repository = get()

        //clear the data to start fresh
        /*runBlocking {
            repository.deleteAllReminders()
        }*/
    }

    @After
    fun stopKoinAfterTest() = stopKoin()

    //    test the navigation of the fragments.
    @Test
    fun testNavigate_ToSaveLocation() {
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }
        // WHEN - Click on the reminder
        onView(withId(R.id.addReminderFAB)).perform(click())

        // THEN - Verify that we navigate to the SaveReminder
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }


    //    test the displayed data on the UI.
    @Test
    fun testDisplayedData() {
        val remindersDao: RemindersDao = get()

        runBlocking {
            remindersDao.deleteAllReminders()
        }
        // GIVEN - A reminder
        val rem = ReminderDTO(
            "Fitness",
            "for exercise",
            "Planet Fitness",
            40.792438046653494,
            -74.1447302699089
        )
        // WHEN - Add the reminder to the repository
        runBlocking {
            remindersDao.saveReminder(rem)
        }

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        // THEN - Verify that the reminder display on the UI
        onView(withText(rem.title)).check(matches(isDisplayed()))
    }

    //    add testing for the error messages.
    @Test
    fun testErrorMessages() {
        val remindersDao: RemindersDao = get()

        val rem = ReminderDTO(
            "Fitness",
            "for exercise",
            "Planet Fitness",
            40.792438046653494,
            -74.1447302699089
        )

        runBlocking {
            remindersDao.saveReminder(rem)
        }

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        runBlocking {
            remindersDao.deleteAllReminders()
        }

        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }
}