package com.mauleCo.cornerpocket.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mauleCo.cornerpocket.MyApp
import com.mauleCo.cornerpocket.Repositories.GameRepository
import com.mauleCo.cornerpocket.Repositories.OpponentRepository
import com.mauleCo.cornerpocket.models.Game
import com.mauleCo.cornerpocket.models.Opponent
import com.mauleCo.cornerpocket.models.RED
import com.mauleCo.cornerpocket.models.SPOT
import com.mauleCo.cornerpocket.models.STRIPE
import com.mauleCo.cornerpocket.models.YELLOW
import com.google.android.material.sidesheet.SideSheetDialog
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

class FilterViewModel : ViewModel() {

    private val realm = MyApp.realm

    //region FILTERS

    var unfilteredGameList : MutableList<Game>? = null
    var filteredGameList : MutableList<Game>? = null

    var filtersDialog : SideSheetDialog? = null

    //endregion

    //region FILTER VALUES

    var f_opponent : Opponent? = null
    var f_gameType : String? = null
    var f_discipline : String? = null
    var f_results : String? = null
    var f_gamesBreaking : String? = null
    var f_order : String? = null

    fun printStoredFilters(){
        Log.e("FVM", "printStoredFilters")
        Log.d("FVM", "OPPONENT : $f_opponent")
        Log.d("FVM", "GAME TYPE : $f_gameType")
        Log.d("FVM", "DISCIPLINE : $f_discipline")
        Log.d("FVM", "RESULTS : $f_results")
        Log.d("FVM", "GAMES BREAKING : $f_gamesBreaking")
        Log.d("FVM", "ORDER : $f_order")
    }

    fun clearStoredFilters() {
        Log.e("FVM", "clearStoredFilters")
        f_opponent = null
        f_gameType = null
        f_discipline = null
        f_results = null
        f_gamesBreaking = null
        f_order = null
    }

    //endregion


    private val gameRepository: GameRepository by lazy {
        GameRepository(realm)
    }

    private val opponentRepository: OpponentRepository by lazy {
        OpponentRepository(realm)
    }
    fun getGames() : Flow<MutableList<Game>> {
        return gameRepository.getGames()
    }

    fun filterGamesByOldest(list : MutableList<Game>) : MutableList<Game>{
        return gameRepository.filterGamesByOldest(list = list)
    }

    fun filterGamesByMostRecent(list : MutableList<Game>) : MutableList<Game>{
        return gameRepository.filterGamesByMostRecent(list = list)
    }

    fun getOpponents() : Flow<MutableList<Opponent>> {
        return opponentRepository.getOpponents()
    }

    fun filterGames(list : MutableList<Game>, opponent : Opponent?, gameType : String?, subType : String?, userWins : Boolean?, userBreaks : Boolean?, orderNewest : Boolean) : MutableList<Game> {
        return gameRepository.filterGames(
            list = list,
            opponent = opponent,
            gameType = gameType,
            subType = subType,
            userWins = userWins,
            userBreaks = userBreaks,
            orderNewest = orderNewest
        )
    }

    fun getOpponentViaId(id : ObjectId) : Opponent? {
        return opponentRepository.getOpponentViaId(id = id)
    }

    fun getUserWins(list : MutableList<Game>) : Int {
        var wins = 0
        list.forEach {
            if (it.userWon){
                wins++
            }
        }
        return wins
    }

    fun getUserLosses(list : MutableList<Game>) : Int {
        var losses = 0
        list.forEach {
            if (!it.userWon){
                losses++
            }
        }
        return losses
    }

    fun getAverageGameLength(list : MutableList<Game>) : Int {
        var totalGameTime = 0
        list.forEach {
            totalGameTime += it.gameDuration
        }
        return if (list.size == 0){
            totalGameTime
        } else {
            totalGameTime/list.size
        }
    }

    fun getTotalGameLength(list : MutableList<Game>) : Int {
        var totalGameTime = 0
        list.forEach {
            totalGameTime += it.gameDuration
        }
        return totalGameTime
    }

    fun getGamesUserBreaksList(list : MutableList<Game>) : MutableList<Game> {
        val filteredList: MutableList<Game> = mutableListOf()
        list.forEach {
            if (it.userBroke) {
                filteredList.add(it)
            }
        }
        return filteredList
    }

    fun getGamesOpponentBreaksList(list : MutableList<Game>) : MutableList<Game> {
        val filteredList: MutableList<Game> = mutableListOf()
        list.forEach {
            if (!it.userBroke) {
                filteredList.add(it)
            }
        }
        return filteredList
    }

    fun getGamesWithRedBalls(list : MutableList<Game>) : MutableList<Game> {
        val filteredList: MutableList<Game> = mutableListOf()
        list.forEach {
            if (it.userBallsPlayed == RED) {
                filteredList.add(it)
            }
        }
        return filteredList
    }

    fun getGamesWithYellowBalls(list : MutableList<Game>) : MutableList<Game> {
        val filteredList: MutableList<Game> = mutableListOf()
        list.forEach {
            if (it.userBallsPlayed == YELLOW) {
                filteredList.add(it)
            }
        }
        return filteredList
    }

    fun getGamesWithSolidBalls(list : MutableList<Game>) : MutableList<Game> {
        val filteredList: MutableList<Game> = mutableListOf()
        list.forEach {
            if (it.userBallsPlayed == SPOT) {
                filteredList.add(it)
            }
        }
        return filteredList
    }

    fun getGamesWithStripedBalls(list : MutableList<Game>) : MutableList<Game> {
        val filteredList: MutableList<Game> = mutableListOf()
        list.forEach {
            if (it.userBallsPlayed == STRIPE) {
                filteredList.add(it)
            }
        }
        return filteredList
    }

}