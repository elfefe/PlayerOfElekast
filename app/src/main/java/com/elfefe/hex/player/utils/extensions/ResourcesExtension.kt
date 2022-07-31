package com.elfefe.hex.player.utils.extensions

import com.elfefe.hex.player.utils.app

fun resString(res: Int, vararg args: Any?) = app.getString(res, args.map { it ?: "Unkown" })