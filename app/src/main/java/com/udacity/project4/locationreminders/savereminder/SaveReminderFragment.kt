package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class SaveReminderFragment : BaseFragment() {

    private val locationPermissionRequestCode = 101
    private var isPermission: Boolean = false
    private lateinit var mainHandler: Handler
    private lateinit var runnable: Runnable

    // Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding

    private lateinit var geofencingClient: GeofencingClient

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireActivity(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(
            requireActivity(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_save_reminder
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        isPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


        setDisplayHomeAsUpEnabled(true)
        binding.viewModel = _viewModel
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        binding.selectLocation.setOnClickListener {
            if (isPermission) {
                // Navigate to another fragment to get the user location
                val directions = SaveReminderFragmentDirections
                    .actionSaveReminderFragmentToSelectLocationFragment()
                _viewModel.navigationCommand.value = NavigationCommand.To(directions)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please allow the location permission",
                    Toast.LENGTH_SHORT
                ).show()

                mainHandler = Handler(Looper.getMainLooper())
                runnable = Runnable {
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        locationPermissionRequestCode
                    )
                }
                mainHandler.postDelayed(runnable, 2000)
            }
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude.value
            val longitude = _viewModel.longitude.value

            // use the user entered reminder details to:
            //  1) add a geofencing request
            //  2) save the reminder to the local db

            val rem = ReminderDataItem(title, description, location, latitude ,longitude)
            addGeofenceForClue(rem)
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofenceForClue(reminder: ReminderDataItem) {
        if (reminder != null) {
            val geofence = Geofence.Builder()
                .setRequestId(reminder.id)
                .setCircularRegion(
                    reminder.latitude!!, reminder.longitude!!, GEOFENCE_RADIUS_IN_METERS
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                addOnSuccessListener {
                    _viewModel.saveReminder(reminder)
                }
                addOnFailureListener {
                }
            }
        }
    }

    private fun showReminderNotification(reminder: ReminderDataItem?) {
        if (reminder != null) {
            val notificationManager = ContextCompat.getSystemService(requireContext(), NotificationManager::class.java)
            val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                //.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(reminder.title)
                //.setContentText(reminder.message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Reminder Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager?.createNotificationChannel(channel)
            }

            notificationManager?.notify(1, notificationBuilder.build())
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
        //mainHandler.removeCallbacks(runnable)
    }

    companion object {
        internal const val ACTION_GEOFENCE_EVENT =
            "HuntMainActivity.treasureHunt.action.ACTION_GEOFENCE_EVENT"
        private const val GEOFENCE_RADIUS_IN_METERS = 100f
        private const val CHANNEL_ID = "reminder_channel"
    }
}