package com.sridhar.telematics.assessment.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.sridhar.telematics.assessment.R
import com.sridhar.telematics.assessment.databinding.ActivityMainBinding
import com.sridhar.telematics.assessment.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainViewModel.getCurrentUser()
        mainViewModel.realmUser.observe(this) { user ->
            if (user != null) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.realmUser.removeObservers(this)
    }
}