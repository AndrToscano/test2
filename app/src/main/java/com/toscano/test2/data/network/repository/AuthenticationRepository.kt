package com.toscano.test2.data.network.repository

import android.content.Intent
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.toscano.test2.data.network.entities.UsersDB
import com.toscano.test2.ui.activities.MainActivity2
import kotlinx.coroutines.tasks.await

class AuthenticationRepository {

    // Initialize Firebase Auth
    private val auth = Firebase.auth

    suspend fun createNewUsersWithEmailandPassword(user: String, password: String): Result<UsersDB?> = runCatching{

        var userDB : UsersDB? = null

        //Ingreso de Nuevos Usuarios
        val user = auth.createUserWithEmailAndPassword(user, password).await().user
        //val user = auth.currentUser

        if (user != null){
            userDB = UsersDB(user.uid, user.email!!, user.displayName.orEmpty())
        }

        return@runCatching userDB
    }


    suspend fun createdUsers(email: String, password: String): Result<UsersDB?> = runCatching{

        var userDB : UsersDB? = null

        val userFireBase = auth.signInWithEmailAndPassword(email, password).await().user

        if (userFireBase != null){
            userDB = UsersDB(userFireBase.uid, userFireBase.email!!, userFireBase.displayName.orEmpty())
        }

        return@runCatching userDB
    }
}