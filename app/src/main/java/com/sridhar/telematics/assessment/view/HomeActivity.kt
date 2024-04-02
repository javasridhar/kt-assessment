package com.sridhar.telematics.assessment.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.permissionx.guolindev.PermissionX
import com.sridhar.telematics.assessment.R
import com.sridhar.telematics.assessment.adapter.LocationAdapter
import com.sridhar.telematics.assessment.adapter.SwitchUserAdapter
import com.sridhar.telematics.assessment.databinding.ActivityHomeBinding
import com.sridhar.telematics.assessment.databinding.SwitchUserDialogBinding
import com.sridhar.telematics.assessment.model.entity.RealmLocation
import com.sridhar.telematics.assessment.model.entity.RealmUser
import com.sridhar.telematics.assessment.service.LocationService
import com.sridhar.telematics.assessment.service.isMyServiceRunning
import com.sridhar.telematics.assessment.utils.Constants
import com.sridhar.telematics.assessment.viewmodel.HomeViewModel
import com.sridhar.telematics.assessment.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.stream.Stream


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private var mDialog: AlertDialog? = null
    private var adapter: LocationAdapter? = null
    private val homeViewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private val mLocationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when {
                intent.hasExtra(Constants.LATITUDE) && intent.hasExtra(Constants.LONGITUDE) -> {
                    val latitude: Double = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
                    val longitude: Double = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)
                    val location: RealmLocation = RealmLocation().apply {
                        id = Calendar.getInstance().timeInMillis.toString()
                        this.latitude = latitude
                        this.longitude = longitude
                    }

                    homeViewModel.insertLocation(location)
                }
            }

            when {
                intent.hasExtra(Constants.LOCATION_SERVICE_EXCEPTION) -> {
                    when (intent.getStringExtra(Constants.LOCATION_SERVICE_EXCEPTION)!!) {
                        Constants.LOCATION_SERVICE_EXCEPTION_GPS_TYPE -> {
                            killService()
                            moveToGpsSettingsPage()
                        }
                        else -> {
                            moveToAppDetailsSettingsPage()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("HomeAct", "onStart")
        askPermission()
    }

    private fun moveToGpsSettingsPage() {
        val gpsIntent = Intent()
        gpsIntent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        startActivity(gpsIntent)
    }

    private fun moveToAppDetailsSettingsPage() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun askPermission() {
        val permissions: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        }

        PermissionX.init(this)
            .permissions(*permissions)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    startService()
                } else {
                    moveToAppDetailsSettingsPage()
                }
            }
    }

    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeAct", "onCreate")
        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(mLocationReceiver, IntentFilter(Constants.LOCATION_UPDATE_INTENT_ACTION))

        mainViewModel.getCurrentUser()
        mainViewModel.realmUser.observe(this) {
            homeViewModel.currentUser = it
            it?.let {
                Glide.with(this).load(it!!.photoUrl).centerCrop().into(binding.profilePic)
            }
        }

        binding.profilePic.setOnClickListener {
            val users: Stream<RealmUser> = homeViewModel.findAllUsers()
            showSwitchUserDialog(users)
        }

        adapter = LocationAdapter { selectedLocation ->
            val intent = Intent(this@HomeActivity, LocationMapActivity::class.java)
            intent.putExtra(Constants.ID, selectedLocation.id)
            intent.putExtra(Constants.LATITUDE, selectedLocation.latitude)
            intent.putExtra(Constants.LONGITUDE, selectedLocation.longitude)
            startActivity(intent)
        }
        binding.recyclerview.adapter = adapter

        homeViewModel.findAllLocations()
        homeViewModel.realmLocations.observeForever {
            Log.d("LocationListSize", it.size.toString())
            adapter!!.addAll(it)
            try {
                binding.recyclerview.smoothScrollToPosition(it.size - 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        homeViewModel.realmLocation.observeForever {
            adapter!!.add(it)
            Log.d("LocationSize", adapter!!.itemCount.toString())
            binding.recyclerview.scrollToPosition(adapter!!.itemCount - 1)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeAct", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("HomeAct", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("HomeAct", "onStop")
    }

    private fun showSwitchUserDialog(users: Stream<RealmUser>) {
        val adapter = SwitchUserAdapter { selectedUser ->
            if (selectedUser.id != homeViewModel.currentUser!!.id) {
                killService()
                loginSelectedUser(selectedUser)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            users?.forEach {
                adapter.add(it)
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Switch User")

        val binding: SwitchUserDialogBinding = DataBindingUtil.inflate(layoutInflater, R.layout.switch_user_dialog, null, false)
        val view: View = binding.root

        binding.recyclerview.adapter = adapter
        builder.setView(view)
        builder.setCancelable(false)
        builder.setNegativeButton("Cancel"
        ) { dialog, _ -> dialog.dismiss() }
        mDialog = builder.create()
        mDialog!!.show()

        binding.signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            killService()
            mDialog!!.dismiss()
            homeViewModel.clearRealm()
            moveToLoginActivity()
        }

        binding.addAccount.setOnClickListener {
            mDialog!!.dismiss()
            killService()
            homeViewModel.clearRealm(false)
            homeViewModel.updateUser()
            moveToLoginActivity()
        }
    }

    private fun loginSelectedUser(selectedUser: RealmUser) {
        homeViewModel.updateUser(selectedUser)
        homeViewModel.clearRealm(false)
        moveToSelfActivity()
    }

    private fun moveToSelfActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        if (!isMyServiceRunning(LocationService::class.java)) {
            Log.d("HomeAct", "Service is not running. start it")
            serviceIntent.action = LocationService.ACTION_START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    private fun killService() {
        Log.d("HomeAct", "Service is running, kill it")
        val serviceIntent = Intent(this, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeAct", "onDestroy")
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(mLocationReceiver)
        mDialog?.let {
            if (mDialog?.isShowing!!) {
                mDialog!!.dismiss()
            }
        }
    }
}