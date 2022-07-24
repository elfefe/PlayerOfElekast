package com.elfefe.elekast.player.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun CoroutineScope.onMain(block: () -> Unit) {
    withContext(Dispatchers.Main) {
        block()
    }
}