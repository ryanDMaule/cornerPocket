package com.example.cornerpocket.viewModels

import androidx.lifecycle.ViewModel
import com.example.cornerpocket.MyApp
import com.example.cornerpocket.Repositories.GameRepository
import com.example.cornerpocket.Repositories.OpponentRepository
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.Opponent
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

    fun filterGames(list : MutableList<Game>, opponent : Opponent?, gameType : String?, userWins : Boolean?, userBreaks : Boolean?, orderNewest : Boolean) : MutableList<Game> {
        return gameRepository.filterGames(
            list = list,
            opponent = opponent,
            gameType = gameType,
            userWins = userWins,
            userBreaks = userBreaks,
            orderNewest = orderNewest
        )
    }

    fun getOpponentViaId(id : ObjectId) : Opponent? {
        return opponentRepository.getOpponentViaId(id = id)
    }

}