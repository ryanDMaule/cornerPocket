package com.example.cornerpocket.Repositories

import com.example.cornerpocket.models.User
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

class UserRepository(passedRealm : Realm) {
    private val realm = passedRealm

    suspend fun insertUser() {
        realm.write {
            val user = User().apply {
                name = "User"
            }
            copyToRealm(user, updatePolicy = UpdatePolicy.ALL)
        }
    }

//    suspend fun getUser() : User? {
//        val queriedUser = realm.query<User>().first().find()
//        return if (queriedUser != null){
//            Log.i("MVM", "getUser: return user : $queriedUser")
//            queriedUser
//        } else {
//            realm.write {
//                val user = User().apply {
//                    name = "User"
//                }
//                copyToRealm(user, updatePolicy = UpdatePolicy.ALL)
//                Log.i("MVM", "getUser: create user : $user")
//
////                getUser()
//            }
//            null
//        }
//    }

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