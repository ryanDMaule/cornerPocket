package com.example.cornerpocket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.databinding.FragmentOpponentSelectBinding

class OpponentSelectFragment : Fragment() {
    private var _binding : FragmentOpponentSelectBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentOpponentSelectBinding.inflate(inflater, container, false)

        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_opponentSelectFragment_to_playFragment)
        }

        return binding.root
    }


}