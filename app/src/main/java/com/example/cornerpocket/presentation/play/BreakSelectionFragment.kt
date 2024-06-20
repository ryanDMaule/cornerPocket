package com.example.cornerpocket.presentation.play

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.FragmentBreakSelectionBinding
import com.example.cornerpocket.viewModels.PlayViewModel

class BreakSelectionFragment : Fragment() {

    private var _binding : FragmentBreakSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayViewModel by navGraphViewModels(R.id.gameGraph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBreakSelectionBinding.inflate(inflater, container, false)

        val user = viewModel.getUser()
        Log.i("BSF", "user = $user")
        if (user != null){
            binding.vsSection.userNameTv.text = viewModel.getUser()?.name
            binding.tvUserName.text = viewModel.getUser()?.name
        }

        val opponent = viewModel.getSelectedOpponent()
        Log.i("BSF", "selectedOpponent = $opponent")
        if (opponent != null){
            binding.tvOpponentName.text = opponent.name
            binding.vsSection.opponentNameTv.text = opponent.name
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_breakSelectionFragment_to_gameTypeFragment)
        }

        binding.btnNextButton.setOnClickListener {
            if (viewModel.getUserBroke() != null){
                findNavController().navigate(R.id.action_breakSelectionFragment_to_gameUnderwayFragment)
            }
        }

        binding.userIcon.setOnClickListener {
            Log.i("BSF", "userIcon = ${viewModel.getUserBroke()}")

            if (viewModel.getUserBroke() != true){
                setUserBreaks()
            }
        }

        binding.opponentIcon.setOnClickListener {
            Log.i("BSF", "opponentIcon = ${viewModel.getUserBroke()}")

            if (viewModel.getUserBroke() != false){
                setOpponentBreaks()
            }
        }

        binding.randomIcon.setOnClickListener {
            selectRandomPlayer()
        }

        return binding.root
    }

    private fun setUserBreaks(){
        viewModel.setUserBroke(true)
        binding.opponentBreakIcon.visibility = View.INVISIBLE
        binding.userBreakIcon.visibility = View.VISIBLE
    }

    private fun setOpponentBreaks(){
        viewModel.setUserBroke(false)
        binding.opponentBreakIcon.visibility = View.VISIBLE
        binding.userBreakIcon.visibility = View.INVISIBLE
    }

    private fun selectRandomPlayer(){
        when ((0..1).random()) {
            0 -> {
                if (viewModel.getUserBroke() != true){
                    setUserBreaks()
                }
            }
            1 -> {
                if (viewModel.getUserBroke() != false){
                    setOpponentBreaks()
                }
            }
        }
    }

}