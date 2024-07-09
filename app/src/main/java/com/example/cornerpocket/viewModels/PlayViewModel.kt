package com.example.cornerpocket.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cornerpocket.MyApp
import com.example.cornerpocket.Repositories.GameRepository
import com.example.cornerpocket.Repositories.OpponentRepository
import com.example.cornerpocket.Repositories.UserRepository
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.Opponent
import com.example.cornerpocket.models.User
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

class PlayViewModel: ViewModel() {

    private val realm = MyApp.realm

    private val gameRepository: GameRepository by lazy {
        GameRepository(realm)
    }

    private val opponentRepository: OpponentRepository by lazy {
        OpponentRepository(realm)
    }

    private val userRepository: UserRepository by lazy {
        UserRepository(realm)
    }

    fun getUser() : User? {
        return userRepository.getUser()
    }

    fun getOpponents() : Flow<MutableList<Opponent>> {
        return opponentRepository.getOpponents()
    }
    suspend fun insertOpponent(opponent : Opponent){
        opponentRepository.insertOpponent(opponent = opponent)
    }

    fun getOpponentMostRecentGame() : Game? {
        return gameRepository.getOpponentMostRecentGame(passedOpponent = selectedOpponent!!)
    }

    fun getUpdatedOpponent() : Opponent? {
        return opponentRepository.getUpdatedOpponent(passedOpponent = selectedOpponent!!)
    }

    fun updateOpponentName(opponent: Opponent, name : String){
        viewModelScope.launch(Dispatchers.IO) {
            realm.write {
                val queriedOpponent = this.query<Opponent>("_id == $0", opponent._id).first().find()

                if (queriedOpponent != null){
                    val queriedOpponentLatest = findLatest(queriedOpponent)

                    if (queriedOpponentLatest != null){
                        queriedOpponentLatest.name = name
                        copyToRealm(queriedOpponentLatest, updatePolicy = UpdatePolicy.ALL)
                    }
                }
            }
        }
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
        return opponentRepository.getOpponentViaId(id = id)
    }

    fun removeOpponent(opponent: Opponent?){
        Log.e("PVM", "removeOpponent : $opponent")

        viewModelScope.launch(Dispatchers.IO) {
            if (opponent != null){
                opponentRepository.removeOpponent(opponent)
            } else {
                Log.e("PVM", "OPPONENT IS NULL")
            }
        }
    }

    fun removeGame(game : Game?){
        Log.e("PVM", "removeGame : $game")

        viewModelScope.launch(Dispatchers.IO) {
            if (game != null){
                gameRepository.removeGame(game)
            } else {
                Log.e("PVM", "GAME IS NULL")
            }
        }
    }

    fun getGameViaId(id : ObjectId) : Game? {
        return gameRepository.getGameViaId(id = id)
    }

    // region gameCreation
    private fun createGame() : Game {
        return gameRepository.createGame(
            selectedOpponent = selectedOpponent!!,
            pGameLength = pGameLength,
            pUserWon = pUserWon == true,
            pGameType = pGameType,
            pSubType = pSubType,
            pUserBroke = pUserBroke == true,
            pUserBallsPlayed = pUserBallsPlayed,
            pMethodOfVictory = pMethodOfVictory,
        )
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

    private var pSubType : String = "ENGLISH"
    fun getSubType() : String {
        Log.e("MVM", "GET - SUB TYPE : $pSubType")
        return pGameType
    }
    fun setSubType(v : String) {
        Log.e("MVM", "SET - SUB TYPE : $v")
        pSubType = v
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


    private var pGameLength : Int = 0
    fun getGameLength() : Int {
        Log.e("MVM", "GET - GAME LENGTH : $pGameLength")
        return pGameLength
    }
    fun setGameLength(v : Int){
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

    fun printPendingGameValues(){
        Log.i("MVM", "GAME DURATION : ${getGameLength()}")
        Log.i("MVM", "USER WON : ${getUserWon()}")
        Log.i("MVM", "GAME TYPE : ${getGameType()}")
        Log.i("MVM", "SUB TYPE : ${getSubType()}")
        Log.i("MVM", "USER BROKE : ${getUserBroke()}")
        Log.i("MVM", "USER BALLS PLAYED : ${getUserBallsPlayed()}")
        Log.i("MVM", "METHOD OF VICTORY : ${getMethodOfVictory()}")
    }

    // endregion

    init {
        Log.i("MVM", "init")

        //createSampleEntries()

//        viewModelScope.launch {
//            getOpponents().collect() {
//                Log.i("MVM", "OPPONENTS LIST SIZE [ ${it.size} ]")
//                opponentsList.forEach {
//                    Log.i("LIST", "OPPONENT [ ${it.name} ]")
//                }
//            }
//        }

    }

//    private fun createSampleEntries(){
//        Log.i("MVM", "createSampleEntries")
//        viewModelScope.launch {
//            realm.write {
//                val user = User().apply {
//                    name = "User"
//                }
//
//                val opponent1 = Opponent().apply {
//                    name = "Ryan"
//                    wins = 0
//                    losses = 0
//                }
//                val opponent2 = Opponent().apply {
//                    name = "Kieron"
//                    wins = 1
//                    losses = 3
//                }
//                val opponent3 = Opponent().apply {
//                    name = "Harrison"
//                    wins = 0
//                    losses = 0
//                }
//                val opponent4 = Opponent().apply {
//                    name = "Aysha"
//                    wins = 1
//                    losses = 0
//                }
//
//                val game1 = Game().apply {
//                    //date = LocalDateTime.now().toString()
//                    //opponent = opponent2
//                    gameDuration = 211
//                    userWon = true
//                    gameType = "ENGLISH"
//                    userBroke = true
//                    userBallsPlayed = "RED"
//                    methodOfVictory = "STANDARD WIN"
//                    userBallsRemaining = 0
//                    opponentBallsRemaining = 3
//                }
//                val game2 = Game().apply {
//                    //date = "12th March, 2024"
//                    //opponent = opponent2
//                    gameDuration = 436
//                    userWon = false
//                    gameType = "ENGLISH"
//                    userBroke = false
//                    userBallsPlayed = "RED"
//                    methodOfVictory = "STANDARD WIN"
//                    userBallsRemaining = 2
//                    opponentBallsRemaining = 0
//                }
//                val game3 = Game().apply {
//                    //date = "17th March, 2024"
//                    //opponent = opponent2
//                    gameDuration = 243
//                    userWon = false
//                    gameType = "ENGLISH"
//                    userBroke = true
//                    userBallsPlayed = "YELLOW"
//                    methodOfVictory = "OPPONENT FOUL"
//                    userBallsRemaining = 4
//                    opponentBallsRemaining = 2
//                }
//                val game4 = Game().apply {
//                    //date = "23rd March, 2024"
//                    //opponent = opponent2
//                    gameDuration = 235
//                    userWon = false
//                    gameType = "ENGLISH"
//                    userBroke = false
//                    userBallsPlayed = "YELLOW"
//                    methodOfVictory = "STANDARD WIN"
//                    userBallsRemaining = 1
//                    opponentBallsRemaining = 0
//                }
//                val game5 = Game().apply {
//                    //date = "11th April, 2024"
//                    //opponent = opponent4
//                    gameDuration = 125
//                    userWon = true
//                    gameType = "AMERICAN"
//                    userBroke = true
//                    userBallsPlayed = "STRIPES"
//                    methodOfVictory = "STANDARD WIN"
//                    userBallsRemaining = 0
//                    opponentBallsRemaining = 5
//                }
//
//                opponent2.gamesHistory.add(game1)
//                opponent2.gamesHistory.add(game2)
//                opponent2.gamesHistory.add(game3)
//                opponent2.gamesHistory.add(game4)
//                opponent4.gamesHistory.add(game5)
//
//                copyToRealm(opponent1, updatePolicy = UpdatePolicy.ALL)
//                copyToRealm(opponent2, updatePolicy = UpdatePolicy.ALL)
//                copyToRealm(opponent3, updatePolicy = UpdatePolicy.ALL)
//                copyToRealm(opponent4, updatePolicy = UpdatePolicy.ALL)
//            }
//        }
//    }

}