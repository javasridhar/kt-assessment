package com.sridhar.telematics.assessment.view

import android.animation.ValueAnimator
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.sridhar.telematics.assessment.R
import com.sridhar.telematics.assessment.databinding.ActivityLocationMapBinding
import com.sridhar.telematics.assessment.model.entity.RealmLocation
import com.sridhar.telematics.assessment.utils.Constants
import com.sridhar.telematics.assessment.utils.LatLngInterpolator
import com.sridhar.telematics.assessment.utils.LinearFixedInterpolator
import com.sridhar.telematics.assessment.viewmodel.LocationMapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.stream.consumeAsFlow


@AndroidEntryPoint
class LocationMapActivity : AppCompatActivity() {

    private var job: Job? = null
    private var timer: Flow<LatLng>? = null
    private var marker: Marker? = null
    private lateinit var selectedLocation: RealmLocation
    private val locationMapViewModel: LocationMapViewModel by viewModels()
    private var locations: ArrayList<LatLng> = arrayListOf()
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLocationMapBinding =  DataBindingUtil.setContentView(this, R.layout.activity_location_map)

        locationMapViewModel.findAllLocations()
        locationMapViewModel.realmLocations.observe(this) {

            /**
             * var i and j are introduced for testing in same/static location (while not moving),
             * latitude and longitude is changed manually by giving i for latitude, j for longitude
             */
            var i = 0.0003f
            var j = 0.0006f
            it.forEach { location ->
                locations.add(LatLng(location.latitude + i, location.longitude + j))
//                locations.add(LatLng(location.latitude, location.longitude))
                i += 0.0003f
                j += 0.0006f
            }
            binding.play.isEnabled = true
        }

        binding.play.setOnClickListener {
            var isFirstTimeAnimate = true
            binding.play.isEnabled = false
            mMap.clear()

            lifecycleScope.launch {
                job?.cancelAndJoin()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                timer = locations.stream().consumeAsFlow()
                job = timer?.onEach {
                    delay(Constants.MARKER_ANIMATION_REFRESH_INTERVAL)
                    if (!isFirstTimeAnimate) {
                        animateMarker(marker!!, Location(LocationManager.GPS_PROVIDER).apply {
                            latitude = it.latitude
                            longitude = it.longitude
                        })
                        marker!!.position = it
                    } else {
                        isFirstTimeAnimate = false
                        marker = mMap.addMarker(MarkerOptions().apply {
                            position(it)
                        })
                    }
                    mMap.setInfoWindowAdapter(CustomInfoWindowAdapter())
                    mMap.setOnInfoWindowClickListener { clickedMarker ->
                        if (clickedMarker.isInfoWindowShown) {
                            clickedMarker.hideInfoWindow()
                        } else {
                            clickedMarker.showInfoWindow()
                        }
                    }
                    marker!!.title = "${marker!!.position.latitude}, ${marker!!.position.longitude}"
                    marker!!.showInfoWindow()
                }?.launchIn(lifecycleScope)
            }

            val polylineOptions = PolylineOptions()
            polylineOptions.addAll(locations)
            polylineOptions.width(10.0f)
            polylineOptions.color(Color.BLUE)
            polylineOptions.visible(true)
            mMap.addPolyline(polylineOptions)

            binding.play.isEnabled = true
        }

        if (intent != null) {
            val id: String = intent.getStringExtra(Constants.ID)!!
            val latitude: Double = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
            val longitude: Double = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)

            selectedLocation = RealmLocation().apply {
                this.id = id
                this.latitude = latitude
                this.longitude = longitude
            }
        }

        val map = supportFragmentManager.findFragmentById(R.id.map)
        (map as SupportMapFragment).getMapAsync {
            mMap = it
            it.uiSettings.setAllGesturesEnabled(true)

            val markerOptions = MarkerOptions()
            markerOptions.position(LatLng(selectedLocation.latitude, selectedLocation.longitude))
            val marker: Marker = it.addMarker(markerOptions)!!
            it.setInfoWindowAdapter(CustomInfoWindowAdapter())
            it.setOnInfoWindowClickListener { clickedMarker ->
                if (clickedMarker.isInfoWindowShown) {
                    clickedMarker.hideInfoWindow()
                } else {
                    clickedMarker.showInfoWindow()
                }
            }
            marker.title = "${selectedLocation.latitude}, ${selectedLocation.longitude}"
            marker.showInfoWindow()

            it.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.position, Constants.MAP_DEFAULT_ZOOM_LEVEL))
        }
    }

    private fun animateMarker(marker: Marker, destination: Location? = null) {
        if (destination != null) {
            val startPosition = marker.position
            val endPosition = LatLng(destination.latitude, destination.longitude)
            val latLngInterpolator: LatLngInterpolator = LinearFixedInterpolator()
            val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator.duration = Constants.MARKER_ANIMATION_REFRESH_INTERVAL
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener { animation ->
                try {
                    val v = animation.animatedFraction
                    val newPosition: LatLng =
                        latLngInterpolator.interpolate(v, startPosition, endPosition)
                    marker.position = newPosition
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            valueAnimator.start()
        }
    }

    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
        private val window: View = layoutInflater.inflate(R.layout.custom_info_window, null)

        override fun getInfoWindow(marker: Marker): View {
            render(marker, window)
            return window
        }

        override fun getInfoContents(marker: Marker): View? {
            return null
        }

        private fun render(marker: Marker, view: View) {
            val title: String? = marker.title
            val titleUi = view.findViewById<TextView>(R.id.title)

            if (title != null) {
                titleUi.text = SpannableString(title).apply {
                    setSpan(ForegroundColorSpan(Color.BLACK), 0, length, 0)
                }
            } else {
                titleUi.text = ""
            }
        }
    }
}