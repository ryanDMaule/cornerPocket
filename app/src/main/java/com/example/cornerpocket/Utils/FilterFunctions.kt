package com.example.cornerpocket.Utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewModelScope
import com.example.cornerpocket.Adapters.OpponentListAdapter
import com.example.cornerpocket.R
import com.example.cornerpocket.models.AMERICAN
import com.example.cornerpocket.models.EIGHT_BALl
import com.example.cornerpocket.models.ENGLISH
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.NINE_BALl
import com.example.cornerpocket.models.Opponent
import com.example.cornerpocket.viewModels.FilterViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.sidesheet.SideSheetDialog
import kotlinx.coroutines.launch

class FilterFunctions {
    companion object {
        var selectedOpponent : Opponent? = null
        var btnResetFilters : ImageView? = null
        var btnApply : MaterialButton? = null
        var orderConstraint : ConstraintLayout? = null
        fun createFiltersDialog(context: Context, li : LayoutInflater, vm : FilterViewModel) : SideSheetDialog {
            val dialog = SideSheetDialog(context)
            val view = li.inflate(R.layout.filters_sheet, null)

            //region OPPONENT SELECT DROPDOWN

            //OPPONENT DROP DOWN BUTTON
            val actv = view.findViewById<AutoCompleteTextView>(R.id.actv)
            vm.viewModelScope.launch {
                vm.getOpponents().collect { opponents ->
                    // Create and set the adapter
                    try {
                        val adapter = OpponentListAdapter(context, opponents.toMutableList())
                        actv.setAdapter(adapter)
                        actv.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, _: View, i: Int, _: Long ->
                            val selectedPerson = adapter.getItem(i) as? Opponent
                            selectedPerson?.let {
                                actv.setText(it.name, false) // Display only the name
                                if (selectedPerson.name == context.getString(R.string.all)) {
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
                        Log.e("FF", "EXCEPTION CAUGHT : $e")
                    }
                }
            }
            //endregion

            //region ON CLICK LOGIC

            val btnClose = view.findViewById<ImageView>(R.id.quit_button)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            btnResetFilters = view.findViewById(R.id.clear_filters_button)
//            btnResetFilters.setOnClickListener {
//                //HANDLE IN SPECIFIC FRAGMENT
//            }

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

            //SUB TYPE - FILTER
            val subTypeFilterButton = view.findViewById<ImageView>(R.id.disciplineFilterAddButton)
            val subTypeFilterExpansion = view.findViewById<ConstraintLayout>(R.id.discipline_filter_expansion)
            subTypeFilterButton.setOnClickListener {
                filterClick(subTypeFilterButton, subTypeFilterExpansion)
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
            orderConstraint = view.findViewById(R.id.order_filter_constraint)
            val orderFilterButton = view.findViewById<ImageView>(R.id.order_filter_add_button)
            val orderFilterExpansion = view.findViewById<ConstraintLayout>(R.id.order_filter_expansion)
            orderFilterButton.setOnClickListener {
                filterClick(orderFilterButton, orderFilterExpansion)
            }

            //endregion

            btnApply = view.findViewById(R.id.footer_button)
//            btnApply.setOnClickListener {
//                //HANDLE IN SPECIFIC FRAGMENT
//            }

            dialog.setContentView(view)
            return dialog
        }

        fun getSelectedRadioButtonText(radioGroup: RadioGroup): String {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val selectedRadioButton = radioGroup.findViewById<RadioButton>(selectedRadioButtonId)
            return selectedRadioButton.text.toString()
        }

        fun getGameTypeRadioResult(selectedOption : String) : String? {
            return when(selectedOption.uppercase()){
                ENGLISH -> {
                    ENGLISH
                }

                AMERICAN -> {
                    AMERICAN
                }

                "BOTH" -> {
                    null
                }

                else -> {
                    null
                }
            }
        }
        fun getSubTypeRadioResult(selectedOption : String) : String? {
            return when(selectedOption){
                "8-Ball" -> {
                    EIGHT_BALl
                }

                "9-Ball" -> {
                    NINE_BALl
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
        fun getUserBreaksRadioResult(selectedOption : String) : Boolean? {
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
        fun createFilterList(dialog : SideSheetDialog, fvm: FilterViewModel): MutableList<Game> {

            return fvm.filterGames(
                fvm.unfilteredGameList!!.toMutableList(),
                opponent = selectedOpponent,
                gameType = getGameTypeRadioResult(getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_gameType)!!)),
                subType =  getSubTypeRadioResult(getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_discipline)!!)),
                userWins = getUserWonRadioResult(getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_results)!!)),
                userBreaks = getUserBreaksRadioResult(
                    getSelectedRadioButtonText(dialog.findViewById(
                        R.id.groupradio_breaking
                    )!!)
                ),
                orderNewest = getOrderRadioResult(getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_order)!!))
            )
        }

    }

}