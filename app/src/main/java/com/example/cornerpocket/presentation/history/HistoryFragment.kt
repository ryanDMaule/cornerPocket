package com.example.cornerpocket.presentation.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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

        //region BACK PRESS
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
        //endregion


        // TODO: if the if is brought back in then the dialog and its filters will be retained when navigating back from the details fragment
        //  but future searches will not update the recycler view
//        if (filterViewModel.filtersDialog == null){
            filterViewModel.filtersDialog = createFiltersDialog(context = requireContext(), li = layoutInflater, vm = filterViewModel)
            dialogButtonsHandling()
//        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        filterViewModel.viewModelScope.launch {
            filterViewModel.getGames().collect { gamesList ->
                filterViewModel.unfilteredGameList = gamesList

//                if (filterViewModel.filteredGameList != null){
//                    populateGamesAdapter(filterViewModel.filteredGameList!!)
//                } else {
                    populateGamesAdapter(filterViewModel.filterGamesByMostRecent(gamesList))
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
        dialogButtonsHandling()

        populateGamesAdapter(filterViewModel.unfilteredGameList!!)
        dialog.dismiss()
    }

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

    private fun applyFilterLogic(dialog : SideSheetDialog){
        val filteredList = createFilterList(dialog, filterViewModel)

        //region HISTORY ADAPTER
        populateGamesAdapter(filteredList)
        filterViewModel.filteredGameList = filteredList
        //endregion

        dialog.dismiss()
    }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("HF", "onCreate called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("HF", "onDestroy called")
    }

}