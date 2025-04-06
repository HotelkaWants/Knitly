package com.hotelka.knitlyWants.Tools

import android.content.Context
import com.hotelka.knitlyWants.R

fun formatRepeatedText(input: String): String {
    val elements = input.split(",").map { it.trim() }.toMutableList()
    val result = mutableListOf<String>()

    while (elements.isNotEmpty()) {
        val first = elements.removeAt(0)
        var count = 1
        val repeatedElements = mutableListOf(first)

        // Find consecutive repeating elements
        while (elements.isNotEmpty() && elements.first() == first) {
            elements.removeAt(0)
            count++
            repeatedElements.add(first)
        }

        if (count > 1) {
            //Find the pattern inside the repeated elements
            val pattern = first.replace("sc", "s").replace("inc", "i")
            result.add("($pattern)x$count")
        } else {
            result.add(first)
        }
    }

    return result.joinToString(", ")
}

fun distributeDecreases(
    context: Context,
    totalStitches: Int,
    decreases: Int,
    distribution: String = context.getString(R.string.uniform)
): String {
    var sc = context.getString(R.string.sc)
    var dec = context.getString(R.string.dec)
    var total = totalStitches - decreases

    if (decreases == 0) return "$totalStitches $sc"

    if (decreases == totalStitches) {
        return "(1 $dec)x $decreases ($total)"
    }

    var result = ""

    when (distribution) {
        context.getString(R.string.uniform) -> {
            val sbn = totalStitches / decreases - 1
            val remains = totalStitches % decreases
            var total = sbn
            var pattern = ""
            if (remains != 0) {
                for (i in 1..decreases) {
                    total += sbn
                    result += ("$sbn $sc, 1 $dec")
                    if (i <= remains) {
                        result += (", 1 $sc")
                    }
                    if (i != decreases) {
                        result += (", ")
                    } else {
                        result = result.replace(pattern, "")
                    }
                }
            } else {
                result += ("(1 $dec)x$decreases")
            }
        }

        context.getString(R.string.start) -> {
            fun calculateInterval(i: Int, totalDecreases: Int): Int {
                return totalDecreases - i
            }

            var currentStitch = 0
            for (i in 1..decreases) {
                val interval = calculateInterval(i, decreases)
                result += if (interval > 0) {
                    "$interval $sc, 1 $dec"
                } else {
                    "1 $dec"
                }
                if (i != decreases) {
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
            for (i in 1..decreases) {
                val interval = calculateInterval(i)
                currentStitch += if (interval > 0) interval + 1 else 1
            }

            val remainingStitches = totalStitches - currentStitch
            if (remainingStitches > 0) {
                result += "$remainingStitches $sc, "
            }
            for (i in 1..decreases) {
                val interval = calculateInterval(i)
                result += if (interval > 0) {
                    "$interval $sc, 1 $dec"
                } else {
                    "1 $dec"
                }
                if (i != decreases) {
                    result += ", "
                }
            }
        }

        else -> throw IllegalArgumentException()
    }
    result += " ($total)"

    return result
}

fun distributeIncreases(
    context: Context,
    totalStitches: Int,
    increases: Int,
    distribution: String = context.getString(R.string.uniform)
): String {
    var sc = context.getString(R.string.sc)
    var inc = context.getString(R.string.inc)
    var total = totalStitches + increases
    if (increases == 0) return "$totalStitches $"

    if (increases == totalStitches) {
        return "(1 $inc)x $increases ($total)"
    }

    var result = ""

    when (distribution) {
        context.getString(R.string.uniform) -> {
            val sbn = totalStitches / increases - 1
            val remains = totalStitches % increases
            var total = sbn
            var pattern = ""
            if (remains != 0) {
                for (i in 1..increases) {
                    total += sbn
                    result += ("$sbn $sc, 1 $inc")
                    if (i <= remains) {
                        result += (", 1 $sc")
                    }
                    if (i != increases) {
                        result += (", ")
                    } else {
                        result = result.replace(pattern, "")
                    }
                }
            } else {
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
                result += if (interval > 0) {
                    "$interval $sc, 1 $inc"
                } else {
                    "1 $inc"
                }
                if (i != increases) {
                    result += ", "
                }
            }

        }

        else -> throw IllegalArgumentException()
    }
    result += " ($total)"

    return result
}