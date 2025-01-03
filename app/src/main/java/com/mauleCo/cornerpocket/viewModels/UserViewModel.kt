package com.mauleCo.cornerpocket.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mauleCo.cornerpocket.MyApp
import com.mauleCo.cornerpocket.Repositories.UserRepository
import com.mauleCo.cornerpocket.models.Opponent
import com.mauleCo.cornerpocket.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class UserViewModel : ViewModel()  {

    private val realm = MyApp.realm

    private val userRepository: UserRepository by lazy {
        UserRepository(realm)
    }
    fun getUser() : User? {
        return userRepository.getUser()
    }

    suspend fun updateUser(name : String) {
        userRepository.updateUser(name = name)
    }

    suspend fun insertUser(user : User){
        val existingUser = getUser()
        if (existingUser == null){
            Log.e("UVM", "NO USER! INSERTING : ${user.name}")
            userRepository.insertUser(user = user)
        } else {
//            Log.e("UVM", "USER POPULATED : ${existingUser.name}")
        }

    }

    init {
        Log.i("UVM", "init")
        val user = getUser()
        if (user != null){
//            Log.e("UVM", "USER POPULATED : ${user.name}")
        } else {
//            Log.e("UVM", "NO USER... CREATING NEW USER")
//            viewModelScope.launch(Dispatchers.IO) {
//                userRepository.insertUser()
//            }

              Log.e("UVM", "NO USER")
        }
    }

}