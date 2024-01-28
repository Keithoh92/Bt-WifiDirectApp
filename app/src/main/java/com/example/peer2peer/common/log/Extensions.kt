package com.example.peer2peer.common.log

import java.math.BigDecimal
import java.math.RoundingMode

internal fun StackTraceElement.simpleClassName(): String = className.substringAfterLast(".")

internal fun StackTraceElement.asString(): String = "($fileName:$lineNumber) $methodName()"

internal fun StackTraceElement.tag(): String = simpleClassName().take(22)

internal fun StackTraceElement.msg(message: String): String = "${asString()} :: $message"

internal fun Array<StackTraceElement>.callerStackElementFrom(any: Any): StackTraceElement {
    val index = this.indexOfLast { it.className.contains(any::class.java.name, true) }
    return this[index + 1]
}

internal fun String.oversize(size: Int): Boolean = this.length > size

internal fun String.splitBySize(size: Int = 5000): List<String> {
    if (!this.oversize(size)) return listOf(this)

    val listSize = (this.length / size).toDouble().scale(0, RoundingMode.UP).toInt()
    val values = mutableListOf("The message may have been split")

    for (i in 0..listSize) {
        val startIndex = i * size
        var endIndex = size + startIndex

        if (endIndex > this.length) endIndex = this.length

        val content = this.substring(startIndex, endIndex)

        values.add(content)
    }

    return values
}

fun Double.scale(newScale: Int, roundingMode: RoundingMode): Double {
    return BigDecimal(this)
        .setScale(newScale, roundingMode)
        .toDouble()
}
