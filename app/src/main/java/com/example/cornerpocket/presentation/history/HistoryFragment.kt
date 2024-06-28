package com.example.cornerpocket.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cornerpocket.Adapters.GameHistoryAdapter
import com.example.cornerpocket.FilterFunctions
import com.example.cornerpocket.FilterFunctions.Companion.createFiltersDialog
import com.example.cornerpocket.FilterFunctions.Companion.createFilterList
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.FragmentHistoryBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.viewModels.FilterViewModel
import com.google.android.material.sidesheet.SideSheetDialog
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private var _binding : FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val filterViewModel: FilterViewModel by navGraphViewModels(R.id.historyGraph)

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: GameHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: if the if is brought back in then the dialog and its filters will be retained when navigating back from the details fragment
        //  but future searches will not update the recycler view
//        if (filterViewModel.filtersDialog == null){
            filterViewModel.filtersDialog = createFiltersDialog(context = requireContext(), li = layoutInflater, vm = filterViewModel)

            val btnResetFilters = FilterFunctions.btnResetFilters
            btnResetFilters?.setOnClickListener {
                resetDialogFilterLogic(filterViewModel.filtersDialog!!)
            }

            val btnApply = FilterFunctions.btnApply
            btnApply?.setOnClickListener {
                applyFilterLogic(filterViewModel.filtersDialog!!)
            }
//        }

        filterViewModel.viewModelScope.launch {
            filterViewModel.getGames().collect { gamesList ->
                filterViewModel.unfilteredGameList = gamesList

//                if (filterViewModel.filteredGameList != null){
//                    populateGamesAdapter(filterViewModel.filteredGameList!!)
//                } else {
                    populateGamesAdapter(gamesList)
//                }

            }
        }

        binding.filtersButton.setOnClickListener {
            filterViewModel.filtersDialog!!.show()
        }

    }
    private fun resetDialogFilterLogic(dialog : SideSheetDialog) {
        filterViewModel.filteredGameList = null
        FilterFunctions.selectedOpponent = null
        filterViewModel.filtersDialog = createFiltersDialog(requireContext(), layoutInflater, filterViewModel)
        populateGamesAdapter(filterViewModel.unfilteredGameList!!)
        dialog.dismiss()
    }
    private fun applyFilterLogic(dialog : SideSheetDialog){
        val filteredList = createFilterList(dialog, filterViewModel)

        //region HISTORY ADAPTER
        populateGamesAdapter(filteredList)
        filterViewModel.filteredGameList = filteredList
        //endregion

        dialog.dismiss()
    }
    private fun populateGamesAdapter(list : MutableList<Game>) {
        recyclerView = binding.historyRecycler
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        historyAdapter = GameHistoryAdapter(list, filterViewModel)
        recyclerView.adapter = historyAdapter

        binding.resultsCounterText.text = "${list.size} RESULTS"

        //HANDLE ITEM CLICKED
        historyAdapter.onItemClicked = { game ->
            val bundle = Bundle().apply {
                putString("gameKey", game._id.toHexString())
            }
            findNavController().navigate(R.id.action_historyFragment_to_historyGameDetailsFragment, bundle)
        }
    }

}