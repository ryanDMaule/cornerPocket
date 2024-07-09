package com.example.cornerpocket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.databinding.FragmentGameReviewBinding
import com.example.cornerpocket.databinding.FragmentHistoryBinding
import com.example.cornerpocket.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_playFragment)
        }

        binding.editUserCL.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_user_details)
        }

        binding.editOpponentCL.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_opponentDetailsFragment)
        }

    }

}