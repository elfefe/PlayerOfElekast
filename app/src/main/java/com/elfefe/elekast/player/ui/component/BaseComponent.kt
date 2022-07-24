package com.elfefe.elekast.player.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import androidx.navigation.NavGraphBuilder
import com.elfefe.elekast.player.ui.StartActivity
import com.elfefe.elekast.player.utils.TRANSITION
import com.elfefe.elekast.player.utils.loge
import com.elfefe.elekast.player.utils.transition

abstract class BaseComponent(protected open val activity: StartActivity) {
    abstract val destination: String

    var state: (Boolean) -> Long = { 0L }

    abstract fun NavGraphBuilder.compose()

    fun setComponent() {
        currentComponent = this
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun Animated(enter: Int = 0, exit: Int = 0, content: @Composable () -> Unit) {
        this.loge = destination
        var visible by remember { mutableStateOf(true) }
        state = { isVisible -> visible = isVisible; TRANSITION.toLong() }
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(transition(enter)),
            exit = fadeOut(transition(exit)),
            initiallyVisible = false
        ) {
            content()
        }
    }

    companion object {
        var currentComponent: BaseComponent? = null
            private set
    }
}