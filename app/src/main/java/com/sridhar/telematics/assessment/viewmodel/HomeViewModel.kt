package com.sridhar.telematics.assessment.viewmodel

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sridhar.telematics.assessment.model.dao.LocationDao
import com.sridhar.telematics.assessment.model.dao.UserDao
import com.sridhar.telematics.assessment.model.entity.RealmLocation
import com.sridhar.telematics.assessment.model.entity.RealmUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.stream.Stream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val locationDao: LocationDao, private val userDao: UserDao) : ViewModel() {

    private var _realmLocation: MutableLiveData<RealmLocation> = MutableLiveData()
    val realmLocation: LiveData<RealmLocation> = _realmLocation

    private var _realmLocations: MutableLiveData<MutableList<RealmLocation>> = MutableLiveData()
    val realmLocations: LiveData<MutableList<RealmLocation>> = _realmLocations

    var currentUser: RealmUser? = null

    fun insertLocation(location: RealmLocation) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                locationDao.insert(location)
            }
            withContext(Dispatchers.Main) {
                _realmLocation.value = location
            }
        }
    }

    fun findAllUsers() : Stream<RealmUser> {
        var results: Stream<RealmUser>? = null
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    results = userDao.findAll().stream()
                }
            }
        }
        return results!!
    }

    fun findAllLocations() {
        val locations: MutableList<RealmLocation> = mutableListOf()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                locationDao.findAll().forEach {
                    locations.add(it)
                }
            }
            withContext(Dispatchers.Main) {
                _realmLocations.value = locations
            }
        }
    }

    fun clearRealm(isSignOut: Boolean = true) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                if (isSignOut)
                    userDao.deleteAll()
                locationDao.deleteAll()
            }
        }
    }

    fun updateUser() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userDao.delete(currentUser!!)
                Log.d("HomeViewModelBeforeUpdateUser", "$currentUser")
                userDao.update(currentUser!!)
                Log.d("HomeViewModelAfterUpdateUser", "$currentUser")
            }
        }
    }

    fun updateUser(selectedUser: RealmUser) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userDao.delete(currentUser!!)
                userDao.update(currentUser!!)
                userDao.delete(selectedUser)
                userDao.update(selectedUser, true)
            }
        }
    }
}