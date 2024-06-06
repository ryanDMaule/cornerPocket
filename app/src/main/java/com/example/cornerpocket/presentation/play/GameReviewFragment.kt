package com.example.cornerpocket.presentation.play

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.FragmentGameReviewBinding
import com.example.cornerpocket.viewModels.MainViewModel

class GameReviewFragment : Fragment() {
    private var _binding : FragmentGameReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by navGraphViewModels(R.id.gameGraph)


    private var dropdownItems = arrayOf("Standard victory","Opponent foul")
    private lateinit var actv : AutoCompleteTextView
    private lateinit var adapterItems : ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGameReviewBinding.inflate(inflater, container, false)

        actv = binding.actv
        adapterItems = ArrayAdapter<String>(requireContext(),
            R.layout.item_text_dropdown, dropdownItems)
        actv.setAdapter(adapterItems)
        actv.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, _: View, i: Int, _: Long ->
            val item : String = adapterView.getItemAtPosition(i).toString()
            viewModel.setMethodOfVictory(item)
        }

        binding.finishGame.setOnClickListener {

            if (viewModel.getMethodOfVictory().isNotBlank()){
                if (binding.redBallsLeftText.text.isNotBlank() && binding.yellowBallsLeftText.text.isNotBlank()){
                    viewModel.setUserBallsRemaining(binding.redBallsLeftText.text.toString().toInt())
                    viewModel.setOpponentBallsRemaining(binding.yellowBallsLeftText.text.toString().toInt())
                } else {
                    Toast.makeText(requireActivity(), "PLEASE ENTER THE BALLS REMAINING FOR BOTH BALL TYPES",Toast.LENGTH_SHORT).show()
                }
                createGame()
                findNavController().navigate(R.id.action_gameReviewFragment_to_gameDetailsFragment)
            } else {
                Toast.makeText(requireActivity(), "PLEASE SELECT THE GAME WINNER AND METHOD OF VICTORY",Toast.LENGTH_SHORT).show()
            }
        }

        binding.quitButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameReviewFragment_to_playFragment)
        }

        //WINNER SECTION
        binding.userText.text = "Ryan"
        val opponent = viewModel.getSelectedOpponent()

        if (opponent != null){
            Log.i("GRF", "selectedOpponent = $opponent}")

            binding.opponentText.text = opponent.name
        } else {
            Log.i("GRF", "viewModel.selectedOpponent == null")
        }

        binding.userImage.setOnClickListener {
            val userWon = viewModel.getUserWon()
            Log.i("GRF", "userIcon = $userWon")

            if (userWon == null){
                setUserWon()
            } else {
                if (!userWon){
                    setUserWon()
                }
            }
        }

        binding.opponentImage.setOnClickListener {
            val userWon = viewModel.getUserWon()
            Log.i("GRF", "opponentIcon = $userWon")

            if (userWon == null){
                setOpponentWon()
            } else {
                if (userWon){
                    setOpponentWon()
                }
            }
        }


        binding.redBallImage.setOnClickListener {
            val ballsPlayed = viewModel.getUserBallsPlayed()
            Log.i("GRF", "ballsPlayed = $ballsPlayed")

            if (ballsPlayed == "YELLOW" || ballsPlayed == "STRIPES" || ballsPlayed.isBlank()){
                balls1Played()
            }
        }

        binding.yellowBallImage.setOnClickListener {
            val ballsPlayed = viewModel.getUserBallsPlayed()
            Log.i("GRF", "ballsPlayed = $ballsPlayed")

            if (ballsPlayed == "RED" || ballsPlayed == "SOLIDS" || ballsPlayed.isBlank()){
                balls2Played()
            }
        }

        return binding.root
    }

    private fun setUserWon(){
        viewModel.setUserWon(true)
        binding.opponentSelectedImage.visibility = View.INVISIBLE
        binding.userSelectedImage.visibility = View.VISIBLE
    }
    private fun setOpponentWon(){
        viewModel.setUserWon(false)
        binding.opponentSelectedImage.visibility = View.VISIBLE
        binding.userSelectedImage.visibility = View.INVISIBLE
    }


    //RED / SOLIDS
    private fun balls1Played(){
        binding.yellowBallSelectedImage.visibility = View.INVISIBLE
        binding.redBallSelectedImage.visibility = View.VISIBLE

        when (viewModel.getGameType()) {
            "ENGLISH" -> {
                viewModel.setUserBallsPlayed("RED")
            }
            "AMERICAN" -> {
                viewModel.setUserBallsPlayed("SOLIDS")
            }
            else -> {
                Log.i("GRF", "Unknown game type : ${viewModel.getGameType()}")
            }
        }

    }
    //YELLOW / STRIPES
    private fun balls2Played(){
        binding.yellowBallSelectedImage.visibility = View.VISIBLE
        binding.redBallSelectedImage.visibility = View.INVISIBLE

        when (viewModel.getGameType()) {
            "ENGLISH" -> {
                viewModel.setUserBallsPlayed("YELLOW")
            }
            "AMERICAN" -> {
                viewModel.setUserBallsPlayed("STRIPES")
            }
            else -> {
                Log.i("GRF", "Unknown game type : ${viewModel.getGameType()}")
            }
        }
    }

    private fun createGame(){
//        viewModel.printPendingGameValues()
        viewModel.updateOpponent()
    }

}