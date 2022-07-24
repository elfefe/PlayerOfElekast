package com.elfefe.elekast.player.utils

import androidx.compose.animation.core.keyframes

fun <T>transition(duration: Int, delay: Int = 0) = keyframes<T> {
    durationMillis = duration
    delayMillis = delay
}

const val TRANSITION = 2000