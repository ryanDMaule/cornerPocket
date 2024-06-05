package com.example.cornerpocket.models

import android.R.attr.name
import android.util.Log
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime
import java.util.Locale
import kotlin.math.absoluteValue


class Game: RealmObject {
    //primary field may not be necessary as a game can never exist without an Opponent and an Opponent has a primary key
    @PrimaryKey var _id: ObjectId = ObjectId()
    var date: String = formatDate()
    var gameDuration: String = ""
    var userWon: Boolean = true
    var gameType: String = ""
    var userBroke: Boolean = true
    var userBallsPlayed: String? = null
    var methodOfVictory: String = ""
    var userBallsRemaining: Int? = null
    var opponentBallsRemaining: Int? = null
}

enum class gameType {
    AMERICAN,
    ENGLISH
}

enum class ballType {
    RED,
    YELLOW,
    SPOT,
    STRIPE
}

enum class victoryMethods {
    STANDARD_VICTORY,
    OPPONENT_FOUL
}

fun ordinalOf(i: Int): String {
    val iAbs = i.absoluteValue // if you want negative ordinals, or just use i
    return "$i" + if (iAbs % 100 in 11..13) "th" else when (iAbs % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

fun formatDate() : String {
    val date = LocalDateTime.now()
    return "${ordinalOf(date.dayOfMonth)} ${date.month.name.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase() }}, ${date.year}"
}