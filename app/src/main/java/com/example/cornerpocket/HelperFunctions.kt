package com.example.cornerpocket

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
    }
}