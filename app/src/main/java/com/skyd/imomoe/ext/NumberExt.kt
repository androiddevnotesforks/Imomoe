package com.skyd.imomoe.ext

import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * 只拼接百分号%
 */
inline val Int.percentage: String
    get() = "${this}%"

/**
 * 只拼接百分号%
 */
fun Double.percentage(pattern: String = "0.##"): String {
    val format = DecimalFormat(pattern)
    format.roundingMode = RoundingMode.FLOOR
    return "${format.format(this)}%"
}

/**
 * 乘100后拼接百分号
 */
fun Int.toPercentage(): String = "${this * 100}%"

fun Int.toBoolean(): Boolean = this != 0