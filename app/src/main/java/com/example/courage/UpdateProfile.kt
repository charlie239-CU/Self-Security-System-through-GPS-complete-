package com.example.courage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import com.jaredrummler.materialspinner.MaterialSpinner
import com.google.android.material.snackbar.Snackbar
import java.util.*
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder


class UpdateProfile : AppCompatActivity() {
    lateinit var addressField:EditText
    private lateinit var genderPicker:MaterialSpinner
    lateinit var picker: DatePicker
    lateinit var proceedButton:Button
    private var gender:String=""
    private var email:String=""
    private var username:String=""

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        addressField=findViewById(R.id.address)
        genderPicker=findViewById(R.id.spinner)
        picker=findViewById(R.id.datepicker)
        proceedButton=findViewById(R.id.save)
        genderPicker.setItems("Male","Female","Other");
        database = Firebase.database.reference
        auth= Firebase.auth
        database= FirebaseDatabase.getInstance("https://courage-4591a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()

        genderPicker.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            gender=item

        })

        
        val intent = intent
        username=intent.getStringExtra("username").toString()
        email=intent.getStringExtra("email").toString()
        Log.d("username",email)

        proceedButton.setOnClickListener {

            val address=addressField.text.toString()
            if(TextUtils.isEmpty(address)){
                return@setOnClickListener
            }
            val date=getCurrentDate()
            if(gender==""){
                gender="Male"
            }
            var userData=PersonalInfo(address=address,gender=gender,dob=date)

            database.child("personaldata").child(username).setValue(userData)
            database.child("users").child(username).child("flag").setValue("true")
            Firebase.auth.signOut()
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)

            finish()

        }

    }
    fun getCurrentDate(): String? {
        val builder = StringBuilder()
        builder.append(picker.getDayOfMonth().toString() + "/")
        builder.append((picker.getMonth() + 1).toString() + "/") //month is 0 based
        builder.append(picker.getYear())
        return builder.toString()
    }

    override fun onBackPressed() {
        Firebase.auth.signOut()
        val intent=Intent(this,Login::class.java)
        startActivity(intent)
    }

}