package com.example.cornerpocket.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Opponent: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    @Index // Allows queries to run faster when using this variable
    var name: String = ""
    var wins: Int = 0
    var losses:Int = 0
    var gamesHistory: RealmList<Game> = realmListOf()
    @Ignore //not stored in DB
    var recyclerOpponentSelected : Boolean = false
}