package com.udacity.project4.locationreminders

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.ActivityRemindersBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragmentDirections
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import org.koin.android.ext.android.inject

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRemindersBinding
    private val locationPermissionRequestCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemindersBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (binding.navHostFragment as NavHostFragment).navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now proceed with location-related tasks
                val _viewModel: SaveReminderViewModel by inject()
                val directions = SaveReminderFragmentDirections
                    .actionSaveReminderFragmentToSelectLocationFragment()
                _viewModel.navigationCommand.value = NavigationCommand.To(directions)
            } else {
                // Permission denied by the user
                /*Toast.makeText(
                    requireContext(),
                    "You should allow location permission!",
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        }
    }
}