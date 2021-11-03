package com.example.courage

import com.google.firebase.database.Exclude

data class ContactStructure(val name: String? = null, val email: String? = null,val address:String?=null,val phone:String?=null) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "address" to address

        )
    }

}