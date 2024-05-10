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

        binding.settingsButton.setOnClickListener {
            // TODO: ON CLICK GO TO SETTINGS PAGE
            Toast.makeText(requireActivity(), "SETTINGS",Toast.LENGTH_SHORT).show()
        }

        binding.userProfileButton.setOnClickListener {
            // TODO: ON CLICK GO TO USER PROFILE PAGE
            Toast.makeText(requireActivity(), "USER PROFILE",Toast.LENGTH_SHORT).show()
        }



        return binding.root
    }

}