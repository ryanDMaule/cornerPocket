package com.example.cornerpocket.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

class HelperFunctions {
    companion object {
        private fun ordinalOf(i: Int): String {
            val iAbs = i.absoluteValue // if you want negative ordinals, or just use i
            return "$i" + if (iAbs % 100 in 11..13) "th" else when (iAbs % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }

        /**
         * Takes a Date and returns a LocalDate object
         */
        private fun convertDateToLocalDate(date: Date): LocalDate {
            return Instant.ofEpochMilli(date.time)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }

        /**
         * Takes a Long and returns a Date object
         */
        fun longConversion(timestamp : Long): Date {
            return Date(timestamp)
        }

        /**
         * Formats a Date object into a readable string in the format "12th May, 2024"
         */
        fun formatDate(date : Date) : String {
            val localDate = convertDateToLocalDate(date)
            return "${ordinalOf(localDate.dayOfMonth)} ${localDate.month.name.lowercase(Locale.ROOT).replaceFirstChar { it.titlecase() }}, ${localDate.year}"
        }

        /**
         * Formats a Date into a readable string in the format dd/MM/yyyy
         */
        fun formatDateToString(date: Date): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(date)
        }

        private const val ANIM_DURATION = 200L
        fun rotate45clockwise(image : ImageView){
            image.animate().apply {
                duration = ANIM_DURATION
                rotation(45F)
            }
        }

        fun rotate45anticlockwise(image : ImageView){
            image.animate().apply {
                duration = ANIM_DURATION
                rotation(0F)
            }
        }

        fun expandView(v: View) {
            val matchParentMeasureSpec =
                View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
            val wrapContentMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
            val targetHeight = v.measuredHeight

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.layoutParams.height = 1
            v.visibility = View.VISIBLE
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    v.layoutParams.height =
                        if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // Expansion speed of 1dp/ms
//        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            a.duration = ANIM_DURATION
            v.startAnimation(a)
        }

        fun collapseView(v: View) {
            val initialHeight = v.measuredHeight
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height =
                            initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // Collapse speed of 1dp/ms
//        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            a.duration = ANIM_DURATION
            v.startAnimation(a)
        }

        fun calculatePercentage(totalGames: Int, divisor: Int): String {
            if (totalGames == 0) {
                return "0.0" // Avoid division by zero
            }
            val percentage = (divisor.toDouble() / totalGames.toDouble()) * 100
            return String.format("%.1f", percentage)
        }

        fun getChronometerElapsedTimeInSeconds(chronometerText: CharSequence): Int {
            val timeParts = chronometerText.split(":").map { it.toInt() }
            return when (timeParts.size) {
                2 -> {
                    // Format is MM:SS
                    val minutes = timeParts[0]
                    val seconds = timeParts[1]
                    minutes * 60 + seconds
                }
                3 -> {
                    // Format is HH:MM:SS
                    val hours = timeParts[0]
                    val minutes = timeParts[1]
                    val seconds = timeParts[2]
                    hours * 3600 + minutes * 60 + seconds
                }
                else -> 0 // Default case if the format is unexpected
            }
        }

        fun formatSecondsToMMSS(totalSeconds: Int): String {
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

        /**
         * @param seconds time to be converted
         * @return time formatted to "3H 11M 42S"
         */
        fun formatTime(seconds: Int) : String {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60

            val formattedTime = StringBuilder()

            if (hours > 0) {
                formattedTime.append("${hours}H ")
            }
            if (minutes > 0 || hours > 0) {
                formattedTime.append("${minutes}M ")
            }
            formattedTime.append("${remainingSeconds}S")

            return formattedTime.toString().trim()
        }

        fun formatSecondsToHHMMSS(seconds: Int): String {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val remainingSeconds = seconds % 60

            // Format the time string using String.format to ensure zero-padding
            return if (hours == 0){
                String.format("%02d:%02d", minutes, remainingSeconds)
            } else {
                String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
            }
        }

        fun getScreenWidth(context: Context): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // For API 30+ (Android 11+)
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val windowMetrics = windowManager.currentWindowMetrics
                val insets = windowMetrics.windowInsets
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                val bounds = windowMetrics.bounds
                bounds.width() - insets.left - insets.right
            } else {
                // For older versions
                val displayMetrics = DisplayMetrics()
                val windowManager = ContextCompat.getSystemService(context, WindowManager::class.java)
                windowManager?.defaultDisplay?.getMetrics(displayMetrics)
                displayMetrics.widthPixels
            }
        }

        fun promptUserForReview(activity: Activity) {
            val reviewManager = ReviewManagerFactory.create(activity)

            val request = reviewManager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e("HF", "reviewInfo : task successful")

                    // We can get the ReviewInfo object
                    val reviewInfo: ReviewInfo? = task.result
                    if (reviewInfo != null){
                        val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                        flow.addOnCompleteListener { _ ->
                            // The review flow has finished. The API does not indicate whether
                            // the user reviewed or not, or even whether the review dialog was shown.
                            // Thus, no need to do anything here.
                        }
                    } else {
                        Log.e("HF", "reviewInfo : NULL")
                    }
                } else {
                    // There was some problem, log or handle the error code.
                    Log.e("HF", "Unable to show review dialog")
                }
            }
        }

        fun setTextColorSection(context: Context, textView: TextView, fullText: String, sectionText: String, color: Int) {
            // Get the color from the color resource ID
            val color = ContextCompat.getColor(context, color)

            // Create a SpannableString from the full text
            val spannableString = SpannableString(fullText)

            // Find the start and end indexes of the section to color
            val startIndex = fullText.indexOf(sectionText)
            val endIndex = startIndex + sectionText.length

            if (startIndex != -1) {
                // Apply the color to the specified section
                val colorSpan = ForegroundColorSpan(color)
                spannableString.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            // Set the text on the TextView
            textView.text = spannableString
        }

        /**
         * Opens the app settings screen for the current application.
         *
         * @param context: Context from which the function is called, such as Activity or Fragment context.
         */
        fun openAppSettings(context: Context) {
            try {
                // Create an intent to open the application details settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    // Set the data URI to the current application package
                    data = Uri.fromParts("package", context.packageName, null)
                }
                // Start the activity using the provided context
                context.startActivity(intent)
            } catch (e: Exception) {
                // Show a Toast message if there is an error while trying to open settings
                Toast.makeText(context, "Unable to open settings", Toast.LENGTH_SHORT).show()
            }
        }

    }
}