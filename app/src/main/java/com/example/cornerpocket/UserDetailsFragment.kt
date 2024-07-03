package com.example.cornerpocket

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.cornerpocket.databinding.FragmentUserDetailsBinding
import com.example.cornerpocket.viewModels.UserViewModel
import kotlinx.coroutines.launch

class UserDetailsFragment : Fragment() {
    private var _binding : FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        val user = userViewModel.getUser()
        Log.i("UDF", "user: $user")

        if (user != null){
            binding.userNameTv.text = user.name

            val pfp = ImageUtils.getImageFromLocalStorage(requireContext(), user._id.toString())
            binding.userImage.setImageURI(pfp)
        }

        binding.clAddImage.setOnClickListener {
            showPhotoAlertDialog()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_user_details_to_playFragment)
        }

        binding.applyBtn.setOnClickListener {
            userViewModel.viewModelScope.launch {
                val updatedName = binding.inputTextName.text.toString()
                if (updatedName.isNotBlank()){
                    userViewModel.updateUser(updatedName)
                    val updatedUser = userViewModel.getUser()
                    if (updatedUser != null){
                        binding.userNameTv.text = updatedUser.name
                    }
                } else {
                    Toast.makeText(requireActivity(), "TEXT FIELD CANNOT BE BLANK", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

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
        } else {
            Log.i("UDF", "CAMERA PERMISSION : DENIED")
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, ImageUtils.PICK_IMAGE_REQUEST_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImageUtils.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            imageUri?.let {
                userViewModel.viewModelScope.launch {
                    val user = userViewModel.getUser()
                    if (user != null){
                        val path = user._id.toString()
                        ImageUtils.saveImageToLocalStorage(requireContext(), it, path)
                    }
                    binding.userImage.setImageURI(it)
                }
            }
        }
    }

}