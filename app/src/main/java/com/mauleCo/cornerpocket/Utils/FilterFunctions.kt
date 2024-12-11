package com.mauleCo.cornerpocket.Utils

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
import com.mauleCo.cornerpocket.Adapters.OpponentListAdapter
import com.mauleCo.cornerpocket.R
import com.mauleCo.cornerpocket.models.AMERICAN
import com.mauleCo.cornerpocket.models.EIGHT_BALl
import com.mauleCo.cornerpocket.models.ENGLISH
import com.mauleCo.cornerpocket.models.Game
import com.mauleCo.cornerpocket.models.NINE_BALl
import com.mauleCo.cornerpocket.models.Opponent
import com.mauleCo.cornerpocket.viewModels.FilterViewModel
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
                        Log.e("FF", "EXCEPTION CAUGHT : $e ")
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
            if (selectedOpponent == null){
                Log.e("FF", "selectedOpponent == null ++++++++++++++++++++++")
                collapseZone(opponentFilterButton, opponentFilterExpansion)
            } else {
                Log.e("FF", "selectedOpponent != null ++++++++++++++++++++++")
                expandZone(opponentFilterButton, opponentFilterExpansion)
                if (vm.f_opponent != null){
                    actv.setText(vm.f_opponent!!.name, false)
                }
            }

            //region GAME TYPE - FILTER
            val gameTypeFilterButton = view.findViewById<ImageView>(R.id.gameTypeFilterAddButton)
            val gameTypeFilterExpansion = view.findViewById<ConstraintLayout>(R.id.gameType_filter_expansion)
            gameTypeFilterButton.setOnClickListener {
                filterClick(gameTypeFilterButton, gameTypeFilterExpansion)
            }
            //BOTH
            if (view.findViewById<RadioButton>(R.id.radio_gameType_1).text == vm.f_gameType){
                view.findViewById<RadioButton>(R.id.radio_gameType_1).isChecked = true
                collapseZone(gameTypeFilterButton, gameTypeFilterExpansion)
            }
            //ENGLISH
            if (view.findViewById<RadioButton>(R.id.radio_gameType_2).text == vm.f_gameType){
                view.findViewById<RadioButton>(R.id.radio_gameType_2).isChecked = true
                expandZone(gameTypeFilterButton, gameTypeFilterExpansion)
            }
            //AMERICAN
            if (view.findViewById<RadioButton>(R.id.radio_gameType_3).text == vm.f_gameType){
                view.findViewById<RadioButton>(R.id.radio_gameType_3).isChecked = true
                expandZone(gameTypeFilterButton, gameTypeFilterExpansion)
            }
            //endregion

            //region SUB TYPE - FILTER
            val subTypeFilterButton = view.findViewById<ImageView>(R.id.disciplineFilterAddButton)
            val subTypeFilterExpansion = view.findViewById<ConstraintLayout>(R.id.discipline_filter_expansion)
            subTypeFilterButton.setOnClickListener {
                filterClick(subTypeFilterButton, subTypeFilterExpansion)
            }
            //BOTH
            if (view.findViewById<RadioButton>(R.id.radio_discipline_1).text == vm.f_discipline){
                view.findViewById<RadioButton>(R.id.radio_discipline_1).isChecked = true
                collapseZone(subTypeFilterButton, subTypeFilterExpansion)
            }
            //8-BALL
            if (view.findViewById<RadioButton>(R.id.radio_discipline_2).text == vm.f_discipline){
                view.findViewById<RadioButton>(R.id.radio_discipline_2).isChecked = true
                expandZone(subTypeFilterButton, subTypeFilterExpansion)
            }
            //9-BALL
            if (view.findViewById<RadioButton>(R.id.radio_discipline_3).text == vm.f_discipline){
                view.findViewById<RadioButton>(R.id.radio_discipline_3).isChecked = true
                expandZone(subTypeFilterButton, subTypeFilterExpansion)
            }
            //endregion

            //region RESULTS - FILTER
            val resultsFilterButton = view.findViewById<ImageView>(R.id.results_filter_add_button)
            val resultsFilterExpansion = view.findViewById<ConstraintLayout>(R.id.results_filter_expansion)
            resultsFilterButton.setOnClickListener {
                filterClick(resultsFilterButton, resultsFilterExpansion)
            }
            //BOTH
            if (view.findViewById<RadioButton>(R.id.radio_results_1).text == vm.f_results){
                view.findViewById<RadioButton>(R.id.radio_results_1).isChecked = true
                collapseZone(resultsFilterButton, resultsFilterExpansion)
            }
            //WINS
            if (view.findViewById<RadioButton>(R.id.radio_results_2).text == vm.f_results){
                view.findViewById<RadioButton>(R.id.radio_results_2).isChecked = true
                expandZone(resultsFilterButton, resultsFilterExpansion)
            }
            //LOSSES
            if (view.findViewById<RadioButton>(R.id.radio_results_3).text == vm.f_results){
                view.findViewById<RadioButton>(R.id.radio_results_3).isChecked = true
                expandZone(resultsFilterButton, resultsFilterExpansion)
            }
            //endregion

            //region BREAK - FILTER
            val breakingFilterButton = view.findViewById<ImageView>(R.id.breaking_filter_add_button)
            val breakingFilterExpansion = view.findViewById<ConstraintLayout>(R.id.breaking_filter_expansion)
            breakingFilterButton.setOnClickListener {
                filterClick(breakingFilterButton, breakingFilterExpansion)
            }
            //BOTH
            if (view.findViewById<RadioButton>(R.id.radio_breaking_1).text == vm.f_gamesBreaking){
                view.findViewById<RadioButton>(R.id.radio_breaking_1).isChecked = true
                collapseZone(breakingFilterButton, breakingFilterExpansion)
            }
            //ME
            if (view.findViewById<RadioButton>(R.id.radio_breaking_2).text == vm.f_gamesBreaking){
                view.findViewById<RadioButton>(R.id.radio_breaking_2).isChecked = true
                expandZone(breakingFilterButton, breakingFilterExpansion)
            }
            //OPPONENT
            if (view.findViewById<RadioButton>(R.id.radio_breaking_3).text == vm.f_gamesBreaking){
                view.findViewById<RadioButton>(R.id.radio_breaking_3).isChecked = true
                expandZone(breakingFilterButton, breakingFilterExpansion)
            }
            //endregion

            //region ORDER - FILTER
            orderConstraint = view.findViewById(R.id.order_filter_constraint)
            val orderFilterButton = view.findViewById<ImageView>(R.id.order_filter_add_button)
            val orderFilterExpansion = view.findViewById<ConstraintLayout>(R.id.order_filter_expansion)
            orderFilterButton.setOnClickListener {
                filterClick(orderFilterButton, orderFilterExpansion)
            }
            //NEWEST TO OLDEST
            if (view.findViewById<RadioButton>(R.id.radio_order_1).text == vm.f_order){
                view.findViewById<RadioButton>(R.id.radio_order_1).isChecked = true
                collapseZone(orderFilterButton, orderFilterExpansion)
            }
            //OLDEST TO NEWEST
            if (view.findViewById<RadioButton>(R.id.radio_order_2).text == vm.f_order){
                view.findViewById<RadioButton>(R.id.radio_order_2).isChecked = true
                expandZone(orderFilterButton, orderFilterExpansion)
            }
            //endregion

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
                expandZone(button, expansionZone)
            }
            // CURRENTLY EXPANDED 'X'
            else {
                collapseZone(button, expansionZone)
            }
        }
        private fun expandZone(button : ImageView, expansionZone : View){
            HelperFunctions.expandView(expansionZone)
            HelperFunctions.rotate45clockwise(button)
        }
        private fun collapseZone(button : ImageView, expansionZone : View){
            HelperFunctions.collapseView(expansionZone)
            HelperFunctions.rotate45anticlockwise(button)
        }
        fun createFilterList(dialog : SideSheetDialog, fvm: FilterViewModel): MutableList<Game> {

            fvm.f_opponent = selectedOpponent
            fvm.f_gameType = getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_gameType)!!)
            fvm.f_discipline = getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_discipline)!!)
            fvm.f_results = getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_results)!!)
            fvm.f_gamesBreaking = getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_breaking)!!)
            fvm.f_order = getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_order)!!)

            return fvm.filterGames(
                fvm.unfilteredGameList!!.toMutableList(),
                opponent = selectedOpponent,
                gameType = getGameTypeRadioResult(getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_gameType)!!)),
                subType =  getSubTypeRadioResult(getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_discipline)!!)),
                userWins = getUserWonRadioResult(getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_results)!!)),
                userBreaks = getUserBreaksRadioResult(getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_breaking)!!)),
                orderNewest = getOrderRadioResult(getSelectedRadioButtonText(dialog.findViewById(R.id.groupradio_order)!!))
            )
        }

    }

}