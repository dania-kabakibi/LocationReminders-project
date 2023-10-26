package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() = database.close()

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        // GIVEN - A new reminder saved in the database.
        val reminder = ReminderDTO(
            "Supermarket",
            "near me",
            "ShopRite of Belleville",
            40.80488070528913,
            -74.1456636786461,
            UUID.randomUUID().toString()
        )
        repository.saveReminder(reminder)

        // WHEN - Reminder retrieved by ID.
        val result = repository.getReminder(reminder.id)

        // THEN - Same reminder is returned.
        result as Result.Success
        assertThat(result.data.title, `is`("Supermarket"))
        assertThat(result.data.description, `is`("near me"))
        assertThat(result.data.location, `is`("ShopRite of Belleville"))
        assertThat(result.data.latitude, `is`(40.80488070528913))
        assertThat(result.data.longitude, `is`(-74.1456636786461))
    }

    @Test
    fun saveReminder_resultError()= runBlocking {
        // GIVEN - A new reminder saved in the database.
        val reminder = ReminderDTO(
            "Supermarket",
            "near me",
            "ShopRite of Belleville",
            40.80488070528913,
            -74.1456636786461,
            UUID.randomUUID().toString()
        )
        repository.saveReminder(reminder)

        // WHEN - Reminder retrieved by ID.
        val result = repository.getReminder(reminder.id)

        // THEN - Assume that Result is Error
        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }
}

/**
 * The only real difference from the analogous DAO test is that the repository returns
 * an instance of the sealed Result class */