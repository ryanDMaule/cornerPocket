package com.example.cornerpocket.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.util.Date

class Game: RealmObject {
    //primary field may not be necessary as a game can never exist without an Opponent and an Oppoonent has a primary key
    @PrimaryKey var _id: ObjectId = ObjectId()
    var date: Date? = null
    var opponent: Opponent? = null
    var gameDuration: Int = 0
    var userWon: Boolean = true
    var gameType: String = ""
    var userBroke: Boolean = true
    var userBallsPlayed: String = ""
    var methodOfVictory: String = ""
    var userBallsRemaining: Int = 0
    var opponentBallsRemaining: Int = 0
}