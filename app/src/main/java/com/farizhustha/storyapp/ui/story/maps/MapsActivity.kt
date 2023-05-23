package com.farizhustha.storyapp.ui.story.maps

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.farizhustha.storyapp.R
import com.farizhustha.storyapp.databinding.ActivityMapsBinding
import com.farizhustha.storyapp.service.local.UserPreferences
import com.farizhustha.storyapp.ui.ViewModelFactory
import com.farizhustha.storyapp.ui.main.dataStore
import com.farizhustha.storyapp.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        val factory = ViewModelFactory(pref)
        viewModel = ViewModelProvider(
            this, factory
        )[MapsViewModel::class.java]

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        setMapsStyle()
        addManyMarker()
    }

    private fun setMapsStyle() {
        try {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        } catch (_: Resources.NotFoundException) {

        }
    }

    private fun addManyMarker() {
        viewModel.listStory.observe(this) { listStory ->
            lifecycleScope.launch(Dispatchers.Main) {
                listStory.forEach { story ->
                    val photo = async { Utils.downloadImage(story.photoUrl.toString()) }
                    val resultPhoto = photo.await()
                    if (resultPhoto != null) {
                        val latLng = LatLng(story.lat as Double, story.lon as Double)
                        val photoBitmap =
                            BitmapDescriptorFactory.fromBitmap(resultPhoto.scale(100, 100))
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(story.name)
                                .icon(photoBitmap)
                        )
                        boundsBuilder.include(latLng)
                    }
                }
                val bounds: LatLngBounds = boundsBuilder.build()
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        resources.displayMetrics.widthPixels,
                        resources.displayMetrics.heightPixels,
                        300
                    )
                )
            }
        }
    }
}