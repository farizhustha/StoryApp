package com.farizhustha.storyapp.ui.story.maps

import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scale
import androidx.lifecycle.lifecycleScope
import com.farizhustha.storyapp.R
import com.farizhustha.storyapp.data.Result
import com.farizhustha.storyapp.databinding.ActivityMapsBinding
import com.farizhustha.storyapp.ui.ViewModelFactory
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
    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory(context = this)
    }
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        viewModel.getAllStoriesWithLocation().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {

                    }
                    is Result.Success -> {
                        lifecycleScope.launch(Dispatchers.Main) {
                            result.data.forEach { story ->
                                val photo = async { Utils.downloadImage(story.photoUrl.toString()) }
                                val resultPhoto = photo.await()
                                if (resultPhoto != null) {
                                    val latLng = LatLng(story.lat as Double, story.lon as Double)
                                    val photoBitmap =
                                        BitmapDescriptorFactory.fromBitmap(
                                            resultPhoto.scale(
                                                100,
                                                100
                                            )
                                        )
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
                    is Result.Error -> {
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}