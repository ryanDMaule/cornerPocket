package com.example.cornerpocket.presentation.play

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.cornerpocket.Utils.HelperFunctions
import com.example.cornerpocket.R
import com.example.cornerpocket.Utils.DialogUtils
import com.example.cornerpocket.Utils.NavigationUtils
import com.example.cornerpocket.databinding.FragmentGameUnderwayBinding
import com.example.cornerpocket.viewModels.PlayViewModel

class GameUnderwayFragment : Fragment() {

    private var _binding : FragmentGameUnderwayBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayViewModel by navGraphViewModels(R.id.gameGraph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameUnderwayBinding.inflate(inflater, container, false)

        //region BACK PRESS
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                DialogUtils.returnToMenuDialog(
                    requireContext(),
                    findNavController(),
                    R.id.action_gameUnderwayFragment_to_playFragment
                )
            }
        })
        //endregion

        binding.timer.start()

        val user = viewModel.getUser()
        Log.i("BSF", "user = $user")
        if (user != null){
            binding.vsSection.userNameTv.text = viewModel.getUser()?.name
        }

        val opponent = viewModel.getSelectedOpponent()
        Log.i("GUF", "selectedOpponent = $opponent")
        if (opponent != null){
            binding.vsSection.opponentNameTv.text = opponent.name
        }

        binding.finishGame.setOnClickListener {
            val chronometerTimer = HelperFunctions.getChronometerElapsedTimeInSeconds(binding.timer.text)
            Log.e("GUF", "chronometerTimer = $chronometerTimer")

            viewModel.setGameLength(chronometerTimer)
            binding.timer.stop()

            NavigationUtils.navigateAndClearBackStack(
                findNavController(),
                R.id.action_gameUnderwayFragment_to_gameReviewFragment,
                R.id.gameUnderwayFragment
            )
        }

        binding.quitButton.setOnClickListener {
            DialogUtils.returnToMenuDialog(
                requireContext(),
                findNavController(),
                R.id.action_gameUnderwayFragment_to_playFragment
            )
        }


        return binding.root
    }

}