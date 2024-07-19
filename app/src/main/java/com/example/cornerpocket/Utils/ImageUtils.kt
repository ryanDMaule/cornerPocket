package com.example.cornerpocket.Utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
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