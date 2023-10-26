package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var reminders: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Reminders not found")
        }
        return Result.Success(reminders.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.values.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Test exception ")
        }
        reminders[id]?.let {
            return Result.Success(it)
        }
        return Result.Error("Reminders not found")
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }
}