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
import com.example.cornerpocket.databinding.FragmentGameUnderwayBinding
import com.example.cornerpocket.viewModels.MainViewModel

class GameUnderwayFragment : Fragment() {

    private var _binding : FragmentGameUnderwayBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by navGraphViewModels(R.id.gameGraph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameUnderwayBinding.inflate(inflater, container, false)

        binding.timer.start()

        val user = viewModel.getUser()
        Log.i("BSF", "user = $user")
        if (user != null){
            binding.vsSection.userNameTv.text = viewModel.getUser()?.name
        }

        val opponent = viewModel.getSelectedOpponent()
        Log.i("GUF", "selectedOpponent = $opponent")
        if (opponent != null){
            binding.vsSection.opponentNameTv.text = opponent.name
        }

        binding.finishGame.setOnClickListener {
            viewModel.setGameLength(binding.timer.text.toString())
            binding.timer.stop()

            findNavController().navigate(R.id.action_gameUnderwayFragment_to_gameReviewFragment)
        }

        binding.quitButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameUnderwayFragment_to_playFragment)
        }


        return binding.root
    }

}