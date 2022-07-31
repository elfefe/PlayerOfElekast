package com.elfefe.hex.player.utils

import android.util.Log

var Any.loge: String
    set(value) {
        Log.e(javaClass.simpleName, value)
    }
    get() = "Set this variable to log a message."

var Any.logd: String
    set(value) {
        Log.d(javaClass.simpleName, value)
    }
    get() = "Set this variable to log an error."

var Any.logw: String
    set(value) {
        Log.w(javaClass.simpleName, value)
    }
    get() = "Set this variable to log a warning."