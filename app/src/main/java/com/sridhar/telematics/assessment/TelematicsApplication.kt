package com.sridhar.telematics.assessment

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.FirebaseApp
import com.sridhar.telematics.assessment.model.database.TelematicsDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Singleton


@HiltAndroidApp
class TelematicsApplication : Application() {

    @Inject
    @Singleton
    lateinit var database: TelematicsDatabase

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        database.initialize()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}