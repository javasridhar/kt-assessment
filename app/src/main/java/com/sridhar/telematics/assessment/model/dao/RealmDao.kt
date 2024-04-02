package com.sridhar.telematics.assessment.model.dao

import android.util.Log
import com.sridhar.telematics.assessment.model.entity.RealmUser
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface RealmDao<T : RealmObject> {

    val realm: Realm
    val clazz: KClass<T>

    suspend fun insert(entity: T) {
        realm.writeBlocking {
            copyToRealm(entity, UpdatePolicy.ALL)
        }.also {
            Log.d(clazz.simpleName, "1 record Inserted $entity")
        }
    }

    suspend fun insertAll(entities: List<T>) {
        entities.forEach {
            insert(it)
        }
    }

    suspend fun update(entity: T, isLoggedIn: Boolean? = false) {
        realm.writeBlocking {
            val user = copyFromRealm(entity)
            (user as RealmUser)?.isLoggedIn = isLoggedIn
            this.copyToRealm(user)
        }
    }

    suspend fun findAll(): RealmResults<T> {
        return realm.query(clazz).find()
    }

    suspend fun findById(id: String): T? {
        return realm.query(clazz, "id == $id", id).first().find()
    }

    suspend fun delete(entity: T) {
        realm.writeBlocking {
            findLatest(entity).also {
                delete(it!!)
            }
        }
    }

    suspend fun stream(): Flow<ResultsChange<T>> {
        return realm.query(clazz).asFlow()
    }

    suspend fun deleteAll() {
        realm.writeBlocking {
            val all = this.query(clazz).find()
            delete(all)
        }
    }
}