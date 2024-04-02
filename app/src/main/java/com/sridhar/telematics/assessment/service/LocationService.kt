package com.sridhar.telematics.assessment.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationServices
import com.sridhar.telematics.assessment.R
import com.sridhar.telematics.assessment.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@Singleton
class LocationService @Inject constructor() : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "Create")
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationService", "StartCommand")
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, START_STICKY)
    }

    @SuppressLint("NotificationPermission")
    private fun start() {
        Log.d("LocationService", "Start")

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location ...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        startForeground(1, notification.build())

        locationClient.getLocationUpdates(Constants.LOCATION_UPDATE_INTERVAL)
            .catch { e ->
                e.printStackTrace()
                val intent = Intent().apply {
                    action = Constants.LOCATION_UPDATE_INTENT_ACTION
                    putExtra(Constants.LOCATION_SERVICE_EXCEPTION, e.message)
                }
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
            .onEach { location ->
                val latitude = location.latitude
                val longitude = location.longitude
                val updatedNotification = notification.setContentText(
                    "Location: ($latitude, $longitude)"
                )
                Log.d("loc", "Location: ($latitude, $longitude)")
                val intent = Intent().apply {
                    action = Constants.LOCATION_UPDATE_INTENT_ACTION
                    putExtra(Constants.LATITUDE, latitude)
                    putExtra(Constants.LONGITUDE, longitude)
                }
                notificationManager.notify(1, updatedNotification.build())
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
            .launchIn(serviceScope)
    }

    private fun stop() {
        Log.d("LocationService", "Stop")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH)
        } else {
            stopForeground(true)
        }
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LocationService", "Destroy")
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}