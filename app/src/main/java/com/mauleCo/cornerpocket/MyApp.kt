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

        val config = RealmConfiguration.Builder(
            schema = setOf(
                Game::class,
                Opponent::class,
                User::class
            )
        )
        .schemaVersion(2) // Increment version for the new schema
        .build()

        // Open the Realm
        realm = Realm.open(config)

    }

}