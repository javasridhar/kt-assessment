package com.sridhar.telematics.assessment.utils

object Constants {

    const val MARKER_ANIMATION_REFRESH_INTERVAL = 100L
    const val MAP_DEFAULT_ZOOM_LEVEL = 15.0f
    const val ID = "id"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val LOCATION_UPDATE_INTENT_ACTION = "LocationUpdate"
    const val LOCATION_SERVICE_EXCEPTION = "locationServiceException"
    const val LOCATION_SERVICE_EXCEPTION_PERMISSION_TYPE = "permissionException"
    const val LOCATION_SERVICE_EXCEPTION_GPS_TYPE = "gpsException"

    // For testing purpose 15 minutes of location update is changed to 10 seconds
    const val LOCATION_UPDATE_INTERVAL = 10L * 1000
}