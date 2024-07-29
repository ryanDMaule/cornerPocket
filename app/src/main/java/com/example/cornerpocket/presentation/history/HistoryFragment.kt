package com.example.cornerpocket.presentation.history

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cornerpocket.Adapters.GameHistoryAdapter
import com.example.cornerpocket.R
import com.example.cornerpocket.Utils.FilterFunctions
import com.example.cornerpocket.Utils.FilterFunctions.Companion.createFilterList
import com.example.cornerpocket.Utils.FilterFunctions.Companion.createFiltersDialog
import com.example.cornerpocket.Utils.NavigationUtils
import com.example.cornerpocket.databinding.FragmentHistoryBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.viewModels.FilterViewModel
import com.google.android.material.sidesheet.SideSheetDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {
    //region GLOBAL VARIABLES
    private var _binding : FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val filterViewModel: FilterViewModel by navGraphViewModels(R.id.historyGraph)

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: GameHistoryAdapter
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region BACK PRESS
        binding.backButton.setOnClickListener {
            clearStuffs()
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                clearStuffs()
                findNavController().popBackStack()
            }
        })
        //endregion

        filterViewModel.filtersDialog = createFiltersDialog(context = requireContext(), li = layoutInflater, vm = filterViewModel)
        dialogButtonsHandling()

        if (filterViewModel.filteredGameList != null){
            populateGamesAdapter(filterViewModel.filteredGameList!!)
        } else if(filterViewModel.unfilteredGameList != null) {
            populateGamesAdapter(filterViewModel.filterGamesByMostRecent(filterViewModel.unfilteredGameList!!))
        } else {
            showLoadingDialogAndPerformTask()
        }

        binding.filtersButton.setOnClickListener {
            filterViewModel.filtersDialog!!.show()
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showLoadingDialogAndPerformTask() {
        // Inflate the dialog layout
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null)

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
                populateGames()
            }

            // Ensure the dialog is shown for at least .75 second
            delay(750)

            dialog.dismiss()
        }
    }

    private suspend fun populateGames() {
        filterViewModel.viewModelScope.launch {
            filterViewModel.getGames().collect { gamesList ->
                filterViewModel.unfilteredGameList = gamesList
                populateGamesAdapter(filterViewModel.filterGamesByMostRecent(gamesList))
            }
        }
    }

    /**
     * This will reset the filtered game list
     * Reset the selected opponent
     * Create a new filter dialog to remove all set filters
     * Then repopulate the recycler view with the original unfiltered game list
     *
     * @param dialog used to dismiss the dialog when finished.
     */
    private fun resetDialogFilterLogic(dialog : SideSheetDialog) {
        clearStuffs()

        filterViewModel.filtersDialog = createFiltersDialog(requireContext(), layoutInflater, filterViewModel)
        dialogButtonsHandling()

        populateGamesAdapter(filterViewModel.unfilteredGameList!!)
        dialog.dismiss()
    }

    /**
     * Stuff that needs to be cleared in certain places within the fragment
     */
    private fun clearStuffs(){
        filterViewModel.filteredGameList = null
        FilterFunctions.selectedOpponent = null
        filterViewModel.clearStoredFilters()
    }

    /**
     * Handles the dialog buttons unique to this fragment, the rest is handled in DialogUtils
     */
    private fun dialogButtonsHandling(){
        val btnResetFilters = FilterFunctions.btnResetFilters
        btnResetFilters?.setOnClickListener {
            resetDialogFilterLogic(filterViewModel.filtersDialog!!)
        }

        val btnApply = FilterFunctions.btnApply
        btnApply?.setOnClickListener {
            applyFilterLogic(filterViewModel.filtersDialog!!)
        }
    }

    /**
     * Creates the filtered list based on the applied filters
     * populates the recycler with this list
     * and saves the list to the filterViewModel
     *
     * @param dialog used to dismiss the dialog when finished.
     */
    private fun applyFilterLogic(dialog : SideSheetDialog){
        val filteredList = createFilterList(dialog, filterViewModel)

        filterViewModel.printStoredFilters()

        //region HISTORY ADAPTER
        populateGamesAdapter(filteredList)
        filterViewModel.filteredGameList = filteredList
        //endregion

        dialog.dismiss()
    }

    /**
     * Populates the recycler with a passed List
     * Handles the onClick for any of the items in the recycler
     *
     * @param list The passed List of Games used to populate the recycler
     */
    private fun populateGamesAdapter(list : MutableList<Game>) {
        try {
            recyclerView = binding.historyRecycler
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
            historyAdapter = GameHistoryAdapter(list, filterViewModel, requireContext())
            recyclerView.adapter = historyAdapter

            binding.resultsCounterText.text = getString(R.string.var_listResults, list.size.toString())

            //HANDLE ITEM CLICKED
            historyAdapter.onItemClicked = { game ->
                val bundle = Bundle().apply {
                    putString("gameKey", game._id.toHexString())
                }
                NavigationUtils.navigateAndClearBackStack(
                    findNavController(),
                    bundle,
                    R.id.action_historyFragment_to_historyGameDetailsFragment,
                    R.id.historyFragment
                )
            }
        } catch (e : Exception) {
            Log.e("HF", "EXCEPTION CAUGHT : $e")
        }
    }

}