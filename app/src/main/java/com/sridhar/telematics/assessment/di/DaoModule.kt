package com.sridhar.telematics.assessment.di

import com.sridhar.telematics.assessment.model.dao.LocationDao
import com.sridhar.telematics.assessment.model.dao.LocationDaoImpl
import com.sridhar.telematics.assessment.model.dao.UserDao
import com.sridhar.telematics.assessment.model.dao.UserDaoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DaoModule {
    @Binds
    abstract fun bindUserDao(impl: UserDaoImpl): UserDao

    @Binds
    abstract fun bindLocationDao(impl: LocationDaoImpl): LocationDao
}