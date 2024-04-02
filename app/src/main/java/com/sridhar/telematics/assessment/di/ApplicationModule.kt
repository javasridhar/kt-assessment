package com.sridhar.telematics.assessment.di

import com.sridhar.telematics.assessment.model.database.TelematicsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideTelematicsDatabase(): TelematicsDatabase = TelematicsDatabase()
}