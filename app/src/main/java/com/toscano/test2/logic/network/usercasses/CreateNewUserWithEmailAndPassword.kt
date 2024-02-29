package com.toscano.test2.logic.network.usercasses

import com.toscano.test2.data.network.entities.UsersDB
import com.toscano.test2.data.network.repository.AuthenticationRepository

class CreateNewUserWithEmailAndPassword {

    suspend fun invoke(email: String, password: String): UsersDB?{

        var user  : UsersDB? = null
        AuthenticationRepository().createNewUsersWithEmailandPassword(email, password)
            .onSuccess {
                user = it
            }
            .onFailure {
                user = null
            }

        return user
    }
}