package com.toscano.test2.logic.network.usercasses

import com.toscano.test2.data.network.entities.UsersDB
import com.toscano.test2.data.network.repository.UsersRepository

class SaveUserInDBUserCase {

    suspend fun invoke(id: String, email: String, name: String): UsersDB?{
       /*
        var user: UsersDB? = null
        UsersRepository().saveUserDB(id, email, name)
            .onSuccess {
                user = it
            }
            .onFailure {
                user = null
            }
        return user

        */
       return UsersRepository().saveUserDB(id, email, name).getOrNull()
    }
}