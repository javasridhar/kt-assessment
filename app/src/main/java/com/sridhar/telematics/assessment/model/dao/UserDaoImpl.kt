package com.sridhar.telematics.assessment.model.dao

import com.sridhar.telematics.assessment.model.database.TelematicsDatabase
import com.sridhar.telematics.assessment.model.entity.RealmUser
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class UserDaoImpl @Inject constructor(telematicsDatabase: TelematicsDatabase): UserDao {
    override val realm: Realm = telematicsDatabase.initialize()
    override val clazz: KClass<RealmUser> = RealmUser::class
}