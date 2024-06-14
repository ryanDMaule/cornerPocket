package com.example.cornerpocket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.databinding.FragmentPlayBinding

class PlayFragment : Fragment() {
    private var _binding : FragmentPlayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)

        binding.startGameButton.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_opponentSelectFragment)
        }

        binding.userProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_user_details)
        }



        return binding.root
    }

}