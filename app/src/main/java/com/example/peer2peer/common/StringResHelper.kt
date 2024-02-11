package com.example.peer2peer.common

import android.content.Context
import androidx.annotation.StringRes

class StringResHelper(private val context: Context) {

    fun getString(@StringRes resId: Int): String = context.getString(resId)

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String =
        context.getString(resId, *formatArgs)
}