package com.example.cornerpocket.presentation.registration

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.CropImageContract
import com.example.cornerpocket.R
import com.example.cornerpocket.Utils.ImageUtils
import com.example.cornerpocket.Utils.NavigationUtils
import com.example.cornerpocket.databinding.FragmentUserDetailsBinding
import com.example.cornerpocket.models.User
import com.example.cornerpocket.viewModels.UserViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrationFragment : Fragment() {
    private var _binding : FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //region BACK PRESS
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //disable back button
            }
        })
        //endregion

        formatPage()

        binding.clAddImage.setOnClickListener {
            showPhotoAlertDialog()
        }

        // Add TextWatcher to the EditText
        binding.inputTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Enable the button if the EditText is not empty, otherwise disable it
                if (s.isNullOrEmpty()){
                    disableButton()
                } else {
                    enableButton()
                }
            }
        })

    }

    private fun formatPage(){
        binding.backText.visibility = View.GONE
        binding.backButton.visibility = View.GONE

        binding.textView2.text = getString(R.string.create_account)
        binding.applyBtn.text = getString(R.string.create)

        disableButton()
    }

    private fun disableButton() {
        binding.applyBtn.isClickable = false
        binding.applyBtn.alpha = .5f

        binding.applyBtn.setOnClickListener {
            Toast.makeText(requireActivity(), R.string.please_enter_a_name, Toast.LENGTH_SHORT).show()
        }
    }

    private fun enableButton() {
        binding.applyBtn.isClickable = true
        binding.applyBtn.alpha = 1f

        binding.applyBtn.setOnClickListener {
            userViewModel.viewModelScope.launch {

                userViewModel.insertUser(user = User().apply {
                    name = binding.inputTextName.text.toString()

                    if (croppedImage != null) {
                        //get opponent and use _id to store cropped image
                        ImageUtils.saveCroppedImageToLocalStorage(
                            requireContext(),
                            croppedImage!!,
                            this._id.toString()
                        )

                    } else if (unCroppedImage != null) {
                        //get opponent and use _id to store un cropped image
                        ImageUtils.saveImageToLocalStorage(
                            requireContext(),
                            unCroppedImage!!,
                            this._id.toString()
                        )
                    }
                })

                Toast.makeText(context, getString(R.string.profile_created), Toast.LENGTH_SHORT).show()

                NavigationUtils.navigateAndClearBackStack(
                    findNavController(),
                    R.id.action_registrationFragment_to_playFragment,
                    R.id.registrationFragment
                )
            }
        }
    }

    //region PHOTO FUNCTIONS

    private var croppedImage : Bitmap? = null
    private var unCroppedImage : Uri? = null

    private fun showPhotoAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add photo")
        builder.setMessage("Use photo from: ")

        builder.setPositiveButton("camera") { dialog, _ ->
            requestPermissionLauncherCamera.launch(Manifest.permission.CAMERA)
            dialog.dismiss()
        }

        builder.setNegativeButton("Camera roll") { dialog, _ ->
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
                binding.userImage.setImageURI(it)
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
                binding.userImage.setImageURI(uri)
                ImageUtils.startCropActivity(uri, cropImageLauncher)
            } else {
                Log.e("onActivityResult", "File does not exist: $currentPhotoPath")
            }
        }
    }

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val cropResult = result.uriContent
            cropResult?.let { uri ->
                lifecycleScope.launch {
                    croppedImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                    binding.userImage.setImageBitmap(croppedImage)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Cropping failed: ${result.error?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    //endregion


}