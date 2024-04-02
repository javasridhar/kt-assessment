package com.sridhar.telematics.assessment.model.dao

import com.sridhar.telematics.assessment.model.database.TelematicsDatabase
import com.sridhar.telematics.assessment.model.entity.RealmLocation
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class LocationDaoImpl @Inject constructor(telematicsDatabase: TelematicsDatabase): LocationDao {
    override val realm: Realm = telematicsDatabase.initialize()
    override val clazz: KClass<RealmLocation> = RealmLocation::class
}