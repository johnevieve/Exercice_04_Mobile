package cstjean.mobile.exercice4
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    /**
     * Lorsque la map est ready
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        Log.d("TRACK", "googleMap initialized")
        // Configurations supplémentaires de la carte si nécessaire
    }
    /**
     * Initialisation du Fragment.
     *
     * @param savedInstanceState Les données conservées au changement d'état.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    /**
     * Lorsque la vue est crée
     *
     * @param savedInstanceState Les données conservées au changement d'état.
     *
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    /**
     * Modifie la position
     *
     * @author Genevieve Trudel
     * @author Loudevick Poirier
     */
    fun updateMap(newPosition: LatLng) {
        Log.d("TRACK", "on update")
        googleMap?.clear() // Effacez les anciens marqueurs
        googleMap?.addMarker(MarkerOptions().position(newPosition).title("Votre position"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 15f))
    }

}