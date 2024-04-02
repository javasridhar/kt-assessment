package com.sridhar.telematics.assessment.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sridhar.telematics.assessment.model.dao.UserDao
import com.sridhar.telematics.assessment.model.entity.RealmUser
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val dao: UserDao) : ViewModel() {

    private var _realmUser: MutableLiveData<RealmUser?> = MutableLiveData()
    val realmUser: LiveData<RealmUser?> = _realmUser

    fun getCurrentUser() {
        var user: RealmUser? = null
        var users: RealmResults<RealmUser>
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                users = dao.findAll()
                if (users.isNotEmpty())
                    user = dao.findAll().first { it.isLoggedIn == true }
            }
            withContext(Dispatchers.Main) {
                _realmUser.value = user
            }
        }
    }
}