package com.example.cornerpocket

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
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

    }
}