package com.example.cornerpocket

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.databinding.FragmentPlayBinding
import com.example.cornerpocket.viewModels.UserViewModel

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

//        val bottomNav = activity?.findViewById<ConstraintLayout>(R.id.bottom_navigation)
//        Log.e("bottomNav", "$bottomNav")


        //creates a user if one does not exist
        // TODO: unskippable pop up dialog to "create user" if one is not found 
        userViewModel.getUser()

        binding.startGameButton.setOnClickListener {
            NavigationUtils.navigateAndClearBackStack(
                findNavController(),
                R.id.action_playFragment_to_opponentSelectFragment,
                R.id.playFragment
            )
        }

        binding.userProfileButton.setOnClickListener {
            NavigationUtils.navigateAndClearBackStack(
                findNavController(),
                R.id.action_playFragment_to_user_details,
                R.id.playFragment
            )
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