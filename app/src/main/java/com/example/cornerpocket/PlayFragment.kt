package com.example.cornerpocket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.databinding.FragmentPlayBinding
import com.example.cornerpocket.viewModels.UserViewModel

class PlayFragment : Fragment() {
    private var _binding : FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)

        //creates a user if one does not exist
        // TODO: unskippable pop up dialog to "create user" if one is not found 
        userViewModel.getUser()

        binding.startGameButton.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_opponentSelectFragment)
        }

        binding.userProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_user_details)
        }



        return binding.root
    }

}