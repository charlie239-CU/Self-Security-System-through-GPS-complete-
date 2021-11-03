package com.example.courage


import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException

//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Courage"
        private val TABLE_CONTACTS = "UserContact"
        private val KEY_ID = "id"
        private val KEY_NAME = "name"
        private val KEY_EMAIL = "email"
        private val KEY_PHONE = "phone"
        private val KEY_ADDRESS = "address"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT," + KEY_PHONE + " TEXT,"
                + KEY_ADDRESS + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }


    //method to insert data
    fun addContact(contact: ContactStructure):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, contact.name)
        contentValues.put(KEY_EMAIL,contact.email )
        contentValues.put(KEY_PHONE,contact.phone )
        contentValues.put(KEY_ADDRESS,contact.address )

        // Inserting Row
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to read data
    @SuppressLint("Range")
    fun viewContacts():List<ContactStructure>{
        val empList:ArrayList<ContactStructure> = ArrayList<ContactStructure>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var userId: Int
        var userName: String
        var userEmail: String
        var userPhone: String
        var userAddress: String
        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getInt(cursor.getColumnIndex("id"))
                userName = cursor.getString(cursor.getColumnIndex("name"))
                userEmail = cursor.getString(cursor.getColumnIndex("email"))
                userPhone = cursor.getString(cursor.getColumnIndex("phone"))
                userAddress = cursor.getString(cursor.getColumnIndex("address"))
                val emp= ContactStructure(name = userName, email = userEmail, address = userAddress,phone=userPhone)
                empList.add(emp)
            } while (cursor.moveToNext())
        }
        return empList
    }
    //method to update data
    fun updateEmployee(contact: ContactStructure):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, contact.name)
        contentValues.put(KEY_EMAIL,contact.email )
        contentValues.put(KEY_PHONE,contact.phone )
        contentValues.put(KEY_ADDRESS,contact.address )

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues,"email="+contact.email,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    //method to delete data
    fun deleteEmployee(contact: ContactStructure):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_EMAIL, contact.email) // EmpModelClass UserId
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS,"email="+contact.email,null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
    fun deleteAllData(){
        val db = this.writableDatabase
        db.execSQL("Delete from "+ TABLE_CONTACTS)
        db.execSQL("VACUUM")
        db.close()


    }
}