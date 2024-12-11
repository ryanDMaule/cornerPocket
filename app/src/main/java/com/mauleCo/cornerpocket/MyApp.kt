package com.mauleCo.cornerpocket

import android.app.Application
import android.util.Log
import com.mauleCo.cornerpocket.models.Game
import com.mauleCo.cornerpocket.models.Opponent
import com.mauleCo.cornerpocket.models.User
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MyApp: Application() {

    companion object{
        lateinit var realm: Realm
    }

    override fun onCreate() {
        super.onCreate()

        Log.i("MA", "OPEN REALM")

        realm = Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    Game::class,
                    Opponent::class,
                    User::class
                )
            )
        )
    }

}