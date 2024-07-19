package com.example.cornerpocket.presentation.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.R
import com.example.cornerpocket.Utils.HelperFunctions
import com.example.cornerpocket.databinding.FragmentDonationsBinding

class DonationsFragment : Fragment() {

    //region GLOBAL VARIABLES
    private var _binding : FragmentDonationsBinding? = null
    private val binding get() = _binding!!
    //endregion
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDonationsBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region BACK PRESS
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
        //endregion

        HelperFunctions.setTextColorSection(requireContext(), binding.subheading, binding.subheading.text.toString(), "Ryan", R.color.cyan)


        // TODO: needs to be tested with actual build
        binding.ratingBtn.setOnClickListener {
           HelperFunctions.promptUserForReview(requireActivity())
       }

        binding.donationBtn.setOnClickListener {
            // TODO: replace with buy me a coffee link
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
            startActivity(i)
        }

    }

}