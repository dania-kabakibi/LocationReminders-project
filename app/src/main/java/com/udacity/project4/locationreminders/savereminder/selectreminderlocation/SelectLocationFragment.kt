package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.Locale


@Suppress("DEPRECATION")
class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    // Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var mMap: GoogleMap
    //private lateinit var mainHandler: Handler
    //private lateinit var runnable: Runnable
    private val foregroundRequestCode = 22

    private var POI: PointOfInterest? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val layoutId = R.layout.fragment_select_location
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.saveButton.setOnClickListener {
            onLocationSelected()
            Navigation.findNavController(it)
                .navigate(R.id.action_selectLocationFragment_to_saveReminderFragment)
        }

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        return binding.root
    }

    private fun onLocationSelected() {
        //  When the user confirms on the selected location,
        //  send back the selected location details to the view model
        //  and navigate back to the previous fragment to save the reminder and add the geofence
        _viewModel.selectedPOI.value = POI
        _viewModel.latitude.value = POI!!.latLng.latitude
        _viewModel.longitude.value = POI!!.latLng.longitude
        _viewModel.reminderSelectedLocationStr.value = POI!!.name
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }

        R.id.hybrid_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }

        R.id.satellite_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }

        R.id.terrain_map -> {
            mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //getLocation()
        /*mainHandler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            askTheUserToSelectPOI()
        }
        mainHandler.postDelayed(runnable, 1000)*/

        setPoiClick(mMap)
        setMapLongClick(mMap)
        //mMap.setMyLocationEnabled(true)
        enableMyLocation()
        setMapStyle(mMap)
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val currentLocation = LatLng(latitude, longitude)
                    val zoomLevel = 15f

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel))
                } else {
                    // Handle the case where location is null
                }
            }
            .addOnFailureListener { e ->
                // Handle any errors that occurred while getting the location
            }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e("SelectLocationFragment", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("SelectLocationFragment", "Can't find style. Error: ", e)
        }
    }


    /*private fun askTheUserToSelectPOI() {
        val mBuilder = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.dialogMessage))
            .setPositiveButton("OK", null)

        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()

        val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            mAlertDialog.cancel()
        }
    }*/

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()

            POI = poi
        }
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)

            )
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            // Permission is already granted. You can proceed with location-related tasks.
            getLocation()
            mMap.setMyLocationEnabled(true)
        } else {
            // Permission is not granted. Request it.
            /** In Activity use the method ActivityCompat.requestPermissions
            In Fragment use requestPermissions */
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                foregroundRequestCode
            )
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == foregroundRequestCode &&
            grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            enableMyLocation()
        } else {
            Snackbar.make(
                binding.selectLocationFragment,
                R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
            ).setAction(android.R.string.ok) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    foregroundRequestCode
                )
            }.show()
        }
    }

   /* override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacks(runnable)
    }*/
}