package com.mauleCo.cornerpocket.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.mauleCo.cornerpocket.R
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {
        fun saveImageToLocalStorage(context : Context, imageUri: Uri, path : String) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val file = File(context.getExternalFilesDir(null), path)
        val outputStream = FileOutputStream(file)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        Log.i("UDF", "Image saved at : ${file.absolutePath}")
    }

    fun getImageFromLocalStorage(context : Context, path : String) : Uri? {
        val file = File(context.getExternalFilesDir(null), path)
//        Log.i("UDF", "FILE : $file")

        return if (file.exists()) {
            Uri.fromFile(file)
        } else {
            null
        }
    }

    const val PICK_IMAGE_REQUEST_CODE = 1002
    const val CAMERA_PIC_REQUEST = 1337

    private fun bitmapToUri(bitmap: Bitmap, context: Context): Uri {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
        }
        return FileProvider.getUriForFile(context, "com.mauleCo.cornerpocket.fileprovider", file)
    }

    fun startCropActivity(sourceUri: Uri, cropLauncher: ActivityResultLauncher<CropImageContractOptions>) {
        val options = CropImageContractOptions(
            uri = sourceUri,
            cropImageOptions = CropImageOptions().apply {
                guidelines = CropImageView.Guidelines.ON
                aspectRatioX = 1
                aspectRatioY = 1
                fixAspectRatio = true
            }
        )
        cropLauncher.launch(options)
    }

    fun saveCroppedImageToLocalStorage(context: Context, bitmap: Bitmap, imageView: ImageView, path : String) {
        val file = File(context.getExternalFilesDir(null), path)
        val outputStream = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        outputStream.close()

        imageView.setImageURI(Uri.fromFile(file))
    }

    fun saveCroppedImageToLocalStorage(context: Context, bitmap: Bitmap, path : String) {
        val file = File(context.getExternalFilesDir(null), path)
        val outputStream = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        outputStream.close()
    }

    //region PHOTO FUNCTIONS

    fun openImagePicker(imagePickerLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    fun openCamera(
        context: Context,
        cameraLauncher: ActivityResultLauncher<Intent>,
        onPhotoPathCreated: (String) -> Unit
    ) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile(context).also { file ->
                onPhotoPathCreated(file.absolutePath)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                context,
                "com.mauleCo.cornerpocket.fileprovider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(intent)
        }
    }

    private lateinit var currentPhotoPath: String
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        Log.e("IU", "createImageFile")

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        //HANDLE GALLERY SELECTION
//        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            val imageUri = data.data
//            imageUri?.let {
//                binding.userImage.setImageURI(it)
//                unCroppedImage = it
//
//                try {
//                    startCropActivity(it, cropImageLauncher)
//                } catch (e : Exception) {
//                    Log.i("UDF", "EXCEPTION CAUGHT : $e")
//                }
//            }
//        }
//
//        //HANDLE CAMERA USAGE
//        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
//            val file = File(currentPhotoPath)
//            if (file.exists()) {
//                val uri = Uri.fromFile(file)
//                unCroppedImage = uri
//                binding.userImage.setImageURI(uri)
//                startCropActivity(uri, cropImageLauncher)
//            } else {
//                Log.e("onActivityResult", "File does not exist: $currentPhotoPath")
//            }
//        }
//    }
//
//    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
//        if (result.isSuccessful) {
//            val cropResult = result.uriContent
//            cropResult?.let { uri ->
//                lifecycleScope.launch {
//                    croppedImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
//                    binding.userImage.setImageBitmap(croppedImage)
//                }
//            }
//        } else {
//            Toast.makeText(requireContext(), getString(R.string.cropping_failed), Toast.LENGTH_SHORT).show()
//        }
//    }

    //endregion


}