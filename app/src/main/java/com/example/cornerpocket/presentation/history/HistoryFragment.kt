package com.example.cornerpocket.presentation.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cornerpocket.Adapters.GameHistoryAdapter
import com.example.cornerpocket.Adapters.OpponentListAdapter
import com.example.cornerpocket.HelperFunctions
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.FragmentHistoryBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.Opponent
import com.example.cornerpocket.viewModels.HistoryViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.sidesheet.SideSheetDialog
import kotlinx.coroutines.launch
import java.lang.Exception

class HistoryFragment : Fragment() {
    private var _binding : FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    //private val viewModel: MainViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by navGraphViewModels(R.id.historyGraph)

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: GameHistoryAdapter
    private lateinit var defaultList: List<Game>
    private lateinit var actv : AutoCompleteTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        binding.filtersButton.setOnClickListener {
            Toast.makeText(requireContext(), "FILTERS", Toast.LENGTH_SHORT).show()
        }

        recyclerView = binding.historyRecycler
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        historyViewModel.viewModelScope.launch {
            historyViewModel.getGames().collect{ gamesList ->
                binding.resultsCounterText.text = "${gamesList.size} RESULTS"

                defaultList = gamesList
                historyAdapter = GameHistoryAdapter(historyViewModel.filterGamesByMostRecent(gamesList), historyViewModel)
                recyclerView.adapter = historyAdapter

                historyAdapter.onItemClicked = { game ->
                    Toast.makeText(requireContext(), "${game._id}", Toast.LENGTH_SHORT).show()
                    val bundle = Bundle().apply {
                        putString("gameKey", game._id.toHexString())
                    }
                    // TODO:
                    findNavController().navigate(R.id.action_historyFragment_to_historyGameDetailsFragment, bundle)
                }
            }
        }

        var selectedOpponent : Opponent? = null

        binding.filtersButton.setOnClickListener {
            val dialog = SideSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.filters_sheet, null)

            //OPPONENT DROP DOWN BUTTON
            actv = view.findViewById(R.id.actv)
            historyViewModel.viewModelScope.launch {
                historyViewModel.getOpponents().collect { opponents ->
                    // Create and set the adapter
                    try {
                        val adapter = OpponentListAdapter(requireContext(), opponents.toMutableList())
                        actv.setAdapter(adapter)
                        actv.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, _: View, i: Int, _: Long ->
                            val selectedPerson = adapter.getItem(i) as? Opponent
                            selectedPerson?.let {
                                actv.setText(it.name, false) // Display only the name
                                if (selectedPerson.name == "All") {
                                    selectedOpponent = null
                                } else {
                                    // Handle the selectedPerson object
                                    selectedPerson.let {
                                        selectedOpponent = selectedPerson
                                    }
                                }
                            }
                        }
                    } catch (e : Exception){
                        //CAUSES A CRASH IF YOU FILTER RESULTS ON HISTORY PAGE GO PLAY A GAME AND CRASHES ON "RETURN TO MENU"
                        Log.e("HF", "EXCEPTION CAUGHT : $e")
                    }
                }
            }

            //region FILTER LOGIC

            val btnClose = view.findViewById<ImageView>(R.id.quit_button)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            //OPPONENT SELECT - FILTER
            val opponentFilterButton = view.findViewById<ImageView>(R.id.opponentFilterAddButton)
            val opponentFilterExpansion = view.findViewById<ConstraintLayout>(R.id.opponent_filter_expansion)
            opponentFilterButton.setOnClickListener {
                filterClick(opponentFilterButton, opponentFilterExpansion)
            }
            
            //GAME TYPE - FILTER
            val gameTypeFilterButton = view.findViewById<ImageView>(R.id.gameTypeFilterAddButton)
            val gameTypeFilterExpansion = view.findViewById<ConstraintLayout>(R.id.gameType_filter_expansion)
            gameTypeFilterButton.setOnClickListener {
                filterClick(gameTypeFilterButton, gameTypeFilterExpansion)
            }

            //RESULTS - FILTER
            val resultsFilterButton = view.findViewById<ImageView>(R.id.results_filter_add_button)
            val resultsFilterExpansion = view.findViewById<ConstraintLayout>(R.id.results_filter_expansion)
            resultsFilterButton.setOnClickListener {
                filterClick(resultsFilterButton, resultsFilterExpansion)
            }

            //BREAK - FILTER
            val breakingFilterButton = view.findViewById<ImageView>(R.id.breaking_filter_add_button)
            val breakingFilterExpansion = view.findViewById<ConstraintLayout>(R.id.breaking_filter_expansion)
            breakingFilterButton.setOnClickListener {
                filterClick(breakingFilterButton, breakingFilterExpansion)
            }

            //ORDER - FILTER
            val orderFilterButton = view.findViewById<ImageView>(R.id.order_filter_add_button)
            val orderFilterExpansion = view.findViewById<ConstraintLayout>(R.id.order_filter_expansion)
            orderFilterButton.setOnClickListener {
                filterClick(orderFilterButton, orderFilterExpansion)
            }

            val btnApply = view.findViewById<MaterialButton>(R.id.footer_button)
            btnApply.setOnClickListener {
                // TODO: APPLY SELECTED FILTERS
                val filteredList = historyViewModel.filterGames(
                    defaultList.toMutableList(),
                    opponent = selectedOpponent,
                    gameType = getGameTypeRadioResult(getSelectedRadioButtonText(view.findViewById(R.id.groupradio_gameType))),
                    userWins = getUserWonRadioResult(getSelectedRadioButtonText(view.findViewById(R.id.groupradio_results))),
                    userBreaks = getUserBreaksRadioResult(getSelectedRadioButtonText(view.findViewById(R.id.groupradio_breaking))),
                    orderNewest = getOrderRadioResult(getSelectedRadioButtonText(view.findViewById(R.id.groupradio_order))))
                Log.i("HF", "filteredList size : ${filteredList.size}")

                historyAdapter = GameHistoryAdapter(filteredList, historyViewModel)
                recyclerView.adapter = historyAdapter
                binding.resultsCounterText.text = "${filteredList.size} RESULTS"

                historyAdapter.onItemClicked = { game ->
                    Toast.makeText(requireContext(), "${game._id}", Toast.LENGTH_SHORT).show()
                    val bundle = Bundle().apply {
                        putString("gameKey", game._id.toHexString())
                    }
                    findNavController().navigate(R.id.action_historyFragment_to_historyGameDetailsFragment, bundle)
                }

                dialog.dismiss()
            }
            
            //endregion

            dialog.setContentView(view)
            dialog.show()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getSelectedRadioButtonText(radioGroup: RadioGroup): String {
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        val selectedRadioButton = radioGroup.findViewById<RadioButton>(selectedRadioButtonId)
        return selectedRadioButton.text.toString()
    }

    private fun getGameTypeRadioResult(selectedOption : String) : String? {
        return when(selectedOption.uppercase()){
            "ENGLISH" -> {
                "ENGLISH"
            }

            "AMERICAN" -> {
                "AMERICAN"
            }

            "BOTH" -> {
                null
            }

            else -> {
                null
            }
        }
    }
    private fun getUserWonRadioResult(selectedOption : String) : Boolean? {
        return when(selectedOption.uppercase()){
            "WINS" -> {
                true
            }

            "LOSSES" -> {
                false
            }

            "BOTH" -> {
                null
            }

            else -> {
                null
            }
        }
    }
    private fun getUserBreaksRadioResult(selectedOption : String) : Boolean? {
        return when(selectedOption.uppercase()){
            "ME" -> {
                true
            }

            "OPPONENT" -> {
                false
            }

            "BOTH" -> {
                null
            }

            else -> {
                null
            }
        }
    }
    private fun getOrderRadioResult(selectedOption : String) : Boolean {
        return when(selectedOption){
            "Date: Newest to oldest" -> {
                true
            }

            "Date: Oldest to newest" -> {
                false
            }

            else -> {
                true
            }
        }
    }


    private fun filterClick(button : ImageView, expansionZone : View) {
        // CURRENTLY NOT EXPANDED '+'
        if (expansionZone.visibility != View.VISIBLE){
            HelperFunctions.expandView(expansionZone)
            HelperFunctions.rotate45clockwise(button)
        }
        // CURRENTLY EXPANDED 'X'
        else {
            HelperFunctions.collapseView(expansionZone)
            HelperFunctions.rotate45anticlockwise(button)
        }
    }

}