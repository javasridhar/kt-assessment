package com.sridhar.telematics.assessment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sridhar.telematics.assessment.model.dao.LocationDao
import com.sridhar.telematics.assessment.model.entity.RealmLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LocationMapViewModel @Inject constructor(private val locationDao: LocationDao) : ViewModel()  {

    private var _realmLocations: MutableLiveData<MutableList<RealmLocation>> = MutableLiveData()
    val realmLocations: LiveData<MutableList<RealmLocation>> = _realmLocations

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
}