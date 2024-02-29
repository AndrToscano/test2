package com.toscano.test2.logic.network.usercasses

import com.toscano.test2.data.network.entities.UsersDB
import com.toscano.test2.data.network.repository.UsersRepository

class GetUserDBByIDUserCase {

    suspend fun invoke(id: String): UsersDB?{

        /*
        var user: UsersDB? = null
        UsersRepository().getUserById(id)
            .onSuccess {
                user = it
            }
            .onFailure {
                user = null
            }
        return user
         */

        return UsersRepository().getUserById(id).getOrNull()
    }
}