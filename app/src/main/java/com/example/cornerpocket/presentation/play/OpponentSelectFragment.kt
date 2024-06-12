package com.example.cornerpocket.presentation.play

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cornerpocket.Adapters.OpponentSelectorAdapter
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.FragmentOpponentSelectBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.Opponent
import com.example.cornerpocket.viewModels.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch


class OpponentSelectFragment : Fragment()  {
    private var _binding : FragmentOpponentSelectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by navGraphViewModels(R.id.gameGraph)

    private lateinit var recyclerView: RecyclerView
    private lateinit var opponentsAdapter: OpponentSelectorAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOpponentSelectBinding.inflate(inflater, container, false)

        binding.userText.text = "Ryan"

        binding.btnNextButton.setOnClickListener {
            if (viewModel.getSelectedOpponent() != null){
                findNavController().navigate(R.id.action_opponentSelectFragment_to_gameTypeFragment)
            } else {
                Toast.makeText(requireContext(), "PLEASE SELECT AN OPPONENT", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_opponentSelectFragment_to_playFragment)
        }

        recyclerView = binding.opponentListRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        viewModel.viewModelScope.launch {
            viewModel.getOpponents().collect{ opponentList ->
                opponentsAdapter = OpponentSelectorAdapter(opponentList)
                recyclerView.adapter = opponentsAdapter

                opponentsAdapter.onItemClicked = { opponent ->
                    itemSelected(opponent)
                    viewModel.setSelectedOpponent(opponent)
                    if (binding.selectedOpponentSection.visibility != View.VISIBLE){
                        binding.noOpponentSelectedBlock.visibility = View.GONE
                        binding.selectedOpponentSection.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.fabAdd.setOnClickListener {
            val dialog = SideSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.add_opponent_sheet, null)

            val btnClose = view.findViewById<ImageView>(R.id.quit_button)
            val btnCreate = view.findViewById<MaterialButton>(R.id.btnCreate)
            val textInputEditText = view.findViewById<TextInputEditText>(R.id.inputTextName)

            btnClose.setOnClickListener {
                dialog.dismiss()
            }

            btnCreate.setOnClickListener {
                createOpponent(textInputEditText, dialog)
            }

            dialog.setContentView(view)

            dialog.show()
        }

    return binding.root
    }

    private fun createOpponent(textInput : TextInputEditText, dialog : SideSheetDialog){
        if(textInput.text?.isBlank() == true){
            Toast.makeText(requireContext(), "PLEASE ENTER A NAME", Toast.LENGTH_SHORT).show()
        } else {
            //add to realm db
            viewModel.viewModelScope.launch {
                viewModel.insertOpponent(opponent = Opponent().apply {
                    name = textInput.text.toString()
                })

                Toast.makeText(requireContext(), "${textInput.text.toString()} added!", Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }
        }
    }

    private fun itemSelected(opponent: Opponent){
        binding.opponentText.text =opponent.name
        binding.winRecordText.text = "${opponent.wins} - ${opponent.losses}"
        formatRecentGames(opponent.gamesHistory.take(5).reversed())
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