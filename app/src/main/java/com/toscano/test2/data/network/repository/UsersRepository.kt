package com.toscano.test2.data.network.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.toscano.test2.data.network.entities.UsersDB
import kotlinx.coroutines.tasks.await

class UsersRepository {

    //Inicializacion de FireStore
    private val db = Firebase.firestore
    suspend fun saveUserDB(id: String, email: String, name: String) = runCatching{

        val user = UsersDB(id, email, name)
        db.collection("Users").add(user).await()

        return@runCatching user
    }

    suspend fun getUserById(id: String) = runCatching{

        val user = UsersDB(id, "", "")

        //Busqeuda de un usuairo por ID
        return@runCatching  db.collection("Users").document(user.id).get()
            .await<DocumentSnapshot>()?.toObject<UsersDB>(UsersDB::class.java)

    }

    suspend fun updateUserById(id: String) = runCatching{

        val user = UsersDB(id, "", "")

        //Busqeuda de un usuairo por ID
        val modif = db.collection("Users").document(user.id).get()
            .await<DocumentSnapshot>()?.toObject<UsersDB>(UsersDB::class.java)

        if (modif != null){
            modif!!.name= "andres"
            db.collection("Users").document(modif!!.id).set(modif)
        }

    }
}