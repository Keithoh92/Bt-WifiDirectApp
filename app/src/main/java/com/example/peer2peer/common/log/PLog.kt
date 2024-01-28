package com.example.peer2peer.common.log

import android.util.Log

object PLog {

    @JvmStatic
    fun d(message: String) {

        val messages = message.splitBySize()

        messages.forEach {
            Log.d(stackElement.tag(), stackElement.msg(it))
        }
    }

    private val stackElement: StackTraceElement
        get() = Thread.currentThread().stackTrace.callerStackElementFrom(this)
}