package com.sridhar.telematics.assessment.model.database

import com.sridhar.telematics.assessment.model.entity.RealmLocation
import com.sridhar.telematics.assessment.model.entity.RealmUser
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

class TelematicsDatabase {

    @Singleton
    lateinit var database: Realm

    @Singleton
    fun initialize() : Realm {
        val realmConfig = RealmConfiguration.create(
            schema = setOf(RealmUser::class, RealmLocation::class)
        )
        database = Realm.open(realmConfig)
        return database
    }
}