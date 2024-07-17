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
import com.example.cornerpocket.Utils.ImageUtils
import com.example.cornerpocket.R
import com.example.cornerpocket.Utils.DialogUtils
import com.example.cornerpocket.databinding.FragmentGameReviewBinding
import com.example.cornerpocket.models.EIGHT_BALl
import com.example.cornerpocket.models.ENGLISH
import com.example.cornerpocket.models.NINE_BALl
import com.example.cornerpocket.viewModels.PlayViewModel

class GameReviewFragment : Fragment() {
    private var _binding : FragmentGameReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayViewModel by navGraphViewModels(R.id.gameGraph)


    private var dropdownItems = arrayOf("Standard victory","Opponent foul")
    private lateinit var actv : AutoCompleteTextView
    private lateinit var adapterItems : ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGameReviewBinding.inflate(inflater, container, false)

        if (viewModel.getSubType() == EIGHT_BALl){
            styleBallsPlayed()
        } else {
            binding.ballsPlayedSection.root.visibility = View.GONE
        }

        actv = binding.winnerSection.actv
        adapterItems = ArrayAdapter<String>(requireContext(),
            R.layout.item_text_dropdown, dropdownItems)
        actv.setAdapter(adapterItems)
        actv.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, _: View, i: Int, _: Long ->
            val item : String = adapterView.getItemAtPosition(i).toString()
            viewModel.setMethodOfVictory(item)
        }

        binding.finishGame.setOnClickListener {
            if (viewModel.getSubType() == EIGHT_BALl){
                if (viewModel.getMethodOfVictory().isBlank() && viewModel.getUserBallsPlayed().isBlank() && viewModel.getUserWon() == null){
                    Toast.makeText(requireActivity(), "PLEASE FILL IN ALL FIELDS",Toast.LENGTH_SHORT).show()
                } else if (viewModel.getUserWon() == null) {
                    Toast.makeText(requireActivity(), "PLEASE ENTER THE WINNER OF THE GAME",Toast.LENGTH_SHORT).show()
                } else if (viewModel.getMethodOfVictory().isBlank()){
                    Toast.makeText(requireActivity(), "PLEASE SELECT THE METHOD OF VICTORY",Toast.LENGTH_SHORT).show()
                } else if (viewModel.getUserBallsPlayed().isBlank()){
                    Toast.makeText(requireActivity(), "PLEASE SELECT WHICH BALLS YOU PLAYED AS",Toast.LENGTH_SHORT).show()
                } else {
                    createGame()
                    findNavController().navigate(R.id.action_gameReviewFragment_to_gameDetailsFragment)
                }
            } else if (viewModel.getSubType() == NINE_BALl){
                if (viewModel.getMethodOfVictory().isBlank() && viewModel.getUserWon() == null){
                    Toast.makeText(requireActivity(), "PLEASE FILL IN ALL FIELDS",Toast.LENGTH_SHORT).show()
                } else if (viewModel.getUserWon() == null) {
                    Toast.makeText(requireActivity(), "PLEASE ENTER THE WINNER OF THE GAME",Toast.LENGTH_SHORT).show()
                } else if (viewModel.getMethodOfVictory().isBlank()){
                    Toast.makeText(requireActivity(), "PLEASE SELECT THE METHOD OF VICTORY",Toast.LENGTH_SHORT).show()
                } else {
                    createGame()
                    findNavController().navigate(R.id.action_gameReviewFragment_to_gameDetailsFragment)
                }
            }

        }

        binding.quitButton.setOnClickListener {
            DialogUtils.returnToMenuDialog(
                requireContext(),
                findNavController(),
                R.id.action_gameReviewFragment_to_playFragment
            )
        }

        //WINNER SECTION
        binding.winnerSection.infoButton.setOnClickListener {
            DialogUtils.methodOfVictoryDialog(requireContext())
        }


        val user = viewModel.getUser()
        Log.i("BSF", "user = $user")
        if (user != null){
            binding.winnerSection.userText.text = user.name
            val pfp = ImageUtils.getImageFromLocalStorage(requireContext(), user._id.toString())
            binding.winnerSection.userImage.setImageURI(pfp)
        }

        val opponent = viewModel.getSelectedOpponent()
        Log.i("GRF", "selectedOpponent = $opponent}")
        if (opponent != null){
            binding.winnerSection.opponentText.text = opponent.name
            val pfp = ImageUtils.getImageFromLocalStorage(requireContext(), opponent._id.toString())
            binding.winnerSection.opponentImage.setImageURI(pfp)
        }

        binding.winnerSection.userImage.setOnClickListener {
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

        binding.winnerSection.opponentImage.setOnClickListener {
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


        binding.ballsPlayedSection.redBallImage.setOnClickListener {
            val ballsPlayed = viewModel.getUserBallsPlayed()
            Log.i("GRF", "ballsPlayed = $ballsPlayed")

            if (ballsPlayed == "YELLOW" || ballsPlayed == "STRIPES" || ballsPlayed.isBlank()){
                balls1Played()
            }
        }

        binding.ballsPlayedSection.yellowBallImage.setOnClickListener {
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
        binding.winnerSection.opponentSelectedImage.visibility = View.INVISIBLE
        binding.winnerSection.userSelectedImage.visibility = View.VISIBLE
    }
    private fun setOpponentWon(){
        viewModel.setUserWon(false)
        binding.winnerSection.opponentSelectedImage.visibility = View.VISIBLE
        binding.winnerSection.userSelectedImage.visibility = View.INVISIBLE
    }


    private fun styleBallsPlayed(){
        when (viewModel.getGameType()) {
            "ENGLISH" -> {
                binding.ballsPlayedSection.redBallImage.setImageResource(R.drawable.red_ball_img)
                binding.ballsPlayedSection.redBallText.text = getString(R.string.red)

                binding.ballsPlayedSection.yellowBallImage.setImageResource(R.drawable.yellow_ball_img)
                binding.ballsPlayedSection.yellowBallText.text = getString(R.string.yellow)
            }
            "AMERICAN" -> {
                binding.ballsPlayedSection.redBallImage.setImageResource(R.drawable.solid_ball_img)
                binding.ballsPlayedSection.redBallText.text = getString(R.string.spot)

                binding.ballsPlayedSection.yellowBallImage.setImageResource(R.drawable.stripe_ball_img)
                binding.ballsPlayedSection.yellowBallText.text = getString(R.string.stripe)
            }
            else -> {
                Log.i("GRF", "Unknown game type : ${viewModel.getGameType()}")
            }
        }
    }

    //RED / SOLIDS
    private fun balls1Played(){
        binding.ballsPlayedSection.yellowBallSelectedImage.visibility = View.INVISIBLE
        binding.ballsPlayedSection.redBallSelectedImage.visibility = View.VISIBLE

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
        binding.ballsPlayedSection.yellowBallSelectedImage.visibility = View.VISIBLE
        binding.ballsPlayedSection.redBallSelectedImage.visibility = View.INVISIBLE

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