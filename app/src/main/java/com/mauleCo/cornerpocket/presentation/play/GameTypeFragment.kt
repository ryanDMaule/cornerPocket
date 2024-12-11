package com.mauleCo.cornerpocket.presentation.play

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.mauleCo.cornerpocket.R
import com.mauleCo.cornerpocket.Utils.HelperFunctions
import com.mauleCo.cornerpocket.databinding.FragmentGameTypeBinding
import com.mauleCo.cornerpocket.models.AMERICAN
import com.mauleCo.cornerpocket.models.EIGHT_BALl
import com.mauleCo.cornerpocket.models.ENGLISH
import com.mauleCo.cornerpocket.models.NINE_BALl
import com.mauleCo.cornerpocket.viewModels.PlayViewModel
import com.google.android.material.button.MaterialButton

class GameTypeFragment : Fragment() {

    //region GLOBAL VARIABLES
    private var _binding : FragmentGameTypeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayViewModel by navGraphViewModels(R.id.gameGraph)
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameTypeBinding.inflate(inflater, container, false)

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

        binding.vsSection.userNameTv.text = viewModel.getUser()?.name
        binding.vsSection.opponentNameTv.text = viewModel.getSelectedOpponent()?.name

        when (viewModel.getGameType()) {
            ENGLISH -> {
                gameTypePressed(englishSelected = true)
            }
            AMERICAN -> {
                gameTypePressed(englishSelected = false)
            }
            else -> {
                //handle
            }
        }

        binding.btnNextButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameTypeFragment_to_breakSelectionFragment)
        }

        binding.englishButton.setOnClickListener {
            gameTypePressed(englishSelected = true)
        }

        binding.americanButton.setOnClickListener {
            gameTypePressed(englishSelected = false)
        }

        binding.eightBallBtn.setOnClickListener {
            disciplineSelected(eightBall = true)
        }

        binding.nineBallBtn.setOnClickListener {
            disciplineSelected(eightBall = false)
        }

    }

    /**
     * Sets the selected game type in the vm
     * updates the button styling
     * Shows the correct image for the break set up
     *
     * @param englishSelected If true ENGLISH game type else AMERICAN
     */
    private fun gameTypePressed(englishSelected : Boolean) {
        if (englishSelected){
            viewModel.setGameType(ENGLISH)
            viewModel.setSubType(EIGHT_BALl)

            styleButton(button = binding.englishButton, selected = true)
            styleButton(button = binding.americanButton, selected = false)

            binding.ivGameSetUp.setImageResource(R.drawable.english_setup)
            if (binding.ballVariantCL.visibility == View.VISIBLE){
                HelperFunctions.collapseView(binding.ballVariantCL)
            }
        } else {
            //American selected
            viewModel.setGameType(AMERICAN)

            if (eightBallSelected){
                disciplineSelected(eightBall = true)
            } else {
                disciplineSelected(eightBall = false)
            }

            styleButton(button = binding.englishButton, selected = false)
            styleButton(button = binding.americanButton, selected = true)

            if (binding.ballVariantCL.visibility != View.VISIBLE){
                HelperFunctions.expandView(binding.ballVariantCL)
            }
        }
    }

    private var eightBallSelected = true
    /**
     * Sets the selected discipline in the vm
     * updates the button styling
     * Shows the correct image for the break set up
     *
     * @param eightBall If true 8-BALL discipline else 9-BALL
     */
    private fun disciplineSelected(eightBall : Boolean) {
        if (eightBall) {
            eightBallSelected = true
            viewModel.setSubType(EIGHT_BALl)

            styleButton(button = binding.nineBallBtn, selected = false)
            styleButton(button = binding.eightBallBtn, selected = true)

            binding.ivGameSetUp.setImageResource(R.drawable.american_setup)
        } else {
            //Nine ball selected
            eightBallSelected = false
            viewModel.setSubType(NINE_BALl)

            styleButton(button = binding.eightBallBtn, selected = false)
            styleButton(button = binding.nineBallBtn, selected = true)

            binding.ivGameSetUp.setImageResource(R.drawable.nine_ball_setup)
        }
    }

    /**
     * Styles a passed button
     *
     * @param button A material button to apply the styling to
     * @param selected If true apply selected styling else deselected
     */
    private fun styleButton(button : MaterialButton, selected : Boolean){
        if (selected){
            button.strokeWidth = 0
            button.setTextColor(resources.getColor(R.color.dark_primary))
            val colorInt2: Int = requireContext().getColor(R.color.cyan)
            val csl2 = ColorStateList.valueOf(colorInt2)
            button.backgroundTintList = csl2
        } else {
            //Unselected styling
            button.strokeWidth = 4
            button.setTextColor(resources.getColor(R.color.white_80))
            val colorInt: Int = requireContext().getColor(R.color.white_80)
            val csl = ColorStateList.valueOf(colorInt)
            button.strokeColor = csl

            val colorInt2: Int = requireContext().getColor(R.color.transparant)
            val csl2 = ColorStateList.valueOf(colorInt2)
            button.backgroundTintList = csl2
        }
    }

}