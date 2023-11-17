package com.udacity.project4.locationreminders

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReminderDescriptionBinding

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        /** Specifying a class as a type argument is easier to read because it’s shorter than the
         * ::class.java syntax you need to use otherwise. */
        // better way
        inline fun <reified T : Activity> Context.createIntent(vararg args: Pair<String, Any>): Intent {
            val intent = Intent(this, T::class.java)
            intent.putExtras(bundleOf(*args))
            return intent
        }

        // Receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            //old way
            /*val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent*/
            return context.createIntent<ReminderDescriptionActivity>(EXTRA_ReminderDataItem to reminderDataItem)
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = R.layout.activity_reminder_description
        binding = DataBindingUtil.setContentView(this, layoutId)

        binding.reminderDataItem =
            intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem

        // The implementation of the reminder details
        /*binding.reminderTitle.text = intent.extras?.getString("REMINDER_TITLE")
        binding.reminderDescription.text = intent.extras?.getString("REMINDER_DESCRIPTION")
        binding.reminderLocation.text = intent.extras?.getString("REMINDER_LOCATION")
        binding.reminderLatitude.text = intent.extras?.getDouble("REMINDER_LATITUDE").toString()
        binding.reminderLongitude.text = intent.extras?.getDouble("REMINDER_LONGITUDE").toString()*/
    }
}