package com.example.cornerpocket.presentation.play

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.cornerpocket.R
import com.example.cornerpocket.Utils.DialogUtils
import com.example.cornerpocket.Utils.ImageUtils
import com.example.cornerpocket.Utils.NavigationUtils
import com.example.cornerpocket.databinding.FragmentGameReviewBinding
import com.example.cornerpocket.models.AMERICAN
import com.example.cornerpocket.models.EIGHT_BALl
import com.example.cornerpocket.models.ENGLISH
import com.example.cornerpocket.models.NINE_BALl
import com.example.cornerpocket.models.RED
import com.example.cornerpocket.models.SPOT
import com.example.cornerpocket.models.STRIPE
import com.example.cornerpocket.models.YELLOW
import com.example.cornerpocket.viewModels.PlayViewModel

class GameReviewFragment : Fragment() {

    //region GLOBAL VARIABLES
    private var _binding : FragmentGameReviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayViewModel by navGraphViewModels(R.id.gameGraph)

    private lateinit var actv : AutoCompleteTextView
    private lateinit var adapterItems : ArrayAdapter<String>
    //endregion
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGameReviewBinding.inflate(inflater, container, false)

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
                    R.id.action_gameReviewFragment_to_playFragment
                )
            }
        })
        //endregion

        //If the user selected 8 ball then style based on if it was American or English else hide the section as its unnecessary for 9 ball
        if (viewModel.getSubType() == EIGHT_BALl){
            styleBallsPlayed()
        } else {
            binding.ballsPlayedSection.root.visibility = View.GONE
        }

        //region METHOD OF VICTORY DROPDOWN
        actv = binding.winnerSection.actv
        val victoryOptions = arrayOf(
            getString(R.string.standard_victory),
            getString(R.string.opponent_foul)
        )
        adapterItems = ArrayAdapter<String>(requireContext(), R.layout.item_text_dropdown, victoryOptions)
        actv.setAdapter(adapterItems)

        actv.onItemClickListener = AdapterView.OnItemClickListener { adapterView: AdapterView<*>, _: View, i: Int, _: Long ->
            val item : String = adapterView.getItemAtPosition(i).toString()
            viewModel.setMethodOfVictory(item)
        }
        //endregion

        binding.finishGame.setOnClickListener {
            if (viewModel.getSubType() == EIGHT_BALl){
                if (viewModel.getMethodOfVictory().isBlank() && viewModel.getUserBallsPlayed().isBlank() && viewModel.getUserWon() == null){
                    Toast.makeText(requireActivity(), getString(R.string.please_fill_in_all_fields),Toast.LENGTH_SHORT).show()
                } else if (viewModel.getUserWon() == null) {
                    Toast.makeText(requireActivity(), getString(R.string.please_select_the_winner_of_the_game),Toast.LENGTH_SHORT).show()
                } else if (viewModel.getMethodOfVictory().isBlank()){
                    Toast.makeText(requireActivity(), getString(R.string.please_select_the_method_of_victory),Toast.LENGTH_SHORT).show()
                } else if (viewModel.getUserBallsPlayed().isBlank()){
                    Toast.makeText(requireActivity(), getString(R.string.please_select_which_balls_you_played_as),Toast.LENGTH_SHORT).show()
                } else {
                    createGame()
                    NavigationUtils.navigateAndClearBackStack(
                        findNavController(),
                        R.id.action_gameReviewFragment_to_gameDetailsFragment,
                        R.id.gameReviewFragment
                    )
                }
            } else if (viewModel.getSubType() == NINE_BALl){
                if (viewModel.getMethodOfVictory().isBlank() && viewModel.getUserWon() == null){
                    Toast.makeText(requireActivity(), getString(R.string.please_fill_in_all_fields),Toast.LENGTH_SHORT).show()
                } else if (viewModel.getUserWon() == null) {
                    Toast.makeText(requireActivity(), getString(R.string.please_select_the_winner_of_the_game),Toast.LENGTH_SHORT).show()
                } else if (viewModel.getMethodOfVictory().isBlank()){
                    Toast.makeText(requireActivity(), getString(R.string.please_select_the_method_of_victory),Toast.LENGTH_SHORT).show()
                } else {
                    createGame()
                    NavigationUtils.navigateAndClearBackStack(
                        findNavController(),
                        R.id.action_gameReviewFragment_to_gameDetailsFragment,
                        R.id.gameReviewFragment
                    )
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
        Log.i("GRF", "user = $user")
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
                formatVictorySelection(userWon = true)
            } else {
                if (!userWon){
                    formatVictorySelection(userWon = true)
                }
            }
        }

        binding.winnerSection.opponentImage.setOnClickListener {
            val userWon = viewModel.getUserWon()
            Log.i("GRF", "opponentIcon = $userWon")

            if (userWon == null){
                formatVictorySelection(userWon = false)
            } else {
                if (userWon){
                    formatVictorySelection(userWon = false)
                }
            }
        }

        binding.ballsPlayedSection.redBallImage.setOnClickListener {
            val ballsPlayed = viewModel.getUserBallsPlayed()
            Log.i("GRF", "ballsPlayed = $ballsPlayed")

            if (ballsPlayed == YELLOW || ballsPlayed == STRIPE || ballsPlayed.isBlank()){
                ballsPlayed(redSolidBallsPlayed = true)
            }
        }

        binding.ballsPlayedSection.yellowBallImage.setOnClickListener {
            val ballsPlayed = viewModel.getUserBallsPlayed()
            Log.i("GRF", "ballsPlayed = $ballsPlayed")

            if (ballsPlayed == RED || ballsPlayed == SPOT || ballsPlayed.isBlank()){
                ballsPlayed(redSolidBallsPlayed = false)
            }
        }

    }

    /**
     * Formats the victory ticks based on the selection
     *
     * @param userWon A boolean if true user won, false opponent won
     */
    private fun formatVictorySelection(userWon : Boolean){
        if (userWon){
            viewModel.setUserWon(true)
            binding.winnerSection.opponentSelectedImage.visibility = View.INVISIBLE
            binding.winnerSection.userSelectedImage.visibility = View.VISIBLE
        } else {
            viewModel.setUserWon(false)
            binding.winnerSection.opponentSelectedImage.visibility = View.VISIBLE
            binding.winnerSection.userSelectedImage.visibility = View.INVISIBLE
        }
    }


    /**
     * Changes the ball images and names dependant on if the game played was American or English
     */
    private fun styleBallsPlayed(){
        when (viewModel.getGameType()) {
            ENGLISH -> {
                binding.ballsPlayedSection.redBallImage.setImageResource(R.drawable.red_ball_img)
                binding.ballsPlayedSection.redBallText.text = getString(R.string.red)

                binding.ballsPlayedSection.yellowBallImage.setImageResource(R.drawable.yellow_ball_img)
                binding.ballsPlayedSection.yellowBallText.text = getString(R.string.yellow)
            }
            AMERICAN -> {
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

    /**
     * When the user selects the balls they played as this will
     * add the tick icon to whichever was selected
     * store the value in the vm
     *
     * @param redSolidBallsPlayed If true the user played as red or solid, false yellow or stripe
     */
    private fun ballsPlayed(redSolidBallsPlayed : Boolean) {
        //Styles the tick icon
        if (redSolidBallsPlayed){
            binding.ballsPlayedSection.yellowBallSelectedImage.visibility = View.INVISIBLE
            binding.ballsPlayedSection.redBallSelectedImage.visibility = View.VISIBLE
        } else {
            binding.ballsPlayedSection.yellowBallSelectedImage.visibility = View.VISIBLE
            binding.ballsPlayedSection.redBallSelectedImage.visibility = View.INVISIBLE
        }

        //Assigns the selected ball in the vm
        when (viewModel.getGameType()) {
            ENGLISH -> {
                if (redSolidBallsPlayed){
                    viewModel.setUserBallsPlayed(RED)
                } else {
                    viewModel.setUserBallsPlayed(YELLOW)
                }
            }
            AMERICAN -> {
                if (redSolidBallsPlayed){
                    viewModel.setUserBallsPlayed(SPOT)
                } else {
                    viewModel.setUserBallsPlayed(STRIPE)
                }
            }
            else -> {
                Log.i("GRF", "Unknown game type : ${viewModel.getGameType()}")
            }
        }
    }

    /**
     * Calls updateOpponent in the vm
     */
    private fun createGame(){
//        Toast.makeText(context, getString(R.string.game_created), Toast.LENGTH_SHORT).show()
//        viewModel.printPendingGameValues()
        viewModel.updateOpponent()
    }

}