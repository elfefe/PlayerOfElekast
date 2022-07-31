package com.elfefe.hex.player.utils.extensions

import android.content.Context
import android.content.SharedPreferences
import com.elfefe.hex.player.R
import com.elfefe.hex.player.utils.app

val prefs: SharedPreferences
    get() = app.getSharedPreferences(resString(R.string.app_name), Context.MODE_PRIVATE)

const val GOOGLE_ID_TOKEN_PREF = "pref:Google ID Token"