package com.example.courage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text


class ProfileFragment : Fragment() {
    lateinit var nameField: TextView
    lateinit var addressField: TextView
    lateinit var emailField: TextView
    lateinit var genderField: TextView
    lateinit var phoneField: TextView
    lateinit var dobField: TextView
    lateinit var logoutButton: TextView
    lateinit var addContact:Button
    lateinit var mapButton:Button

    private lateinit var activity: Activity
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameField=view.findViewById(R.id.profile_name)
        addressField=view.findViewById(R.id.profile_address)
        emailField=view.findViewById(R.id.profile_email)
        genderField=view.findViewById(R.id.gender) //change it
        phoneField=view.findViewById(R.id.profile_phone) //change it
        dobField=view.findViewById(R.id.profile_dob)

        logoutButton=view.findViewById(R.id.logout)
        addContact=view.findViewById<Button>(R.id.nav_contact_button)
        mapButton=view.findViewById(R.id.mapButton)
        activity=requireActivity()
        val sharedPreference =  activity.getSharedPreferences("Contact_sqlite", Context.MODE_PRIVATE)
        val databaseHandler: DatabaseHandler= DatabaseHandler(activity)
        auth = Firebase.auth
        database = Firebase.database.reference


        addressField.setText(Helper.personalUserData!!.address)
        genderField.setText(Helper.personalUserData!!.gender)
        dobField.setText(Helper.personalUserData!!.dob)
        nameField.setText(Helper.userData.name)
        emailField.setText(Helper.userData.email)
        phoneField.setText(Helper.userData.phone)

        logoutButton.setOnClickListener {

            Firebase.auth.signOut()
            Helper.userData= User()
            Helper.userList= mutableListOf()
            Helper.usernameList= listOf()
            var editor = sharedPreference.edit()
            editor.putString("operation", "false")
            editor.commit()
            databaseHandler.deleteAllData()
            val intent=Intent(activity,Login::class.java)
            startActivity(intent)
        }


    }
}