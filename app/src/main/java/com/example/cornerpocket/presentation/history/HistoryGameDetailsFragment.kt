package com.example.cornerpocket.presentation.history

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.R
import com.example.cornerpocket.Utils.HelperFunctions
import com.example.cornerpocket.Utils.ImageUtils
import com.example.cornerpocket.Utils.NavigationUtils
import com.example.cornerpocket.databinding.FragmentGameDetailsBinding
import com.example.cornerpocket.databinding.FragmentHistoryBinding
import com.example.cornerpocket.models.AMERICAN
import com.example.cornerpocket.models.EIGHT_BALl
import com.example.cornerpocket.models.ENGLISH
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.RED
import com.example.cornerpocket.models.SPOT
import com.example.cornerpocket.models.STRIPE
import com.example.cornerpocket.models.YELLOW
import com.example.cornerpocket.viewModels.PlayViewModel
import com.google.android.material.button.MaterialButton
import org.mongodb.kbson.ObjectId

class HistoryGameDetailsFragment : Fragment() {

    //region GLOBAL VARIABLES
    private var _binding : FragmentGameDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayViewModel by viewModels()

    private var passedGame: Game? = null
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameDetailsBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region BACK PRESS
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                NavigationUtils.navigateAndClearBackStack(
                    findNavController(),
                    R.id.action_historyGameDetailsFragment_to_historyFragment,
                    R.id.historyGameDetailsFragment
                )
            }
        })
        //endregion

        formatPage()

        //Get game from bundle
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

            //GAME TYPE
            binding.gameTypeTitle.text = getString(R.string.var_gameType, passedGame!!.gameType, passedGame!!.subType)
            HelperFunctions.setTextColorSection(requireContext(), binding.gameTypeTitle, binding.gameTypeTitle.text.toString(), "${passedGame!!.gameType} ${passedGame!!.subType}", R.color.white)

            //GAME TIME
            val gameTime = HelperFunctions.formatSecondsToMMSS(passedGame!!.gameDuration)
            binding.gameDurationTitle.text = getString(R.string.var_gameDuration, gameTime)
            HelperFunctions.setTextColorSection(requireContext(), binding.gameDurationTitle, binding.gameDurationTitle.text.toString(), gameTime, R.color.white)

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

                val pfp = ImageUtils.getImageFromLocalStorage(requireContext(), user._id.toString())
                binding.userImage.setImageURI(pfp)
            }

            if (opponent != null) {
                binding.opponentText.text = opponent.name

                val pfp = ImageUtils.getImageFromLocalStorage(requireContext(), opponent._id.toString())
                binding.opponentImage.setImageURI(pfp)
            }
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

            if (passedGame!!.subType == EIGHT_BALl){
                //BALLS PLAYED
                when (passedGame!!.gameType) {
                    ENGLISH -> {
                        when (passedGame!!.userBallsPlayed) {
                            RED -> {
                                binding.userBallsPlayed.setImageResource(R.drawable.red_ball_img)
                                binding.userBallsPlayedText.text = getString(R.string.red)

                                binding.opponentBallsPlayed.setImageResource(R.drawable.yellow_ball_img)
                                binding.opponentBallsPlayedText.text = getString(R.string.yellow)
                            }

                            YELLOW -> {
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
                    AMERICAN -> {
                        when (passedGame!!.userBallsPlayed) {
                            SPOT -> {
                                binding.userBallsPlayed.setImageResource(R.drawable.solid_ball_img)
                                binding.userBallsPlayedText.text = getString(R.string.spot)

                                binding.opponentBallsPlayed.setImageResource(R.drawable.stripe_ball_img)
                                binding.opponentBallsPlayedText.text = getString(R.string.stripe)
                            }

                            STRIPE -> {
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

    }

    /**
     * Sets certain elements for the fragment visible as this view is shared
     */
    private fun formatPage() {
        binding.recordCl.visibility = View.GONE

        binding.backButtonConstraint.visibility = View.VISIBLE
        binding.backButtonConstraint.setOnClickListener {
            NavigationUtils.navigateAndClearBackStack(
                findNavController(),
                R.id.action_historyGameDetailsFragment_to_historyFragment,
                R.id.historyGameDetailsFragment
            )
        }

        binding.deleteButtonConstraint.visibility = View.VISIBLE
        binding.deleteButtonConstraint.setOnClickListener {
            removeGameWarningDialog()
        }

        binding.footer.visibility = View.GONE
    }

    /**
     * Create a dialog for the deletion of a game
     */
    private fun removeGameWarningDialog() {
        // Inflate the dialog layout
        val dialogView: View = LayoutInflater.from(requireContext()).inflate(R.layout.two_button_dialog, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Initialize dialog views
        val dialogTitle: TextView = dialogView.findViewById(R.id.dialog_title)
        val dialogDescription: TextView = dialogView.findViewById(R.id.dialog_description)
        val dialogButton1: MaterialButton = dialogView.findViewById(R.id.dialog_button_1)
        val dialogButton2: MaterialButton = dialogView.findViewById(R.id.dialog_button_2)

        dialogTitle.text = getString(R.string.delete_game)
        dialogDescription.text = getString(R.string.delete_game_content)
        dialogButton1.text = getString(R.string.cancel)
        dialogButton2.text = getString(R.string.delete)

        dialogButton1.setOnClickListener {
            dialog.dismiss()
        }

        dialogButton2.setOnClickListener {
            Toast.makeText(context, getString(R.string.game_deleted), Toast.LENGTH_SHORT).show()

            viewModel.removeGame(passedGame)
            NavigationUtils.navigateAndClearBackStack(
                findNavController(),
                R.id.action_historyGameDetailsFragment_to_historyFragment,
                R.id.historyGameDetailsFragment
            )

            dialog.dismiss()
        }

        //prevents showing solid whit in the corners where the edges are rounded
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Show the dialog
        dialog.show()
    }

}