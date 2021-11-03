package com.example.courage

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission

class ContactHelper {
    companion object{
        @SuppressLint("Range")
        public fun getContacts(contentResolver:ContentResolver): MutableMap<String,String> {
            var map:MutableMap<String,String> = mutableMapOf()
            val builder = StringBuilder()
            val resolver: ContentResolver = contentResolver;
            val cursor = resolver.query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null,
                null)

            if (cursor!!.count > 0) {
                while (cursor!!.moveToNext()) {
                    val id = cursor.getString(cursor!!.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNumber = (cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                    if (phoneNumber > 0) {
                        val cursorPhone = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                        if(cursorPhone!!.count > 0) {
                            while (cursorPhone!!.moveToNext()) {
                                val phoneNumValue = cursorPhone!!.getString(
                                    cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                builder.append("Contact: ").append(name).append(", Phone Number: ").append(
                                    phoneNumValue).append("\n\n")
                                //Log.e("Name ===>",phoneNumValue);
                                map.put(name,phoneNumValue)
                            }
                        }
                        cursorPhone.close()
                    }
                }
            } else {
                //   toast("No contacts available!")
            }
            cursor!!.close()
            return map
        }
    }
}