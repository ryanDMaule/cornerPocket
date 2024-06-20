package com.example.cornerpocket.viewModels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cornerpocket.MyApp
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.Opponent
import com.example.cornerpocket.models.User
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime
import kotlin.math.abs

class MainViewModel: ViewModel() {

    private val realm = MyApp.realm

    fun getUser() : User? {
        val queriedUser = realm.query<User>().first().find()
        return if (queriedUser != null){
            Log.i("MVM", "getUser: return user : $queriedUser")
            queriedUser
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                realm.write {
                    val user = User().apply {
                        name = "User"
                    }
                    copyToRealm(user, updatePolicy = UpdatePolicy.ALL)
                    Log.i("MVM", "getUser: create user : $user")

                    getUser()
                }
            }
            null
        }
    }

    fun updateUser(name : String) {
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                val queriedUser = realm.query<User>().first().find()

                if (queriedUser != null){
                    val queriedUserLatest = findLatest(queriedUser)

                    if (queriedUserLatest != null){
                        queriedUserLatest.name = name
                        copyToRealm(queriedUserLatest, updatePolicy = UpdatePolicy.ALL)
                    }
                }
            }
        }
    }

    fun getOpponents() : Flow<MutableList<Opponent>> {
        return realm.query<Opponent>().asFlow().map { it.list.toMutableList() }
    }
    fun getGames() : Flow<MutableList<Game>> {
        return realm.query<Game>().asFlow().map { it.list.toMutableList() }
    }

    fun filterGames(list : MutableList<Game>, opponent : Opponent?, gameType : String?, userWins : Boolean?, userBreaks : Boolean?, orderNewest : Boolean) : MutableList<Game> {
        var filteredList = list

        if (opponent != null){
            filteredList = filterGamesByOpponent(filteredList, opponent)
        }
        if (gameType != null){
            filteredList = filterGamesByGameType(filteredList, gameType)
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
            "ENGLISH" -> {
                //ENGLISH games played
                list.filter { it.gameType == "ENGLISH" }.toMutableList()
            }

            "AMERICAN" -> {
                //AMERICAN games played
                list.filter { it.gameType == "AMERICAN" }.toMutableList()
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

    suspend fun insertOpponent(opponent : Opponent){
        realm.write { copyToRealm(opponent) }
    }

    fun getOpponentMostRecentGame() : Game? {
        val queriedOpponent = realm.query<Opponent>("_id == $0", selectedOpponent!!._id).first().find()
        if (queriedOpponent != null){
            return queriedOpponent.gamesHistory[queriedOpponent.gamesHistory.size-1]
        }
        return null
    }

    fun getUpdatedOpponent() : Opponent? {
        val queriedOpponent = realm.query<Opponent>("_id == $0", selectedOpponent!!._id).first().find()
        if (queriedOpponent != null){
            return queriedOpponent
        }
        return null
    }

    fun updateOpponent(){
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                val queriedOpponent = this.query<Opponent>("_id == $0", selectedOpponent!!._id).first().find()

                if (queriedOpponent != null){
                    val queriedOpponentLatest = findLatest(queriedOpponent)

                    if (queriedOpponentLatest != null){
                        if (pUserWon != null){
                            if (pUserWon == true){
                                queriedOpponentLatest.losses++
                            } else {
                                queriedOpponent.wins++
                            }
                        }

                        queriedOpponentLatest.gamesHistory.add(createGame())
                        copyToRealm(queriedOpponentLatest, updatePolicy = UpdatePolicy.ALL)
                    }
                }
            }
        }
    }

    fun getOpponentViaId(id : ObjectId) : Opponent? {
        val queriedOpponent = realm.query<Opponent>("_id == $0", id).first().find()
        if (queriedOpponent != null){
            return queriedOpponent
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

    // region gameCreation
    private fun createGame() : Game {
        return Game().apply {
            opponentId = selectedOpponent!!._id
            gameDuration = pGameLength
            userWon = pUserWon == true
            gameType = pGameType
            userBroke = pUserBroke == true
            userBallsPlayed = pUserBallsPlayed
            methodOfVictory = pMethodOfVictory
            userBallsRemaining = pUserBallsRemaining
            opponentBallsRemaining = pOpponentBallsRemaining
        }
    }

    private var selectedOpponent : Opponent? = null
    fun getSelectedOpponent() : Opponent? {
        Log.e("MVM", "GET - SELECTED OPPONENT : $selectedOpponent")
        return selectedOpponent
    }
    fun setSelectedOpponent(opponent: Opponent) {
        Log.e("MVM", "SET - SELECTED OPPONENT : $selectedOpponent")
        selectedOpponent = opponent
    }

    // p = pending

    // TODO: USE DEFAULT GAME TYPE AS DEFAULT STRING
    private var pGameType : String = "ENGLISH"
    fun getGameType() : String {
        Log.e("MVM", "GET - GAME TYPE : $pGameType")
        return pGameType
    }
    fun setGameType(v : String) {
        Log.e("MVM", "SET - GAME TYPE : $v")
        pGameType = v
    }


    private var pUserBroke : Boolean? = null
    fun getUserBroke() : Boolean? {
        Log.e("MVM", "GET - USER BROKE : $pUserBroke")
        return pUserBroke
    }
    fun setUserBroke(v : Boolean){
        Log.e("MVM", "SET - USER BROKE : $v")
        pUserBroke = v
    }


    private var pGameLength : String = ""
    fun getGameLength() : String {
        Log.e("MVM", "GET - GAME LENGTH : $pGameLength")
        return pGameLength
    }
    fun setGameLength(v : String){
        Log.e("MVM", "SET - GAME LENGTH : $v")
        pGameLength = v
    }


    private var pUserWon : Boolean? = null
    fun getUserWon() : Boolean? {
        Log.e("MVM", "GET - USER WON : $pUserWon")
        return pUserWon
    }
    fun setUserWon(v : Boolean){
        Log.e("MVM", "SET - USER WON : $v")
        pUserWon = v
    }


    private var pMethodOfVictory : String = ""
    fun getMethodOfVictory() : String {
        Log.e("MVM", "GET - METHOD OF VICTORY : $pUserWon")
        return pMethodOfVictory
    }
    fun setMethodOfVictory(v : String){
        Log.e("MVM", "SET - METHOD OF VICTORY : $v")
        pMethodOfVictory = v
    }


    private var pUserBallsPlayed: String = ""
    fun getUserBallsPlayed() : String {
        Log.e("MVM", "GET - USER BALLS PLAYED : $pUserBallsPlayed")
        return pUserBallsPlayed
    }
    fun setUserBallsPlayed(v : String) {
        Log.e("MVM", "SET - USER BALLS PLAYED : $v")
        pUserBallsPlayed = v
    }


    private var pUserBallsRemaining: Int? = null
    fun getUserBallsRemaining() : Int? {
        Log.e("MVM", "GET - USER BALLS REMAINING : $pUserBallsRemaining")
        return pUserBallsRemaining
    }
    fun setUserBallsRemaining(v : Int) {
        Log.e("MVM", "SET - USER BALLS REMAINING : $v")
        pUserBallsRemaining = v
    }


    private var pOpponentBallsRemaining: Int? = null
    fun getOpponentBallsRemaining() : Int? {
        Log.e("MVM", "GET - OPPONENT BALLS REMAINING : $pOpponentBallsRemaining")
        return pOpponentBallsRemaining
    }
    fun setOpponentBallsRemaining(v : Int) {
        Log.e("MVM", "SET - OPPONENT BALLS REMAINING : $v")
        pOpponentBallsRemaining = v
    }

    fun printPendingGameValues(){
        Log.i("MVM", "GAME DURATION : ${getGameLength()}")
        Log.i("MVM", "USER WON : ${getUserWon()}")
        Log.i("MVM", "GAME TYPE : ${getGameType()}")
        Log.i("MVM", "USER BROKE : ${getUserBroke()}")
        Log.i("MVM", "USER BALLS PLAYED : ${getUserBallsPlayed()}")
        Log.i("MVM", "METHOD OF VICTORY : ${getMethodOfVictory()}")
        Log.i("MVM", "USER BALLS REMAINING : ${getUserBallsRemaining()}")
        Log.i("MVM", "OPPONENT BALLS REMAINING : ${getOpponentBallsRemaining()}")
    }

    // endregion

    init {
        Log.i("MVM", "init")

        //createSampleEntries()

        viewModelScope.launch {
            getOpponents().collect() {
                Log.i("MVM", "OPPONENTS LIST SIZE [ ${it.size} ]")
//                opponentsList.forEach {
//                    Log.i("LIST", "OPPONENT [ ${it.name} ]")
//                }
            }
        }

    }

    private fun createSampleEntries(){
        Log.i("MVM", "createSampleEntries")
        viewModelScope.launch {
            realm.write {
                val user = User().apply {
                    name = "User"
                }

                val opponent1 = Opponent().apply {
                    name = "Ryan"
                    wins = 0
                    losses = 0
                }
                val opponent2 = Opponent().apply {
                    name = "Kieron"
                    wins = 1
                    losses = 3
                }
                val opponent3 = Opponent().apply {
                    name = "Harrison"
                    wins = 0
                    losses = 0
                }
                val opponent4 = Opponent().apply {
                    name = "Aysha"
                    wins = 1
                    losses = 0
                }

                val game1 = Game().apply {
                    //date = LocalDateTime.now().toString()
                    //opponent = opponent2
                    gameDuration = "03:47"
                    userWon = true
                    gameType = "ENGLISH"
                    userBroke = true
                    userBallsPlayed = "RED"
                    methodOfVictory = "STANDARD WIN"
                    userBallsRemaining = 0
                    opponentBallsRemaining = 3
                }
                val game2 = Game().apply {
                    //date = "12th March, 2024"
                    //opponent = opponent2
                    gameDuration = "05:11"
                    userWon = false
                    gameType = "ENGLISH"
                    userBroke = false
                    userBallsPlayed = "RED"
                    methodOfVictory = "STANDARD WIN"
                    userBallsRemaining = 2
                    opponentBallsRemaining = 0
                }
                val game3 = Game().apply {
                    //date = "17th March, 2024"
                    //opponent = opponent2
                    gameDuration = "06:09"
                    userWon = false
                    gameType = "ENGLISH"
                    userBroke = true
                    userBallsPlayed = "YELLOW"
                    methodOfVictory = "OPPONENT FOUL"
                    userBallsRemaining = 4
                    opponentBallsRemaining = 2
                }
                val game4 = Game().apply {
                    //date = "23rd March, 2024"
                    //opponent = opponent2
                    gameDuration = "03:43"
                    userWon = false
                    gameType = "ENGLISH"
                    userBroke = false
                    userBallsPlayed = "YELLOW"
                    methodOfVictory = "STANDARD WIN"
                    userBallsRemaining = 1
                    opponentBallsRemaining = 0
                }
                val game5 = Game().apply {
                    //date = "11th April, 2024"
                    //opponent = opponent4
                    gameDuration = "04:44"
                    userWon = true
                    gameType = "AMERICAN"
                    userBroke = true
                    userBallsPlayed = "STRIPES"
                    methodOfVictory = "STANDARD WIN"
                    userBallsRemaining = 0
                    opponentBallsRemaining = 5
                }

                opponent2.gamesHistory.add(game1)
                opponent2.gamesHistory.add(game2)
                opponent2.gamesHistory.add(game3)
                opponent2.gamesHistory.add(game4)
                opponent4.gamesHistory.add(game5)

                copyToRealm(opponent1, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(opponent2, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(opponent3, updatePolicy = UpdatePolicy.ALL)
                copyToRealm(opponent4, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

}