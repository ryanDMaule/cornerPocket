package com.example.cornerpocket.models

import android.content.res.Resources
import com.example.cornerpocket.R
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class Game: RealmObject {
    //primary field may not be necessary as a game can never exist without an Opponent and an Opponent has a primary key
    @PrimaryKey var _id: ObjectId = ObjectId()
    @Index // Allows queries to run faster when using this variable
    var opponentId: ObjectId = ObjectId()
    var date: Long = System.currentTimeMillis()
    var gameDuration: Int = 0
    var userWon: Boolean = true
    var gameType: String = defaultGameType
    var userBroke: Boolean = true
    var userBallsPlayed: String? = null
    var methodOfVictory: String = ""
}

// TODO: ALLOW THIS TO BE CHANGED IN SETTINGS 
var defaultGameType = "ENGLISH"

enum class gameType {
    AMERICAN,
    ENGLISH
}

enum class BallType {
    RED,
    YELLOW,
    SPOT,
    STRIPE
}

fun ballTypeLookup(v : BallType) : String {
    val res = Resources.getSystem()
    return when(v) {
        BallType.RED -> res.getString(R.string.red)
        BallType.YELLOW -> res.getString(R.string.yellow)
        BallType.SPOT -> res.getString(R.string.spot)
        BallType.STRIPE -> res.getString(R.string.stripe)
    }
}

enum class victoryMethods {
    STANDARD_VICTORY,
    OPPONENT_FOUL
}