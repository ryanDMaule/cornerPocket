package com.example.cornerpocket.Utils

import android.app.Activity
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.TextView
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
        fun ordinalOf(i: Int): String {
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
        fun convertDateToLocalDate(date: Date): LocalDate {
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

    }
}