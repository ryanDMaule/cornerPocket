package com.example.cornerpocket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.cornerpocket.databinding.FragmentGameTypeBinding
import com.example.cornerpocket.databinding.FragmentPlayBinding
import com.example.cornerpocket.viewModels.MainViewModel

class GameTypeFragment : Fragment() {
    private var _binding : FragmentGameTypeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by navGraphViewModels(R.id.gameGraph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGameTypeBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameTypeFragment_to_opponentSelectFragment)
        }

        binding.btnNext.setOnClickListener {
            setVmGameType()
            findNavController().navigate(R.id.action_gameTypeFragment_to_breakSelectionFragment)
        }

        binding.englishGameTypeText.setOnClickListener {
            englishGamePressed()
        }

        binding.americanGameTypeText.setOnClickListener {
            americanGamePressed()
        }

        return binding.root
    }

    private var englishSelected = true

    private fun setVmGameType(){
        if (englishSelected){
            viewModel.newGameGameType = "ENGLISH"
        } else {
            viewModel.newGameGameType = "AMERICAN"
        }
    }

    private fun englishGamePressed(){
        if (englishSelected){
            //do nothing
        } else {
            englishSelected = true
            binding.englishBar.setBackgroundResource(R.drawable.gradient_blue_purple)
            binding.englishGameTypeText.setTextColor(resources.getColor(R.color.cyan))

            binding.americanBar.setBackgroundResource(R.color.white)
            binding.americanGameTypeText.setTextColor(resources.getColor(R.color.white))

            binding.ivGameSetUp.setImageResource(R.drawable.english_setup)
        }
    }

    private fun americanGamePressed(){
        if (!englishSelected){
            //do nothing
        } else {
            englishSelected = false
            binding.englishBar.setBackgroundResource(R.color.white)
            binding.englishGameTypeText.setTextColor(resources.getColor(R.color.white))

            binding.americanBar.setBackgroundResource(R.drawable.gradient_purple_blue2)
            binding.americanGameTypeText.setTextColor(resources.getColor(R.color.cyan))

            binding.ivGameSetUp.setImageResource(R.drawable.american_setup)
        }
    }

}