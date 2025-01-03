package com.mauleCo.cornerpocket.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mauleCo.cornerpocket.Activities.PdfActivity
import com.mauleCo.cornerpocket.R
import com.mauleCo.cornerpocket.Utils.HelperFunctions
import com.mauleCo.cornerpocket.Utils.ImageUtils
import com.mauleCo.cornerpocket.databinding.FragmentHomeBinding
import com.mauleCo.cornerpocket.viewModels.UserViewModel
import com.google.android.gms.ads.AdRequest
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class HomeFragment : Fragment() {

    //region GLOBAL VARIABLES
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //LOAD AD
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        //region BACK PRESS
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
        //endregion

        //creates a user if one does not exist
        val user = userViewModel.getUser()
        if (user != null){
            binding.userNameTV.text = getString(R.string.var_game_time, user.name)
            HelperFunctions.setTextColorSection(requireContext(), binding.userNameTV, binding.userNameTV.text.toString(), user.name, R.color.cyan)

            val pfp = ImageUtils.getImageFromLocalStorage(requireContext(), user._id.toString())
            binding.userIcon.setImageURI(pfp)
        }
        val date: Date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        binding.dateTV.text = HelperFunctions.formatDate(date)

        binding.userIcon.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_user_details)
        }

        binding.playCL.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_opponentSelectFragment)
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
            val intent = Intent(requireContext(), PdfActivity::class.java)
            startActivity(intent)
        }

        binding.donationsCL.setOnClickListener {
            findNavController().navigate(R.id.action_playFragment_to_moreFragment2)
        }

    }

}