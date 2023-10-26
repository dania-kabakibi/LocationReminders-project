package com.udacity.project4.locationreminders

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragmentDirections
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import org.koin.android.ext.android.inject

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReminderDescriptionBinding

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        // Receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = R.layout.activity_reminder_description
        binding = DataBindingUtil.setContentView(this, layoutId)

        // The implementation of the reminder details
        binding.reminderTitle.text = intent.extras?.getString("REMINDER_TITLE")
        binding.reminderDescription.text = intent.extras?.getString("REMINDER_DESCRIPTION")
        binding.reminderLocation.text = intent.extras?.getString("REMINDER_LOCATION")
        binding.reminderLatitude.text = intent.extras?.getDouble("REMINDER_LATITUDE").toString()
        binding.reminderLongitude.text = intent.extras?.getDouble("REMINDER_LONGITUDE").toString()
    }

}