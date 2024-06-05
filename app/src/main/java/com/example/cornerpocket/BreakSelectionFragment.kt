package com.example.cornerpocket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.cornerpocket.databinding.FragmentBreakSelectionBinding
import com.example.cornerpocket.viewModels.MainViewModel

class BreakSelectionFragment : Fragment() {

    private var _binding : FragmentBreakSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by navGraphViewModels(R.id.gameGraph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBreakSelectionBinding.inflate(inflater, container, false)

        // TODO: get users name from Realm object 
        binding.tvUserName.text = "Ryan"
        
        if (viewModel.selectedOpponent != null){
            Log.i("BSF", "selectedOpponent = ${viewModel.selectedOpponent}")

            binding.tvOpponentName.text = viewModel.selectedOpponent!!.name
        } else {
            Log.i("BSF", "viewModel.selectedOpponent == null")
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_breakSelectionFragment_to_gameTypeFragment)
        }

        binding.startGameButton.setOnClickListener {
            if (userBreaks != null){
                findNavController().navigate(R.id.action_breakSelectionFragment_to_gameUnderwayFragment)
            }
        }

        binding.userIcon.setOnClickListener {
            Log.i("BSF", "userIcon = $userBreaks")

            if (userBreaks != true){
                setUserBreaks()
            }
        }

        binding.opponentIcon.setOnClickListener {
            Log.i("BSF", "opponentIcon = $userBreaks")

            if (userBreaks != false){
                setOpponentBreaks()
            }
        }

        binding.randomIcon.setOnClickListener {
            selectRandomPlayer()
        }

        return binding.root
    }

    private var userBreaks : Boolean? = null

    private fun setUserBreaks(){
        userBreaks = true
        viewModel.newGameUserBroke = userBreaks as Boolean
        binding.opponentBreakIcon.visibility = View.INVISIBLE
        binding.userBreakIcon.visibility = View.VISIBLE
    }

    private fun setOpponentBreaks(){
        userBreaks = false
        viewModel.newGameUserBroke = userBreaks as Boolean
        binding.opponentBreakIcon.visibility = View.VISIBLE
        binding.userBreakIcon.visibility = View.INVISIBLE
    }

    private fun selectRandomPlayer(){
        when ((0..1).random()) {
            0 -> {
                if (userBreaks != true){
                    setUserBreaks()
                }
            }
            1 -> {
                if (userBreaks != false){
                    setOpponentBreaks()
                }
            }
        }
    }

}