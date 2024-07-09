package com.example.cornerpocket.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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
        Log.i("UDF", "FILE : $file")

        return if (file.exists()) {
            Uri.fromFile(file)
        } else {
            null
        }
    }

    const val PICK_IMAGE_REQUEST_CODE = 1002
    const val CAMERA_PIC_REQUEST = 1337
    fun openImagePicker(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }
    fun showPhotoAlertDialog(context : Context, rplm : ActivityResultLauncher<String>, rplc : ActivityResultLauncher<String>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Add photo")
        builder.setMessage("Use photo from: ")

        builder.setPositiveButton("camera") { dialog, _ ->
            rplc.launch(Manifest.permission.CAMERA)
            dialog.dismiss()
        }

        builder.setNegativeButton("Camera roll") { dialog, _ ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                rplm.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                rplm.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun bitmapToUri(bitmap: Bitmap, context: Context): Uri {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
        }
        return FileProvider.getUriForFile(context, "com.example.cornerpocket.fileprovider", file)
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

    fun startCropActivity(bitmap: Bitmap, cropLauncher: ActivityResultLauncher<CropImageContractOptions>, context: Context) {
        val sourceUri = bitmapToUri(bitmap, context)
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

}