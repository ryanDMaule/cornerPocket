package com.mauleCo.cornerpocket.Repositories

import com.mauleCo.cornerpocket.models.User
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

class UserRepository(passedRealm : Realm) {
    private val realm = passedRealm
    suspend fun insertUser(user : User){
        realm.write { copyToRealm(user, updatePolicy = UpdatePolicy.ALL) }
    }

    fun getUser() : User? {
        val queriedUser = realm.query<User>().first().find()
        if (queriedUser != null){
            return queriedUser
        }
        return null
    }

    suspend fun updateUser(name : String) {
        realm.write {
            val queriedUser = realm.query<User>().first().find()

            if (queriedUser != null){
                val queriedUserLatest = findLatest(queriedUser)

                if (queriedUserLatest != null){
                    queriedUserLatest.name = name
                    copyToRealm(queriedUserLatest, updatePolicy = UpdatePolicy.ALL)
                }
            }
        }
    }

}