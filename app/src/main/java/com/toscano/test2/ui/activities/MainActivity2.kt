package com.toscano.test2.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.toscano.test2.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    //Declaramos la variable user
    private lateinit var auth : FirebaseAuth

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserData()
        initListeners()
    }

    private fun initListeners(){
        binding.btnLogOut.setOnClickListener {
            logOut()
        }
    }
    private fun getUserData(){

        val user = Firebase.auth.currentUser

        user?.let {
            // Name, email address, and profile photo Url
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            // Check if user's email is verified
            val emailVerified = it.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = it.uid

            Log.d("TAG", email.toString())
            Log.d("TAG", uid.toString())
        }
    }

    private fun logOut(){
        auth.signOut()
        startActivity(Intent(this, MainActivity::class.java))
    }
}