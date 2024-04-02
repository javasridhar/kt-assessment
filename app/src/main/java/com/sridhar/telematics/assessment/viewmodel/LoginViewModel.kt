package com.sridhar.telematics.assessment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sridhar.telematics.assessment.model.dao.UserDao
import com.sridhar.telematics.assessment.model.entity.RealmUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val dao: UserDao) : ViewModel() {

    private var _realmUser: MutableLiveData<RealmUser> = MutableLiveData()
    val realmUser: LiveData<RealmUser> = _realmUser

    fun insertUser(user: RealmUser) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.insert(user)
            }
        }
    }
}