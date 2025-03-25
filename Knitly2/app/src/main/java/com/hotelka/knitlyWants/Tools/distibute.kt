package com.hotelka.knitlyWants.Tools

import android.content.Context
import com.google.android.play.core.integrity.p
import com.hotelka.knitlyWants.R

fun formatRepeatedText(input: String): String {
    // Split the input string into individual components
    val components = input.split(", ")

    // Create a list to hold the formatted components
    val formattedComponents = mutableListOf<String>()

    // Use a sliding window to detect repeating patterns
    var i = 0
    while (i < components.size) {
        // Try to find the longest repeating pattern starting at position i
        var patternLength = 1
        while (i + patternLength * 2 <= components.size) {
            val firstSegment = components.subList(i, i + patternLength)
            val secondSegment = components.subList(i + patternLength, i + patternLength * 2)
            if (firstSegment != secondSegment) {
                break
            }
            patternLength++
        }

        // If a repeating pattern is found, format it
        if (patternLength > 1) {
            val pattern = components.subList(i, i + patternLength).joinToString(", ")
            var repeatCount = 1
            while (i + patternLength * (repeatCount + 1) <= components.size &&
                components.subList(i, i + patternLength) == components.subList(i + patternLength * repeatCount, i + patternLength * (repeatCount + 1))
            ) {
                repeatCount++
            }
            formattedComponents.add("($pattern)x$repeatCount")
            i += patternLength * repeatCount
        } else {
            // If no repeating pattern, add the component as is
            formattedComponents.add(components[i])
            i++
        }
    }

    // Join the formatted components with ", " and return the result
    return formattedComponents.joinToString(", ")
}


fun distributeIncreases(
    context: Context,
    totalStitches: Int,
    increases: Int,
    distribution: String = context.getString(R.string.uniform)
): String {
    var sc = context.getString(R.string.sc)
    var inc = context.getString(R.string.inc)

    if (increases == 0) return "$totalStitches $"

    if (increases == totalStitches) {
        return "(1 $inc)x $increases"
    }

    var result = ""

    when (distribution) {
        context.getString(R.string.uniform) -> {
            val sbn = totalStitches / increases - 1
            val remains = totalStitches % increases
            var total = sbn
            var pattern = ""
            if (remains != 0){
                for (i in 1..increases) {
                    total += sbn
                    result += ("$sbn $sc, 1 $inc")
                    if (i <= remains){
                        result += (", 1 $sc")
                    }
                    if (i != increases){
                        result += (", ")
                    } else{
                        result = result.replace(pattern, "")
                    }
                }
            } else{
                result += ("($sbn $sc, 1 $inc)x$increases")
            }
        }

        context.getString(R.string.start) -> {
            fun calculateInterval(i: Int, totalIncreases: Int): Int {
                return totalIncreases - i
            }

            var currentStitch = 0
            for (i in 1..increases) {
                val interval = calculateInterval(i, increases)
                result += if (interval > 0) {
                    "$interval $sc, 1 $inc"
                } else {
                    "1 $inc"
                }
                if (i != increases) {
                    result += ", "
                }
                currentStitch += if (interval > 0) interval + 1 else 1
            }

            val remainingStitches = totalStitches - currentStitch
            if (remainingStitches > 0) {
                result += ", $remainingStitches $sc"
            }
        }

        context.getString(R.string.end) -> {
            fun calculateInterval(i: Int): Int {
                return i - 1
            }
            var currentStitch = 0
            for (i in 1..increases) {
                val interval = calculateInterval(i)
                currentStitch += if (interval > 0) interval + 1 else 1
            }

            val remainingStitches = totalStitches - currentStitch
            if (remainingStitches > 0) {
                result += "$remainingStitches $sc, "
            }
            for (i in 1..increases) {
                val interval = calculateInterval(i)
                if (interval > 0) {
                    result += "$interval $sc, 1 $inc"
                } else {
                    result += "1 $inc"
                }
                if (i != increases) {
                    result += ", "
                }
            }

        }

        else -> throw IllegalArgumentException()
    }

    return result
}