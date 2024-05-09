package com.example.cornerpocket.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Opponent: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var name: String = ""
    // var image??? - store image path
    var wins: Int = 0
    var losses:Int = 0
    var gamesHistory: RealmList<Game> = realmListOf()
}