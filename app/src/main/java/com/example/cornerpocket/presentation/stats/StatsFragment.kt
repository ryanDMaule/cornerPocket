package com.example.cornerpocket.presentation.stats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.example.cornerpocket.FilterFunctions
import com.example.cornerpocket.HelperFunctions
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.FragmentStatsBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.viewModels.FilterViewModel
import com.google.android.material.sidesheet.SideSheetDialog
import kotlinx.coroutines.launch

class StatsFragment : Fragment() {
    private var _binding : FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val filterViewModel: FilterViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: remove this when the onDestroy fragment stuff works 
        filterViewModel.filteredGameList = null
        FilterFunctions.selectedOpponent = null

        createDialog()

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
        if (filterViewModel.filteredGameList != null){

            val gameType = FilterFunctions.getGameTypeRadioResult(FilterFunctions.getSelectedRadioButtonText(filterViewModel.filtersDialog!!.findViewById(R.id.groupradio_gameType)!!))
            Log.e("SF", "gameType : $gameType")

            if (gameType != null) {
                when(gameType.uppercase()){
                    "BOTH" -> {
                        binding.ballStatistics.parentConstraint.visibility = View.GONE
                    }

                    else -> {
                        binding.ballStatistics.parentConstraint.visibility = View.VISIBLE
                    }

                }
            } else {
                binding.ballStatistics.parentConstraint.visibility = View.VISIBLE
            }


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
        populateBallStatisticsSection()
    }

    private fun populateGameStatisticsSection() {
        var list : MutableList<Game>? = if (filterViewModel.filteredGameList != null){
            filterViewModel.filteredGameList
        } else {
            filterViewModel.unfilteredGameList
        }

        if (list != null){
            //SET AVERAGE GAME LENGTH
            val averageGameLength = filterViewModel.getAverageGameLength(list)
            binding.gameStatistics.gameLength.text = "Average game duration : ${HelperFunctions.formatSecondsToMMSS(averageGameLength)}"

            //TOTAL GAMES
            binding.gameStatistics.gamesTotal.text = list.size.toString()

            //USER WINS
            val userWins = filterViewModel.getUserWins(list)
            binding.gameStatistics.winsTotal.text = userWins.toString()
            binding.gameStatistics.winsPercentage.text = "${HelperFunctions.calculatePercentage(list.size, userWins)}%"

            //USER LOSSES
            val userLosses = (list.size - userWins)
            binding.gameStatistics.lossesTotal.text = userLosses.toString()
            binding.gameStatistics.lossesPercentage.text = "${HelperFunctions.calculatePercentage(list.size, userLosses)}%"

            //RECENT 5 GAMES
            formatRecentGames(list.reversed().take(5))
        } else {
            Log.e("SF", "LIST IS NULL")
        }

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

    }

    private fun populateBallStatisticsSection() {

    }

}