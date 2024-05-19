package com.example.cornerpocket.viewModels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cornerpocket.MyApp
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.Opponent
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainViewModel: ViewModel() {

    private val realm = MyApp.realm

    val opponents = realm
        .query<Opponent>()
        .asFlow()
        .map { results ->
            results.list.toList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    fun getOpponents() : Flow<MutableList<Opponent>> {
        return realm.query<Opponent>().asFlow().map { it.list.toMutableList() }
    }

    suspend fun insertOpponent(opponent : Opponent){
        realm.write { copyToRealm(opponent) }
    }

    val newGame = Game().apply {
        //date = "12th March, 2024"
        opponent = selectedOpponent
        gameDuration = 709
        userWon = false
        gameType = "ENGLISH"
        userBroke = false
        userBallsPlayed = "RED"
        methodOfVictory = "STANDARD WIN"
        userBallsRemaining = 2
        opponentBallsRemaining = 0
    }

    var selectedOpponent : Opponent? = null
    var newGameGameType : String = ""
    var newGameLength : String = ""

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
                    opponent = opponent2
                    gameDuration = 397
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
                    opponent = opponent2
                    gameDuration = 709
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
                    opponent = opponent2
                    gameDuration = 612
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
                    opponent = opponent2
                    gameDuration = 911
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
                    opponent = opponent4
                    gameDuration = 402
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

//                copyToRealm(game1, updatePolicy = UpdatePolicy.ALL)
//                copyToRealm(game2, updatePolicy = UpdatePolicy.ALL)
//                copyToRealm(game3, updatePolicy = UpdatePolicy.ALL)
//                copyToRealm(game4, updatePolicy = UpdatePolicy.ALL)
//                copyToRealm(game5, updatePolicy = UpdatePolicy.ALL)

            }
        }
    }

}