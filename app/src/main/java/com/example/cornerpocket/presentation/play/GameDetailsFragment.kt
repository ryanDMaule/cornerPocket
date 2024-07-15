package com.example.cornerpocket.presentation.play

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.cornerpocket.Utils.HelperFunctions
import com.example.cornerpocket.Utils.ImageUtils
import com.example.cornerpocket.R
import com.example.cornerpocket.databinding.FragmentGameDetailsBinding
import com.example.cornerpocket.models.EIGHT_BALl
import com.example.cornerpocket.viewModels.PlayViewModel

class GameDetailsFragment : Fragment() {
    private var _binding : FragmentGameDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayViewModel by navGraphViewModels(R.id.gameGraph)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGameDetailsBinding.inflate(inflater, container, false)

        binding.menuButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameDetailsFragment_to_playFragment)
        }

        val game = viewModel.getOpponentMostRecentGame()
        val opponent = viewModel.getUpdatedOpponent()

        if (game == null || opponent == null){
//            Toast.makeText(requireActivity(), "NO GAME/OPPONENT FOUND!",Toast.LENGTH_SHORT).show()
        } else {

            //DATE
            val date = HelperFunctions.longConversion(game.date)
            binding.dateTitle.text = HelperFunctions.formatDate(date)

            //GAME TYPE
//            binding.gameTypeTitle.text = "${game.gameType}: ${game.subType}"
            binding.gameTypeTitle.text = getString(R.string.var_double, game.gameType, game.subType)

            //GAME TIME
            val gameTime = HelperFunctions.formatSecondsToMMSS(game.gameDuration)
//            binding.gameDurationTitle.text = "GAME DURATION : $gameTime"
            binding.gameDurationTitle.text = getString(R.string.var_gameDuration, gameTime)
            HelperFunctions.setTextColorSection(requireContext(), binding.gameDurationTitle, binding.gameDurationTitle.text.toString(), gameTime, R.color.white)

            //WINNERS CROWN / RECORD UP ARROW
            if (game.userWon){
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
                val pfp = ImageUtils.getImageFromLocalStorage(requireContext(), user._id.toString())
                binding.userImage.setImageURI(pfp)
            }

            binding.opponentText.text = opponent.name
            val pfp = ImageUtils.getImageFromLocalStorage(requireContext(), opponent._id.toString())
            binding.opponentImage.setImageURI(pfp)

            //METHOD OF VICTORY
            binding.winningMethod.text = game.methodOfVictory

            //PLAYER AND OPPONENT WIN RECORDS
            binding.userRecordText.text = opponent.losses.toString()
            binding.opponentRecordText.text = opponent.wins.toString()

            //WHO BROKE
            if (game.userBroke){
                binding.userBrokeImage.visibility = View.VISIBLE
            } else {
                binding.opponentBrokeImage.visibility = View.VISIBLE
            }

            if (game.subType == EIGHT_BALl){
                //BALLS PLAYED
                when (game.gameType) {
                    "ENGLISH" -> {
                        when (game.userBallsPlayed) {
                            "RED" -> {
                                binding.userBallsPlayed.setImageResource(R.drawable.red_ball_img)
                                binding.userBallsPlayedText.text = getString(R.string.red)

                                binding.opponentBallsPlayed.setImageResource(R.drawable.yellow_ball_img)
                                binding.opponentBallsPlayedText.text = getString(R.string.yellow)
                            }

                            "YELLOW" -> {
                                binding.userBallsPlayed.setImageResource(R.drawable.yellow_ball_img)
                                binding.userBallsPlayedText.text = getString(R.string.yellow)

                                binding.opponentBallsPlayed.setImageResource(R.drawable.red_ball_img)
                                binding.opponentBallsPlayedText.text = getString(R.string.red)
                            }

                            else -> {
                                //dunno
                            }
                        }
                    }
                    "AMERICAN" -> {
                        when (game.userBallsPlayed) {
                            "SOLIDS" -> {
                                binding.userBallsPlayed.setImageResource(R.drawable.solid_ball_img)
                                binding.userBallsPlayedText.text = getString(R.string.spot)

                                binding.opponentBallsPlayed.setImageResource(R.drawable.stripe_ball_img)
                                binding.opponentBallsPlayedText.text = getString(R.string.stripe)
                            }

                            "STRIPES" -> {
                                binding.userBallsPlayed.setImageResource(R.drawable.stripe_ball_img)
                                binding.userBallsPlayedText.text = getString(R.string.stripe)

                                binding.opponentBallsPlayed.setImageResource(R.drawable.solid_ball_img)
                                binding.opponentBallsPlayedText.text = getString(R.string.spot)
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
            } else {
                binding.userBallsPlayed.visibility = View.GONE
                binding.userBallsPlayedText.visibility = View.GONE

                binding.ballsPlayedTitle.visibility = View.GONE

                binding.opponentBallsPlayed.visibility = View.GONE
                binding.opponentBallsPlayedText.visibility = View.GONE
            }

        }


        return binding.root
    }
}