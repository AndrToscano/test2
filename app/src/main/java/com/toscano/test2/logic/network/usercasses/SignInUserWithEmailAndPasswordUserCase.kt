package com.toscano.test2.logic.network.usercasses


import com.toscano.test2.data.network.entities.UsersDB
import com.toscano.test2.data.network.repository.AuthenticationRepository

class SignInUserWithEmailAndPasswordUserCase{

    suspend fun invoke  (email: String, password: String): UsersDB?{

        var userDB : UsersDB? = null
        AuthenticationRepository().createdUsers(email, password).onSuccess {

            userDB = it
        }.onFailure {
            userDB = null
        }

        return userDB
    }

}