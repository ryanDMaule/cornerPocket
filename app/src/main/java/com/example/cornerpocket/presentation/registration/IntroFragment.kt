package com.example.cornerpocket.presentation.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.R
import com.example.cornerpocket.Utils.HelperFunctions
import com.example.cornerpocket.Utils.NavigationUtils
import com.example.cornerpocket.databinding.FragmentIntroBinding
import com.example.cornerpocket.viewModels.UserViewModel

class IntroFragment : Fragment() {

    //region GLOBAL VARIABLES
    private var _binding : FragmentIntroBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIntroBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region BACK PRESS
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
        //endregion

        HelperFunctions.setTextColorSection(requireContext(), binding.title, binding.title.text.toString(), "Corner Pocket", R.color.cyan)

        val user = userViewModel.getUser()
        if (user != null){
            NavigationUtils.navigateAndClearBackStack(
                findNavController(),
                R.id.action_introFragment_to_playFragment,
                R.id.introFragment
            )
        }

        binding.applyBtn.setOnClickListener {
            NavigationUtils.navigateAndClearBackStack(
                findNavController(),
                R.id.action_introFragment_to_registrationFragment,
                R.id.introFragment
            )
        }
    }

}