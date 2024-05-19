package com.example.cornerpocket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.databinding.FragmentBreakSelectionBinding
import com.example.cornerpocket.viewModels.MainViewModel

class BreakSelectionFragment : Fragment() {

    private var _binding : FragmentBreakSelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBreakSelectionBinding.inflate(inflater, container, false)

        if (viewModel.selectedOpponent != null){
            binding.tvOpponentName.text = viewModel.selectedOpponent!!.name
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameTypeFragment_to_opponentSelectFragment)
        }

        binding.startGameButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameTypeFragment_to_breakSelectionFragment)
        }

        return binding.root
    }

}