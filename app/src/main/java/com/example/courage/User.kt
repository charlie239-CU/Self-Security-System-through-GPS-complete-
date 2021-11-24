package com.example.courage

import com.google.firebase.database.Exclude

data class User(val name: String? = null, val email: String? = null,val phone:String?=null,val mapLocation:Map<String,String>?=null,val contacts:Any?=null,val flag:String?=null,val username:String?=null) {



    @JvmName("name")
    fun getName(): String? {
        return name
    }
    @JvmName("email")
    fun getEmail(): String? {
        return email
    }
    @JvmName("phone")
    fun getPhone(): String? {
        return phone
    }
    @JvmName("mapLocation")
    fun getMapLocation(): Map<String,String>? {
        return mapLocation
    }
    @JvmName("contacts")
    fun getContact(): Any? {
        return contacts
    }


}