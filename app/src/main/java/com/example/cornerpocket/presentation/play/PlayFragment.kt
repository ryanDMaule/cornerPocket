package com.example.cornerpocket.presentation.play

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.R
import com.example.cornerpocket.Utils.HelperFunctions
import com.example.cornerpocket.Utils.NavigationUtils
import com.example.cornerpocket.databinding.FragmentPlayBinding
import com.example.cornerpocket.viewModels.UserViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalQueries.localDate
import java.util.Date


class PlayFragment : Fragment() {
    private var _binding : FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)

        //creates a user if one does not exist
        val user = userViewModel.getUser()
        if (user != null){
            binding.userNameTV.text = "Game time, ${user.name}"
            HelperFunctions.setTextColorSection(requireContext(), binding.userNameTV, binding.userNameTV.text.toString(), "${user.name}", R.color.cyan)

        } else {
            // TODO: unskippable pop up dialog to "create user" if one is not found
        }

        val date: Date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        binding.dateTV.text = HelperFunctions.formatDate(date)

        binding.userIcon.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_user_details)
        }

        binding.playCL.setOnClickListener {
            NavigationUtils.navigateAndClearBackStack(
                findNavController(),
                R.id.action_playFragment_to_opponentSelectFragment,
                R.id.playFragment
            )
        }

        binding.historyCL.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_historyGraph)
        }

        binding.statisticsCL.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_statsFragment)
        }

        binding.editOpponentsCL.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_opponentDetailsFragment)
        }

        binding.learnCL.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
            startActivity(i)
        }

        binding.donationsCL.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_donationsFragment)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("PF", "onCreate called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PF", "onDestroy called")
    }

}