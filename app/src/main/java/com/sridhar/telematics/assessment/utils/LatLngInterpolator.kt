package com.sridhar.telematics.assessment.utils

import com.google.android.gms.maps.model.LatLng

interface LatLngInterpolator {
    fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng
}