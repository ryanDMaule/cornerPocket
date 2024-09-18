package com.example.cornerpocket.Repositories

import com.example.cornerpocket.models.AMERICAN
import com.example.cornerpocket.models.EIGHT_BALl
import com.example.cornerpocket.models.ENGLISH
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.NINE_BALl
import com.example.cornerpocket.models.Opponent
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import kotlin.math.abs

class GameRepository(passedRealm : Realm) {
    private val realm = passedRealm

    fun getGames() : Flow<MutableList<Game>> {
        return realm.query<Game>().asFlow().map { it.list.toMutableList() }
    }

    fun filterGames(list : MutableList<Game>, opponent : Opponent?, gameType : String?, subType : String?, userWins : Boolean?, userBreaks : Boolean?, orderNewest : Boolean) : MutableList<Game> {
        var filteredList = list

        if (opponent != null){
            filteredList = filterGamesByOpponent(filteredList, opponent)
        }
        if (gameType != null){
            filteredList = filterGamesByGameType(filteredList, gameType)
        }
        if (subType != null){
            filteredList = filterGamesBySubType(filteredList, subType)
        }
        if (userWins != null){
            filteredList = filterGamesByResult(filteredList, userWins)
        }
        if (userBreaks != null) {
            filteredList = filterGamesByUserBreaks(filteredList, userBreaks)
        }
        filteredList = if (orderNewest){
            filterGamesByMostRecent(filteredList)
        } else {
            filterGamesByOldest(filteredList)
        }

        return filteredList
    }

    private fun filterGamesByOpponent(list : MutableList<Game>, opponent: Opponent) : MutableList<Game>{ //
        return list.filter { it.opponentId == opponent._id }.toMutableList()
    }

    private fun filterGamesByGameType(list : MutableList<Game>, gameType : String) : MutableList<Game>{
        return when(gameType){
            ENGLISH -> {
                //ENGLISH games played
                list.filter { it.gameType == ENGLISH }.toMutableList()
            }

            AMERICAN -> {
                //AMERICAN games played
                list.filter { it.gameType == AMERICAN }.toMutableList()
            }
            else -> {
                list
            }
        }
    }

    private fun filterGamesBySubType(list : MutableList<Game>, gameType : String) : MutableList<Game>{
        return when(gameType){
            EIGHT_BALl -> {
                //ENGLISH games played
                list.filter { it.subType == EIGHT_BALl }.toMutableList()
            }

            NINE_BALl -> {
                //AMERICAN games played
                list.filter { it.subType == NINE_BALl }.toMutableList()
            }
            else -> {
                list
            }
        }
    }

    private fun filterGamesByResult(list : MutableList<Game>, userWins : Boolean) : MutableList<Game>{
        return when(userWins){
            true -> {
                //games user won
                list.filter { it.userWon }.toMutableList()
            }

            false -> {
                //games user lost
                list.filter { !it.userWon }.toMutableList()
            }
        }
    }

    private fun filterGamesByUserBreaks(list : MutableList<Game>, breaks : Boolean) : MutableList<Game>{
        return when(breaks){
            true -> {
                //games user broke
                list.filter { it.userBroke }.toMutableList()
            }

            false -> {
                //games opponentBroke
                list.filter { !it.userBroke }.toMutableList()
            }
        }
    }

    fun filterGamesByOldest(list : MutableList<Game>) : MutableList<Game>{
        list.sortedBy { game ->
            abs(game.date - System.currentTimeMillis())
        }
        return list.toMutableList()
    }

    fun filterGamesByMostRecent(list : MutableList<Game>) : MutableList<Game>{
        return filterGamesByOldest(list).reversed().toMutableList()
    }

    fun getOpponentMostRecentGame(passedOpponent : Opponent) : Game? {
        val queriedOpponent = realm.query<Opponent>("_id == $0", passedOpponent._id).first().find()
        if (queriedOpponent != null){
            return queriedOpponent.gamesHistory[queriedOpponent.gamesHistory.size-1]
        }
        return null
    }

    fun getGameViaId(id : ObjectId) : Game? {
        val queriedGame = realm.query<Game>("_id == $0", id).first().find()
        if (queriedGame != null){
            return queriedGame
        }
        return null
    }

    fun createGame(
        selectedOpponent : Opponent,
        pGameLength : Int,
        pUserWon : Boolean,
        pGameType : String,
        pSubType : String,
        pUserBroke : Boolean,
        pUserBallsPlayed : String,
        pMethodOfVictory : String,
    ) : Game {
        return Game().apply {
            opponentId = selectedOpponent._id
            gameDuration = pGameLength
            userWon = pUserWon == true
            gameType = pGameType
            subType = pSubType
            userBroke = pUserBroke == true
            userBallsPlayed = pUserBallsPlayed
            methodOfVictory = pMethodOfVictory
        }
    }

    suspend fun removeGame(game : Game) {
        realm.write {
            val latestGame = findLatest(game)
            if (latestGame != null) {
                this.delete(latestGame)
            }
        }
    }

}