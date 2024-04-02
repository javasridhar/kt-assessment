package com.sridhar.telematics.assessment.model.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class RealmLocation : RealmObject {

    @PrimaryKey
    var id: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun toString(): String {
        return "RealmLocation(id='$id', latitude=$latitude, longitude=$longitude)"
    }
}