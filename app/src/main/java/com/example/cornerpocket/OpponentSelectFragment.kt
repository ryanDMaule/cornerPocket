package com.example.cornerpocket

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cornerpocket.Adapters.OpponentSelectorAdapter
import com.example.cornerpocket.databinding.FragmentOpponentSelectBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.Opponent
import com.example.cornerpocket.viewModels.MainViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class OpponentSelectFragment : Fragment()  {
    private var _binding : FragmentOpponentSelectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var opponentsAdapter: OpponentSelectorAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOpponentSelectBinding.inflate(inflater, container, false)

        Log.i("OSF", "onCreateView")


        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_opponentSelectFragment_to_playFragment)
        }

        recyclerView = binding.opponentListRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        viewModel.viewModelScope.launch {
            viewModel.getOpponents().collect() {
                opponentsAdapter = OpponentSelectorAdapter(it)
                recyclerView.adapter = opponentsAdapter

                opponentsAdapter.onItemClicked = {
                    itemSelected(it)
                }
            }
        }

        return binding.root
    }

    private fun itemSelected(opponent: Opponent){
        binding.selectedOpponentName.text =opponent.name
        binding.selectedOpponentRecord.text = "${opponent.wins} - ${opponent.losses}"
        formatRecentGames(opponent.gamesHistory.take(5))
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
            0 -> binding.result1
            1 -> binding.result2
            2 -> binding.result3
            3 -> binding.result4
            4 -> binding.result5

            else -> throw Error()
        }
    }

}