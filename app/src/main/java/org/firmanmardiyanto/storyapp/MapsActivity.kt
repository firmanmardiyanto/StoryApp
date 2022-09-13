package org.firmanmardiyanto.storyapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle
import com.google.android.gms.maps.model.MarkerOptions
import org.firmanmardiyanto.storyapp.databinding.ActivityMapsBinding
import org.firmanmardiyanto.storyapp.maps.MapsViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val mapsViewModel: MapsViewModel by viewModel()
    private lateinit var mMap: GoogleMap
    private lateinit var _binding: ActivityMapsBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapsViewModel.fetchStories()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.setMapStyle(
            loadRawResourceStyle(
                this, R.raw.map_style
            )
        )

        mapsViewModel.stories.observe(this) {
            it?.let { stories ->
                mMap.clear()
                stories.data?.forEach { story ->
                    val latLng = LatLng(story.lat, story.lon)
                    mMap.addMarker(
                        MarkerOptions().position(latLng).title(story.name)
                            .snippet(story.description)
                    )
                }
                stories.data?.lastOrNull()?.let { story ->
                    val latLng = LatLng(story.lat, story.lon)
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            12f
                        )
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }
}