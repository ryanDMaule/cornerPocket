package com.example.cornerpocket.presentation.play

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImageContract
import com.example.cornerpocket.Adapters.OpponentSelectorAdapter
import com.example.cornerpocket.Utils.ImageUtils
import com.example.cornerpocket.R
import com.example.cornerpocket.Utils.DialogUtils
import com.example.cornerpocket.databinding.FragmentOpponentSelectBinding
import com.example.cornerpocket.models.Game
import com.example.cornerpocket.models.Opponent
import com.example.cornerpocket.viewModels.PlayViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OpponentSelectFragment : Fragment()  {

    //region GLOBAL VARIABLES
    private var _binding : FragmentOpponentSelectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayViewModel by navGraphViewModels(R.id.gameGraph)

    private lateinit var recyclerView: RecyclerView
    private lateinit var opponentsAdapter: OpponentSelectorAdapter
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOpponentSelectBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region BACK PRESS
        binding.backButton.setOnClickListener {
            DialogUtils.returnToMenuDialog(
                requireContext(),
                findNavController(),
                R.id.action_opponentSelectFragment_to_playFragment
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                DialogUtils.returnToMenuDialog(
                    requireContext(),
                    findNavController(),
                    R.id.action_opponentSelectFragment_to_playFragment
                )
            }
        })
        //endregion

        binding.userText.text = viewModel.getUser()?.name

        val user = viewModel.getUser()
        if (user != null){
            binding.userText.text = user.name

            val pfp = ImageUtils.getImageFromLocalStorage(requireContext(), user._id.toString())
            binding.userImage.setImageURI(pfp)
        }

        binding.btnNextButton.setOnClickListener {
            if (viewModel.getSelectedOpponent() != null){
                findNavController().navigate(R.id.action_opponentSelectFragment_to_gameTypeFragment)
            } else {
                Toast.makeText(requireContext(), getString(R.string.select_opponent), Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView = binding.opponentListRecycler
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        viewModel.viewModelScope.launch {
            viewModel.getOpponents().collect{ opponentList ->
                opponentsAdapter = OpponentSelectorAdapter(opponentList, requireContext())
                recyclerView.adapter = opponentsAdapter

                opponentsAdapter.onItemClicked = { opponent ->
                    itemSelected(opponent)
                    viewModel.setSelectedOpponent(opponent)
                    if (binding.selectedOpponentSection.visibility != View.VISIBLE){
                        binding.noOpponentSelectedBlock.visibility = View.GONE
                        binding.selectedOpponentSection.visibility = View.VISIBLE
                    }
                }
            }
        }


        binding.fabAdd.setOnClickListener {
            val dialog = SideSheetDialog(requireContext())

            val view = layoutInflater.inflate(R.layout.add_opponent_sheet, null)
            val btnClose = view.findViewById<ImageView>(R.id.quit_button)
            val window = view.findViewById<ConstraintLayout>(R.id.full_layout)
            opponentImage = view.findViewById(R.id.addOponentImage)
            val btnAddImage = view.findViewById<ConstraintLayout>(R.id.clAddImage)
            val btnCreate = view.findViewById<MaterialButton>(R.id.footer_button)
            val textInputEditText = view.findViewById<TextInputEditText>(R.id.inputTextName)

            dialog.setOnDismissListener {
                if (unCroppedImage == null &&
                    croppedImage == null &&
                    textInputEditText.text.isNullOrBlank()){
                    //do nothing
                } else {
                    Toast.makeText(requireActivity(), getString(R.string.creation_cancelled),Toast.LENGTH_SHORT).show()
                }
            }

            window.setOnClickListener {
                //prevents closing the dialog by mis clicking
            }

            btnClose.setOnClickListener {
                if (unCroppedImage == null &&
                    croppedImage == null &&
                    textInputEditText.text.isNullOrBlank()){

                    dialog.dismiss()
                } else {
                    editOpponentWarningDialog(dialog)
                }
            }

            btnCreate.setOnClickListener {
                createOpponent(textInputEditText, dialog)
            }

            btnAddImage.setOnClickListener{
                showPhotoAlertDialog()
            }

            dialog.setContentView(view)

            dialog.show()
        }

    }

    private fun editOpponentWarningDialog(ssd : SideSheetDialog) {
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

        dialogTitle.text = getString(R.string.abandon_changes)
        dialogDescription.text = getString(R.string.unsaved_changes_content)
        dialogButton1.text = getString(R.string.cancel)
        dialogButton2.text = getString(R.string.close)

        dialogButton1.setOnClickListener {
            dialog.dismiss()
        }

        dialogButton2.setOnClickListener {
            unCroppedImage = null
            croppedImage = null

            dialog.dismiss()
            ssd.dismiss()
        }

        //prevents showing solid whit in the corners where the edges are rounded
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Show the dialog
        dialog.show()
    }

    private fun createOpponent(textInput : TextInputEditText, dialog : SideSheetDialog){
        if(textInput.text?.isBlank() == true){
            Toast.makeText(requireContext(), getString(R.string.please_enter_a_name), Toast.LENGTH_SHORT).show()
        } else {
            //add to realm db
            viewModel.viewModelScope.launch {
                viewModel.insertOpponent(opponent = Opponent().apply {
                    name = textInput.text.toString()

                    if (croppedImage != null) {
                        //get opponent and use _id to store cropped image
                        ImageUtils.saveCroppedImageToLocalStorage(requireContext(), croppedImage!!, this._id.toString())

                    } else if (unCroppedImage != null) {
                        //get opponent and use _id to store un cropped image
                        ImageUtils.saveImageToLocalStorage(requireContext(), unCroppedImage!!, this._id.toString())
                    }
                })

                Toast.makeText(context, getString(R.string.opponent_created), Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }
        }
    }

    private fun itemSelected(opponent: Opponent){
        binding.opponentText.text = opponent.name
        binding.winRecordText.text = getString(R.string.var_record, opponent.losses.toString(), opponent.wins.toString())

        val pfp = ImageUtils.getImageFromLocalStorage(requireContext(), opponent._id.toString())
        if (pfp != null){
            binding.opponentImage.setImageURI(pfp)
        } else {
            binding.opponentImage.setImageResource(R.drawable.user_icon_red_no_circle)
        }

        formatRecentGames(opponent.gamesHistory.reversed().take(5))
    }

    private fun formatRecentGames(gamesList : List<Game>){
        for (i in 0 until 5) {
            val recentGameImg = getImage(i)

            if(i >= gamesList.size){
                recentGameImg.setImageResource(R.drawable.na_img)
            } else {
                if (gamesList[i].userWon){
                    recentGameImg.setImageResource(R.drawable.win_img)
                } else {
                    recentGameImg.setImageResource(R.drawable.loss_img)
                }
            }
        }
    }

    private fun getImage(position: Int) : ImageView {
        return when(position) {
            0 -> binding.opponentPreviousFiveSection.result1
            1 -> binding.opponentPreviousFiveSection.result2
            2 -> binding.opponentPreviousFiveSection.result3
            3 -> binding.opponentPreviousFiveSection.result4
            4 -> binding.opponentPreviousFiveSection.result5

            else -> throw Error()
        }
    }

    //region IMAGE HANDLING
    private fun showPhotoAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.add_photo)
        builder.setMessage(R.string.use_photo_from)

        builder.setPositiveButton(R.string.camera) { dialog, _ ->
            requestPermissionLauncherCamera.launch(Manifest.permission.CAMERA)
            dialog.dismiss()
        }

        builder.setNegativeButton(R.string.camera_roll) { dialog, _ ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncherMedia.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                requestPermissionLauncherMedia.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private val requestPermissionLauncherMedia = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
        if (isGranted){
            Log.i("UDF", "MEDIA PERMISSION : GRANTED")
            openImagePicker()
        } else {
            Log.i("UDF", "MEDIA PERMISSION : DENIED")
        }
    }
    private val requestPermissionLauncherCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
        if (isGranted){
            Log.i("UDF", "CAMERA PERMISSION : GRANTED")
            openCamera()
        } else {
            Log.i("UDF", "CAMERA PERMISSION : DENIED")
        }
    }
    private lateinit var currentPhotoPath: String
    private var opponentImage: ImageView? = null

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, ImageUtils.PICK_IMAGE_REQUEST_CODE)
    }
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.cornerpocket.fileprovider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, ImageUtils.CAMERA_PIC_REQUEST)
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //HANDLE GALLERY SELECTION
        if (requestCode == ImageUtils.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            imageUri?.let {
                opponentImage?.setImageURI(it)
                unCroppedImage = it
                try {
                    ImageUtils.startCropActivity(it, cropImageLauncher)
                } catch (e : Exception) {
                    Log.i("UDF", "EXCEPTION CAUGHT : $e")
                }
            }
        }

        //HANDLE CAMERA USAGE
        if (requestCode == ImageUtils.CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                unCroppedImage = uri
                opponentImage?.setImageURI(uri)
                ImageUtils.startCropActivity(uri, cropImageLauncher)
            } else {
                Log.e("onActivityResult", "File does not exist: $currentPhotoPath")
            }
        }
    }

    private var croppedImage : Bitmap? = null
    private var unCroppedImage : Uri? = null

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val cropResult = result.uriContent
            cropResult?.let { uri ->
                croppedImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                opponentImage?.setImageBitmap(croppedImage)
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.cropping_failed, result.error?.message), Toast.LENGTH_SHORT).show()
        }
    }

    //endregion

}