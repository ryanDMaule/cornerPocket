package com.mauleCo.cornerpocket.presentation.play

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.mauleCo.cornerpocket.Activities.PdfActivity
import com.mauleCo.cornerpocket.Utils.HelperFunctions
import com.mauleCo.cornerpocket.R
import com.mauleCo.cornerpocket.Utils.DialogUtils
import com.mauleCo.cornerpocket.Utils.NavigationUtils
import com.mauleCo.cornerpocket.databinding.FragmentGameUnderwayBinding
import com.mauleCo.cornerpocket.viewModels.PlayViewModel

class GameUnderwayFragment : Fragment() {

    //region GLOBAL VARIABLES

    private var _binding : FragmentGameUnderwayBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayViewModel by navGraphViewModels(R.id.gameGraph)
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameUnderwayBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.infoButton.setOnClickListener {
            val intent = Intent(requireContext(), PdfActivity::class.java)
            startActivity(intent)
        }

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

    }

}