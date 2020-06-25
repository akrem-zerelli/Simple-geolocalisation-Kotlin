package esprims.gi2.tp4x

import android.Manifest
import android.content.ContentProviderClient
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = getFusedLocationProviderClient(this)
        this.getLastLocation()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        // mMap.mapType=GoogleMap.MAP_TYPE_SATELLITE
        //  mMap.mapType=GoogleMap.MAP_TYPE_TERRAIN

        // Add a marker in Sydney and move the camera
        val monastir = LatLng(35.769260, 10.819970)
        val sousse = LatLng(35.82, 10.64)
        val tunis = LatLng(36.8, 10.17)
        mMap.addMarker(MarkerOptions().position(monastir).title("monastir"))
        mMap.addMarker(MarkerOptions().position(sousse).title("sousse"))
        mMap.addPolyline(PolylineOptions().add(monastir, sousse))
        mMap.addCircle(CircleOptions().center(tunis).radius(30000.00))
        val cameraPosition = CameraPosition.builder()
            .target(monastir)
            .zoom(10f)
            .bearing(45f)
            .tilt(90f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        mMap.setOnMapClickListener {
            Toast.makeText(
                applicationContext,
                "latitude : " + it.latitude + ", longitude : " + it.longitude,
                Toast.LENGTH_SHORT
            ).show()
        }
        mMap.setOnMarkerClickListener {


            intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://fr.wikipedia.org/wiki/" + it.title))
            startActivity(intent)
            true
        }

    }

    fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                applicationContext,
                "vous devez activer l'autorisation",
                Toast.LENGTH_SHORT
            ).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    Toast.makeText(
                        this,
                        "Latitude: " + it.latitude + " Logitude: " + it.longitude + ";",
                        Toast.LENGTH_LONG
                    ).show()
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(it.latitude, it.longitude))
                            .title("Last position")
                    )

                    var geocoder = Geocoder(applicationContext, Locale.getDefault())
                    var address = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    Toast.makeText(this, address.get(0).getAddressLine(0).toString(), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Unknown last location", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLastLocation()
            else
                Toast.makeText(applicationContext, "acces no autorisé", Toast.LENGTH_SHORT).show()

        }

    }
}