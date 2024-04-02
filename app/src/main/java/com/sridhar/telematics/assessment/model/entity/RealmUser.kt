package com.sridhar.telematics.assessment.model.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class RealmUser : RealmObject {

    @PrimaryKey
    var id: String = ""
    var name: String? = ""
    var email: String? = ""
    var photoUrl: String? = ""
    var isLoggedIn: Boolean? = false

    override fun toString(): String {
        return "RealmUser(id='$id', name=$name, email=$email, photoUrl=$photoUrl, isLoggedIn=$isLoggedIn)"
    }
}
