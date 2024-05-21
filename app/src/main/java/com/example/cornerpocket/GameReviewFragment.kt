package com.example.cornerpocket

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
import com.example.cornerpocket.databinding.FragmentGameReviewBinding
import com.example.cornerpocket.viewModels.MainViewModel

class GameReviewFragment : Fragment() {
    private var _binding : FragmentGameReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by navGraphViewModels(R.id.gameGraph)


    private var dropdownItems = arrayOf<String>("Standard victory","Opponent foul")
    private lateinit var actv : AutoCompleteTextView
    private lateinit var adapterItems : ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGameReviewBinding.inflate(inflater, container, false)

        actv = binding.actv
        adapterItems = ArrayAdapter<String>(requireContext(), R.layout.item_text_dropdown, dropdownItems)
        actv.setAdapter(adapterItems)
        actv.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, _: View, i: Int, _: Long ->
            val item : String = adapterView.getItemAtPosition(i).toString()
            viewModel.newGameMethodOfVictory = item
        }

        binding.finishGame.setOnClickListener {

            if (viewModel.newGameUserWon != null && viewModel.newGameMethodOfVictory.isNotBlank()){
                if (binding.redBallsLeftText.text.isNotBlank() && binding.yellowBallsLeftText.text.isNotBlank()){
                    when (viewModel.newGameUserBallsPlayed) {
                        "RED", "SOLIDS" -> {
                            viewModel.newGameUserBallsRemaining = binding.redBallsLeftText.text.toString().toInt()
                            viewModel.newGameOpponentBallsRemaining = binding.yellowBallsLeftText.text.toString().toInt()
                        }
                        "YELLOW", "STRIPES" -> {
                            viewModel.newGameUserBallsRemaining = binding.yellowBallsLeftText.text.toString().toInt()
                            viewModel.newGameOpponentBallsRemaining = binding.redBallsLeftText.text.toString().toInt()
                        }
                        else -> {
                            Log.i("GRF", "Unknown balls played = ${viewModel.newGameUserBallsPlayed}")
                        }
                    }
                } else {
                    Toast.makeText(requireActivity(), "PLEASE ENTER THE BALLS REMAINING FOR BOTH BALL TYPES",Toast.LENGTH_SHORT).show()
                }
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

        if (viewModel.selectedOpponent != null){
            Log.i("GRF", "selectedOpponent = ${viewModel.selectedOpponent}")

            binding.opponentText.text = viewModel.selectedOpponent!!.name
        } else {
            Log.i("GRF", "viewModel.selectedOpponent == null")
        }

        binding.userImage.setOnClickListener {
            Log.i("GRF", "userIcon = $viewModel.newGameUserWon")

            if (viewModel.newGameUserWon != true){
                setUserWon()
            }
        }

        binding.opponentImage.setOnClickListener {
            Log.i("GRF", "opponentIcon = $viewModel.newGameUserWon")

            if (viewModel.newGameUserWon != false){
                setOpponentWon()
            }
        }


        binding.redBallImage.setOnClickListener {
            Log.i("GRF", "ballsPlayed = $viewModel.newGameUserBallsPlayed")

            if (viewModel.newGameUserBallsPlayed != "RED" || viewModel.newGameUserBallsPlayed != "SOLIDS"){
                balls1Played()
            }
        }

        binding.yellowBallImage.setOnClickListener {
            Log.i("GRF", "ballsPlayed = $viewModel.newGameUserBallsPlayed")

            if (viewModel.newGameUserBallsPlayed != "YELLOW" || viewModel.newGameUserBallsPlayed != "STRIPES"){
                balls2Played()
            }
        }

        return binding.root
    }

    private fun setUserWon(){
        viewModel.newGameUserWon = true
        binding.opponentSelectedImage.visibility = View.INVISIBLE
        binding.userSelectedImage.visibility = View.VISIBLE
    }
    private fun setOpponentWon(){
        viewModel.newGameUserWon = false
        binding.opponentSelectedImage.visibility = View.VISIBLE
        binding.userSelectedImage.visibility = View.INVISIBLE
    }


    //RED / SOLIDS, YELLOW / STRIPES
    private fun balls1Played(){
        binding.yellowBallSelectedImage.visibility = View.INVISIBLE
        binding.redBallSelectedImage.visibility = View.VISIBLE

        when (viewModel.newGameGameType) {
            "ENGLISH" -> {
                viewModel.newGameUserBallsPlayed = "RED"
            }
            "AMERICAN" -> {
                viewModel.newGameUserBallsPlayed = "SOLIDS"
            }
            else -> {
                Log.i("GRF", "Unknown game type : ${viewModel.newGameGameType}")
            }
        }

    }
    private fun balls2Played(){
        binding.yellowBallSelectedImage.visibility = View.VISIBLE
        binding.redBallSelectedImage.visibility = View.INVISIBLE

        when (viewModel.newGameGameType) {
            "ENGLISH" -> {
                viewModel.newGameUserBallsPlayed = "YELLOW"
            }
            "AMERICAN" -> {
                viewModel.newGameUserBallsPlayed = "STRIPES"
            }
            else -> {
                Log.i("GRF", "Unknown game type : ${viewModel.newGameGameType}")
            }
        }
    }


}