package com.example.cornerpocket.presentation.play

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.cornerpocket.Utils.HelperFunctions
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.FragmentGameTypeBinding
import com.example.cornerpocket.models.EIGHT_BALl
import com.example.cornerpocket.models.NINE_BALl
import com.example.cornerpocket.viewModels.PlayViewModel
import com.google.android.material.button.MaterialButton

class GameTypeFragment : Fragment() {
    private var _binding : FragmentGameTypeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayViewModel by navGraphViewModels(R.id.gameGraph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameTypeBinding.inflate(inflater, container, false)

        binding.vsSection.userNameTv.text = viewModel.getUser()?.name
        binding.vsSection.opponentNameTv.text = viewModel.getSelectedOpponent()?.name

        when (viewModel.getGameType()) {
            "ENGLISH" -> {
                englishGamePressed()
            }
            "AMERICAN" -> {
                americanGamePressed()
            }
            else -> {
                //handle
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameTypeFragment_to_opponentSelectFragment)
        }

        binding.btnNextButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameTypeFragment_to_breakSelectionFragment)
        }

        binding.englishButton.setOnClickListener {
            englishGamePressed()
        }

        binding.americanButton.setOnClickListener {
            americanGamePressed()
        }

        binding.eightBallBtn.setOnClickListener {
            eightBallPressed()
        }

        binding.nineBallBtn.setOnClickListener {
            nineBallPressed()
        }

        return binding.root
    }

    private fun englishGamePressed(){
        viewModel.setGameType("ENGLISH")
        viewModel.setSubType(EIGHT_BALl)

        styleSelectedButton(binding.englishButton)
        styleUnselectedButton(binding.americanButton)

        binding.ivGameSetUp.setImageResource(R.drawable.english_setup)
        if (binding.ballVariantCL.visibility == View.VISIBLE){
            HelperFunctions.collapseView(binding.ballVariantCL)
        }
    }

    private fun americanGamePressed(){
        viewModel.setGameType("AMERICAN")

        if (eightBallSelected){
            eightBallPressed()
        } else {
            nineBallPressed()
        }

        styleUnselectedButton(binding.englishButton)
        styleSelectedButton(binding.americanButton)

        if (binding.ballVariantCL.visibility != View.VISIBLE){
            HelperFunctions.expandView(binding.ballVariantCL)
        }
    }

    private var eightBallSelected = true

    private fun eightBallPressed(){
        eightBallSelected = true
        viewModel.setSubType(EIGHT_BALl)

        styleUnselectedButton(binding.nineBallBtn)
        styleSelectedButton(binding.eightBallBtn)

        binding.ivGameSetUp.setImageResource(R.drawable.american_setup)

    }

    private fun nineBallPressed(){
        eightBallSelected = false
        viewModel.setSubType(NINE_BALl)

        styleUnselectedButton(binding.eightBallBtn)
        styleSelectedButton(binding.nineBallBtn)

        binding.ivGameSetUp.setImageResource(R.drawable.nine_ball_setup)

    }

    private fun styleUnselectedButton(button: MaterialButton) {
        button.strokeWidth = 4
        button.setTextColor(resources.getColor(R.color.white_80))
        val colorInt: Int = requireContext().getColor(R.color.white_80)
        val csl = ColorStateList.valueOf(colorInt)
        button.strokeColor = csl

        val colorInt2: Int = requireContext().getColor(R.color.transparant)
        val csl2 = ColorStateList.valueOf(colorInt2)
        button.backgroundTintList = csl2
    }

    private fun styleSelectedButton(button: MaterialButton) {
        button.strokeWidth = 0
        button.setTextColor(resources.getColor(R.color.dark_primary))
        val colorInt2: Int = requireContext().getColor(R.color.cyan)
        val csl2 = ColorStateList.valueOf(colorInt2)
        button.backgroundTintList = csl2
    }

}