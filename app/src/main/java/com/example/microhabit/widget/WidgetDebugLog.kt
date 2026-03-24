package com.example.microhabit.widget

import android.util.Log

internal object WidgetDebugLog {
    private const val TAG = "WidgetFlow"

    fun d(message: String) {
        Log.d(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
}
