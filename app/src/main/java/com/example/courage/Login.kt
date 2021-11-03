package com.example.courage

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    lateinit var emailField:EditText
    lateinit var passField:EditText
    lateinit var loginButton:Button
    lateinit var signUpButton: TextView
    private lateinit var database: DatabaseReference
    lateinit var logo:ImageView
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        emailField=findViewById(R.id.email)
        passField=findViewById(R.id.password)
        loginButton=findViewById(R.id.login)
        signUpButton=findViewById(R.id.register)
        database = Firebase.database.reference
        logo=findViewById(R.id.logo)
        auth = Firebase.auth
        Helper.createLocationRequest(this)
        database= FirebaseDatabase.getInstance("https://courage-4591a-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()

        val currentUser = auth.currentUser
        if(currentUser != null){
            //Toast.makeText(baseContext, "already login.", Toast.LENGTH_SHORT).show()
            val intent=Intent(this,AfterSplash::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener {
            val email:String=emailField.text.toString()
            val password:String=passField.text.toString()
            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                return@setOnClickListener
                //Helper.makeToast(activity = Activity(),"Empty Email and password field")

            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(baseContext, "Successfull login.",
                            Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        user?.let {
                            // Name, email address, and profile photo Url
                            val name = user.displayName
                            val email = user.email
                            val photoUrl = user.photoUrl

                            // Check if user's email is verified
                            val emailVerified = user.isEmailVerified

                            // The user's ID, unique to the Firebase project. Do NOT use this value to
                            // authenticate with your backend server, if you have one. Use
                            // FirebaseUser.getToken() instead.
                            val uid = user.uid
//                            Toast.makeText(baseContext, name.toString(),
//                                Toast.LENGTH_SHORT).show()
                        }

                        //Firebase.auth.signOut()
                        val intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)
                       // updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("login", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }
                }
        }
        signUpButton.setOnClickListener {
            var intent=Intent(this,singup::class.java)
            startActivity(intent)
        }
    }
}