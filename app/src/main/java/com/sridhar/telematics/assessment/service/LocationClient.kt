package com.sridhar.telematics.assessment.service

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLocationUpdates(interval: Long): Flow<Location>
    class LocationException(override var message: String?) : Throwable(message)
}