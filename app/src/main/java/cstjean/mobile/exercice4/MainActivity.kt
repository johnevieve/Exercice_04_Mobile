package cstjean.mobile.exercice4

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit

private const val REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY"

class MainActivity : AppCompatActivity() {

    private lateinit var mapFragment: MapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val fragmentManager = supportFragmentManager
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var requestLocationUpdates = false
    private var requestingLocationUpdates = false

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {  permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Log.d("TRACK", "isGranted - FINE")
                    startLocationUpdates()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Log.d("TRACK", "isGranted - COARSE")
                    startLocationUpdates()
                }
                else -> {
                    // TODO
                    // Expliquer à l'usager que la fonctionnalité n'est pas disponible car elle
                    // nécessite une permission qui a été refusée.
                    Log.d("TRACK", "notGranted")
                }
            }
        }

    /**
     * Initialisation de l'Activity.
     *
     * @param savedInstanceState Les données conservées au changement d'état.
     *
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (savedInstanceState != null) {
            requestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY, false)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            TimeUnit.SECONDS.toMillis(2)
        ).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { showLocation(it) }

            }
        }
    }

    /**
     * Affiche la derniere loc
     *
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    private fun showLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO Demander la permission
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                Log.d("TRACK", location.toText())
            }
    }

    /**
     * Lorsque Start
     *
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    override fun onStart() {
        super.onStart()
            if (isPermissionGranted()) {
                Log.d("TRACK", "déjà OK")
                startLocationUpdates()
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // TODO Expliquer pourquoi la permission est nécessaire pour la fonctionnalité
                Log.d("TRACK", "déjà dit non")
            } else {
                locationPermissionLauncher.launch(permissions)
            }
    }

    /**
     * Lorsque resume
     *
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    /**
     * Lorsque pause
     *
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    override fun onPause() {
        super.onPause()
        requestingLocationUpdates = requestLocationUpdates
        stopLocationUpdates()
    }

    /**
     * Arrete l'update de loc
     *
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    private fun stopLocationUpdates() {
        Log.d("TRACK", "STOP")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        requestLocationUpdates = false
    }

    /**
     * Lance l'update de loc
     *
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (!isPermissionGranted() || requestLocationUpdates) return
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        showLastLocation()
        requestLocationUpdates = true
    }

    /**
     * Montre la location
     *
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    private fun showLocation(location: Location) {
        Log.d("TRACK", "On se rend")
        val newPosition = LatLng(location.latitude, location.longitude)
        fragmentManager.executePendingTransactions()
        val mapFragment = fragmentManager.findFragmentById(R.id.fragment_container) as? MapFragment
        mapFragment?.updateMap(newPosition)
        Log.d("TRACK", location.toText()) }

    private fun isPermissionGranted(): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) return true
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }
}