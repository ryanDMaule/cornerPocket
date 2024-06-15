package com.example.cornerpocket.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cornerpocket.Adapters.GameHistoryAdapter
import com.example.cornerpocket.databinding.FragmentHistoryBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.viewModels.MainViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

class HistoryFragment : Fragment() {
    private var _binding : FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: GameHistoryAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        binding.filtersButton.setOnClickListener {
            Toast.makeText(requireContext(), "FILTERS", Toast.LENGTH_SHORT).show()
        }

        recyclerView = binding.historyRecycler
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        viewModel.viewModelScope.launch {
            viewModel.getGames().collect{ gamesList ->
                historyAdapter = GameHistoryAdapter(sortGamesListByMostRecent(gamesList), viewModel)
                recyclerView.adapter = historyAdapter

                historyAdapter.onItemClicked = { game ->
                    Toast.makeText(requireContext(), "${game._id}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    fun sortGamesListByMostRecent(games : MutableList<Game>) : MutableList<Game> {
        val list = games.sortedBy { game ->
            abs(game.date - System.currentTimeMillis())
        }
        return list.toMutableList()
    }

}