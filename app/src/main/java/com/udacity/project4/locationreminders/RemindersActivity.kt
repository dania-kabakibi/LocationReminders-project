package com.udacity.project4.locationreminders

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityRemindersBinding


/**
 * The RemindersActivity that holds the reminders fragments
 */

class RemindersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRemindersBinding
    private val PERMISSION_REQUEST_CODE = 112

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemindersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")){
                getNotificationPermission();
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                navHostFragment.navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
            Log.e("RemindersActivity", "${e.message}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // allow
                } else {
                    //deny
                }
                return
            }
        }
    }
}