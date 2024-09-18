package com.example.cornerpocket.models

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
    var gameType: String = ENGLISH
    var subType: String = EIGHT_BALl
    var userBroke: Boolean = true
    var userBallsPlayed: String? = null
    var methodOfVictory: String = ""
}

const val EIGHT_BALl = "8-BALL"
const val NINE_BALl = "9-BALL"

const val AMERICAN = "AMERICAN"
const val SPOT = "SPOT"
const val STRIPE = "STRIPE"

const val ENGLISH = "ENGLISH"
const val RED = "RED"
const val YELLOW = "YELLOW"
