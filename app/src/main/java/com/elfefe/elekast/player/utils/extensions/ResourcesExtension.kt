package com.elfefe.elekast.player.utils.extensions

import com.elfefe.elekast.player.utils.app

fun resString(res: Int, vararg args: Any?) = app.getString(res, args.map { it ?: "Unkown" })