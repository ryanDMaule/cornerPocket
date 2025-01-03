package com.mauleCo.cornerpocket.presentation.stats

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.mauleCo.cornerpocket.Utils.FilterFunctions
import com.mauleCo.cornerpocket.Utils.HelperFunctions
import com.mauleCo.cornerpocket.R
import com.mauleCo.cornerpocket.databinding.FragmentStatsBinding
import com.mauleCo.cornerpocket.models.AMERICAN
import com.mauleCo.cornerpocket.models.ENGLISH
import com.mauleCo.cornerpocket.models.Game
import com.mauleCo.cornerpocket.viewModels.FilterViewModel
import com.google.android.material.sidesheet.SideSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatsFragment : Fragment() {

    //region GLOBAL VARIABLES
    private var _binding : FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val filterViewModel: FilterViewModel by viewModels()
    //endregion
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region BACK PRESS
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
        //endregion

        // TODO: remove this when the onDestroy fragment stuff works 
        filterViewModel.filteredGameList = null
        FilterFunctions.selectedOpponent = null

        createDialog()

        showLoadingDialogAndPerformTask()

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showLoadingDialogAndPerformTask() {
        // Inflate the dialog layout
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null)

        val dialogTitle: TextView = dialogView.findViewById(R.id.dialog_title)
        dialogTitle.text = getString(R.string.generating_stats)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false) // Prevents user from dismissing it manually
            .create()

        //prevents showing solid whit in the corners where the edges are rounded
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        // Use a coroutine to manage timing
        GlobalScope.launch(Dispatchers.Main) {
            // Perform the task asynchronously
            withContext(Dispatchers.IO) {
                populateStats()
            }

            // Ensure the dialog is shown for at least .75 second
            delay(750)

            dialog.dismiss()
        }
    }

    private suspend fun populateStats() {
        filterViewModel.viewModelScope.launch {
            filterViewModel.getGames().collect { gamesList ->
                filterViewModel.unfilteredGameList = gamesList
                formatStatSections()
            }
        }
    }

    private fun createDialog(){
        filterViewModel.filtersDialog = FilterFunctions.createFiltersDialog(
            context = requireContext(),
            li = layoutInflater,
            vm = filterViewModel
        )

        val btnResetFilters = FilterFunctions.btnResetFilters
        btnResetFilters?.setOnClickListener {
            resetDialogFilterLogic(filterViewModel.filtersDialog!!)
        }

        val btnApply = FilterFunctions.btnApply
        btnApply?.setOnClickListener {
            applyFilterLogic(filterViewModel.filtersDialog!!)
        }

        FilterFunctions.orderConstraint?.visibility = View.GONE

        binding.filtersButton.setOnClickListener {
            filterViewModel.filtersDialog!!.show()
        }
    }
    private fun resetDialogFilterLogic(dialog : SideSheetDialog) {
        filterViewModel.filteredGameList = null
        FilterFunctions.selectedOpponent = null
        createDialog()
        formatStatSections()
        dialog.dismiss()
    }
    private fun applyFilterLogic(dialog : SideSheetDialog){
        val filteredList = FilterFunctions.createFilterList(dialog, filterViewModel)
        filterViewModel.filteredGameList = filteredList
        formatStatSections()
        dialog.dismiss()
    }
    private fun formatStatSections(){
        var gameType : String? = null
        if (filterViewModel.filteredGameList != null){
            gameType = FilterFunctions.getGameTypeRadioResult(FilterFunctions.getSelectedRadioButtonText(filterViewModel.filtersDialog!!.findViewById(R.id.groupradio_gameType)!!))?.uppercase()

            val gamesBreaking = FilterFunctions.getSelectedRadioButtonText(filterViewModel.filtersDialog!!.findViewById(R.id.groupradio_breaking)!!)
            Log.e("SF", "gamesBreaking : $gamesBreaking")

            when(gamesBreaking.uppercase()) {
                "BOTH" -> {
                    binding.breakStatistics.parentConstraint.visibility = View.VISIBLE
                }

                else -> {
                    binding.breakStatistics.parentConstraint.visibility = View.GONE
                }
            }

        } else {
            //DEFAULT VIEW
            binding.gameStatistics.parentConstraint.visibility = View.VISIBLE
            binding.breakStatistics.parentConstraint.visibility = View.VISIBLE
            binding.ballStatistics.parentConstraint.visibility = View.GONE
        }

        populateGameStatisticsSection()
        populateBreakStatisticsSection()
        populateBallStatisticsSection(gameType)
    }
    private fun populateGameStatisticsSection() {
        val list : MutableList<Game> = getGamesList()

        //SET TOTAL GAME LENGTH
        val totalGameLength = filterViewModel.getTotalGameLength(list)
        binding.gameStatistics.gameLengthTotal.text = getString(R.string.var_total_duration, HelperFunctions.formatSecondsToHHMMSS(totalGameLength))

        //SET AVERAGE GAME LENGTH
        val averageGameLength = filterViewModel.getAverageGameLength(list)
        binding.gameStatistics.gameLength.text = getString(R.string.var_average_duration, HelperFunctions.formatSecondsToMMSS(averageGameLength))

        //TOTAL GAMES
        binding.gameStatistics.gamesTotal.text = list.size.toString()

        //USER WINS
        val userWins = filterViewModel.getUserWins(list)
        binding.gameStatistics.winsTotal.text = userWins.toString()
        val winPercentage = HelperFunctions.calculatePercentage(list.size, userWins)
        binding.gameStatistics.winsPercentage.text = getString(R.string.var_percentage, winPercentage)

        //USER LOSSES
        val userLosses = (list.size - userWins)
        binding.gameStatistics.lossesTotal.text = userLosses.toString()
        val lossPercentage = HelperFunctions.calculatePercentage(list.size, userLosses)
        binding.gameStatistics.lossesPercentage.text = getString(R.string.var_percentage, lossPercentage)

        //RECENT 5 GAMES
        formatRecentGames(list.reversed().take(5))
    }
    private fun getGamesList() : MutableList<Game> {
        val list: MutableList<Game> = if (filterViewModel.filteredGameList != null){
            filterViewModel.filteredGameList!!
        } else {
            filterViewModel.unfilteredGameList!!
        }
        return list
    }
    private fun formatRecentGames(gamesList : List<Game>){
        for (i in 0 until 5) {
            val recentGameImg = getImage(i)

            if(i >= gamesList.size){
                recentGameImg.setImageResource(R.drawable.na_img)
            } else {
                if (gamesList[i].userWon){
                    recentGameImg.setImageResource(R.drawable.win_img)
                } else {
                    recentGameImg.setImageResource(R.drawable.loss_img)
                }
            }
        }
    }
    private fun getImage(position: Int) : ImageView {
        return when(position) {
            0 -> binding.gameStatistics.opponentPreviousFiveSection.result1
            1 -> binding.gameStatistics.opponentPreviousFiveSection.result2
            2 -> binding.gameStatistics.opponentPreviousFiveSection.result3
            3 -> binding.gameStatistics.opponentPreviousFiveSection.result4
            4 -> binding.gameStatistics.opponentPreviousFiveSection.result5

            else -> throw Error()
        }
    }
    private fun populateBreakStatisticsSection() {
        val list : MutableList<Game> = getGamesList()

        //region GAMES BREAKING

        //GAMES BREAKING TOTAL
        val gamesBreaking = filterViewModel.getGamesUserBreaksList(list)
        binding.breakStatistics.gamesBreakingTotal.text = gamesBreaking.size.toString()

        //USER WINS
        var userWins = filterViewModel.getUserWins(gamesBreaking)
        binding.breakStatistics.winsBreakingTotal.text = userWins.toString()
        binding.breakStatistics.winsBreakingPercentage.text = getString(R.string.var_percentage,HelperFunctions.calculatePercentage(gamesBreaking.size, userWins))


        //USER LOSSES
        var userLosses = (gamesBreaking.size - userWins)
        binding.breakStatistics.lossesBreakingTotal.text = userLosses.toString()
        binding.breakStatistics.lossesBreakingPercentage.text = getString(R.string.var_percentage,HelperFunctions.calculatePercentage(gamesBreaking.size, userLosses))

        //endregion

        //region GAMES NOT BREAKING

        //GAMES NOT BREAKING TOTAL
        val gamesNotBreaking = filterViewModel.getGamesOpponentBreaksList(list)
        binding.breakStatistics.gamesNbTotal.text = gamesNotBreaking.size.toString()

        //USER WINS
        userWins = filterViewModel.getUserWins(gamesNotBreaking)
        binding.breakStatistics.winsNbTotal.text = userWins.toString()
        binding.breakStatistics.winsNbPercentage.text = getString(R.string.var_percentage,HelperFunctions.calculatePercentage(gamesNotBreaking.size, userWins))

        //USER LOSSES
        userLosses = (gamesNotBreaking.size - userWins)
        binding.breakStatistics.lossesNbTotal.text = userLosses.toString()
        binding.breakStatistics.lossesNbPercentage.text = getString(R.string.var_percentage,HelperFunctions.calculatePercentage(gamesNotBreaking.size, userLosses))

        //endregion

    }
    private fun populateBallStatisticsSection(gameType : String?) {
        Log.e("SF", "populateBallStatisticsSection : gameType : $gameType")

        val list : MutableList<Game> = getGamesList()

        if (gameType != null) {
            when(gameType){
                "BOTH" -> {
                    binding.ballStatistics.parentConstraint.visibility = View.GONE
                }

                ENGLISH -> {
                    binding.ballStatistics.parentConstraint.visibility = View.VISIBLE

                    //region RED BALLS

                    var ballString = getString(R.string.red)
                    binding.ballStatistics.ball1TextView.text = getString(R.string.var_games_with, ballString)
                    binding.ballStatistics.ball1Image.setImageResource(R.drawable.red_ball_img)
                    val redBallsList = filterViewModel.getGamesWithRedBalls(list)

                    //TOTAL GAMES
                    binding.ballStatistics.gamesBall1Total.text = redBallsList.size.toString()

                    //USER WINS
                    var userWins = filterViewModel.getUserWins(redBallsList)
                    binding.ballStatistics.winsBall1Total.text = userWins.toString()
                    binding.ballStatistics.winsBall1Percentage.text =  getString(R.string.var_percentage, HelperFunctions.calculatePercentage(redBallsList.size, userWins))

                    //USER LOSSES
                    var userLosses = (redBallsList.size - userWins)
                    binding.ballStatistics.lossesBall1Total.text = userLosses.toString()
                    binding.ballStatistics.lossesBall1Percentage.text = getString(R.string.var_percentage, HelperFunctions.calculatePercentage(redBallsList.size, userLosses))

                    //endregion

                    //region YELLOW BALLS

                    ballString = getString(R.string.yellow)
                    binding.ballStatistics.ball2Text.text = getString(R.string.var_games_with, ballString)
                    binding.ballStatistics.ball2Image.setImageResource(R.drawable.yellow_ball_img)
                    val yellowBallsList = filterViewModel.getGamesWithYellowBalls(list)

                    //TOTAL GAMES
                    binding.ballStatistics.gamesBall2Total.text = yellowBallsList.size.toString()

                    //USER WINS
                    userWins = filterViewModel.getUserWins(yellowBallsList)
                    binding.ballStatistics.winsBall2Total.text = userWins.toString()
                    binding.ballStatistics.winsBall2Percentage.text = getString(R.string.var_percentage, HelperFunctions.calculatePercentage(yellowBallsList.size, userWins))

                    //USER LOSSES
                    userLosses = (yellowBallsList.size - userWins)
                    binding.ballStatistics.lossesBall2Total.text = userLosses.toString()
                    binding.ballStatistics.lossesBall2Percentage.text = getString(R.string.var_percentage, HelperFunctions.calculatePercentage(yellowBallsList.size, userLosses))

                    //endregion
                }

                AMERICAN -> {
                    binding.ballStatistics.parentConstraint.visibility = View.VISIBLE

                    //region SOLID BALLS

                    var ballString = getString(R.string.spot)
                    binding.ballStatistics.ball1TextView.text = getString(R.string.var_games_with, ballString)
                    binding.ballStatistics.ball1Image.setImageResource(R.drawable.solid_ball_img)
                    val solidBallsList = filterViewModel.getGamesWithSolidBalls(list)

                    //TOTAL GAMES
                    binding.ballStatistics.gamesBall1Total.text = solidBallsList.size.toString()

                    //USER WINS
                    var userWins = filterViewModel.getUserWins(solidBallsList)
                    binding.ballStatistics.winsBall1Total.text = userWins.toString()
                    binding.ballStatistics.winsBall1Percentage.text = getString(R.string.var_percentage, HelperFunctions.calculatePercentage(solidBallsList.size, userWins))

                    //USER LOSSES
                    var userLosses = (solidBallsList.size - userWins)
                    binding.ballStatistics.lossesBall1Total.text = userLosses.toString()
                    binding.ballStatistics.lossesBall1Percentage.text = getString(R.string.var_percentage, HelperFunctions.calculatePercentage(solidBallsList.size, userLosses))

                    //endregion

                    //region STRIPE BALLS

                    ballString = getString(R.string.stripe)
                    binding.ballStatistics.ball2Text.text = getString(R.string.var_games_with, ballString)
                    binding.ballStatistics.ball2Image.setImageResource(R.drawable.stripe_ball_img)
                    val stripeBallsList = filterViewModel.getGamesWithStripedBalls(list)

                    //TOTAL GAMES
                    binding.ballStatistics.gamesBall2Total.text = stripeBallsList.size.toString()

                    //USER WINS
                    userWins = filterViewModel.getUserWins(stripeBallsList)
                    binding.ballStatistics.winsBall2Total.text = userWins.toString()
                    binding.ballStatistics.winsBall2Percentage.text = getString(R.string.var_percentage, HelperFunctions.calculatePercentage(stripeBallsList.size, userWins))

                    //USER LOSSES
                    userLosses = (stripeBallsList.size - userWins)
                    binding.ballStatistics.lossesBall2Total.text = userLosses.toString()
                    binding.ballStatistics.winsBall2Percentage.text = getString(R.string.var_percentage, HelperFunctions.calculatePercentage(stripeBallsList.size, userLosses))

                    //endregion
                }

                else -> {
                    binding.ballStatistics.parentConstraint.visibility = View.GONE
                }

            }
        } else {
            binding.ballStatistics.parentConstraint.visibility = View.GONE
        }

    }

}