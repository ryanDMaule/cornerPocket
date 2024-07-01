package com.example.cornerpocket.presentation.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.HelperFunctions
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.FragmentGameDetailsBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.viewModels.PlayViewModel
import org.mongodb.kbson.ObjectId

class HistoryGameDetailsFragment : Fragment() {
    private var _binding : FragmentGameDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayViewModel by viewModels()

    private var passedGame: Game? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameDetailsBinding.inflate(inflater, container, false)

        formatPage()

        arguments?.let {
            val objectIdString = it.getString("gameKey")
            if (objectIdString != null) {
                passedGame = viewModel.getGameViaId(ObjectId(objectIdString))
            }
        }

        if (passedGame != null) {
            val opponent = viewModel.getOpponentViaId(passedGame!!.opponentId)

            //DATE
            val date = HelperFunctions.longConversion(passedGame!!.date)
            binding.dateTitle.text = HelperFunctions.formatDate(date)

            //GAME TIME
            binding.gameDurationText.text = HelperFunctions.formatSecondsToMMSS(passedGame!!.gameDuration)

            //WINNERS CROWN / RECORD UP ARROW
            if (passedGame!!.userWon){
                binding.userCrown.visibility = View.VISIBLE
                binding.userUpArrow.visibility = View.VISIBLE
            } else {
                binding.opponentCrown.visibility = View.VISIBLE
                binding.opponentUpArrow.visibility = View.VISIBLE
            }

            //PLAYER NAMES
            val user = viewModel.getUser()
            Log.i("BSF", "user = $user")
            if (user != null){
                binding.userText.text = user.name
            }
            binding.opponentText.text = opponent?.name

            //METHOD OF VICTORY
            binding.winningMethod.text = passedGame!!.methodOfVictory

            //PLAYER AND OPPONENT WIN RECORDS
            binding.userRecordText.text = opponent?.losses.toString()
            binding.opponentRecordText.text = opponent?.wins.toString()

            //WHO BROKE
            if (passedGame!!.userBroke){
                binding.userBrokeImage.visibility = View.VISIBLE
            } else {
                binding.opponentBrokeImage.visibility = View.VISIBLE
            }

            //BALLS PLAYED
            when (passedGame!!.gameType) {
                "ENGLISH" -> {
                    when (passedGame!!.userBallsPlayed) {
                        "RED" -> {
                            binding.userBallsPlayed.setImageResource(R.drawable.red_ball_img)
                            binding.userBallsPlayedText.text = "RED"

                            binding.opponentBallsPlayed.setImageResource(R.drawable.yellow_ball_img)
                            binding.opponentBallsPlayedText.text = "YELLOW"
                        }

                        "YELLOW" -> {
                            binding.userBallsPlayed.setImageResource(R.drawable.yellow_ball_img)
                            binding.userBallsPlayedText.text = "YELLOW"

                            binding.opponentBallsPlayed.setImageResource(R.drawable.red_ball_img)
                            binding.opponentBallsPlayedText.text = "RED"
                        }

                        else -> {
                            //dunno
                        }
                    }
                }
                "AMERICAN" -> {
                    when (passedGame!!.userBallsPlayed) {
                        "SOLIDS" -> {
                            binding.userBallsPlayed.setImageResource(R.drawable.red_ball_img)
                            binding.userBallsPlayedText.text = "SOLIDS"

                            binding.opponentBallsPlayed.setImageResource(R.drawable.yellow_ball_img)
                            binding.opponentBallsPlayedText.text = "STRIPES"
                        }

                        "STRIPES" -> {
                            binding.userBallsPlayed.setImageResource(R.drawable.stripe_ball_img)
                            binding.userBallsPlayedText.text = "STRIPES"

                            binding.opponentBallsPlayed.setImageResource(R.drawable.solid_ball_img)
                            binding.opponentBallsPlayedText.text = "SOLIDS"
                        }

                        else -> {
                            //dunno
                        }
                    }
                }
                else -> {
                    //dunno
                }
            }
        }

        return binding.root
    }

    private fun formatPage() {
        binding.backButtonConstraint.visibility = View.VISIBLE
        binding.backButtonConstraint.setOnClickListener {
            findNavController().navigate(R.id.action_historyGameDetailsFragment_to_historyFragment)
        }

        binding.fadeOutIv.visibility = View.GONE
        binding.footer.visibility = View.GONE
    }

}