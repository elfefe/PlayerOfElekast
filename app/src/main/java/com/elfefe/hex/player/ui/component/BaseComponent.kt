package com.elfefe.hex.player.ui.component

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import androidx.navigation.NavGraphBuilder
import com.elfefe.hex.player.R
import com.elfefe.hex.player.ui.StartActivity
import com.elfefe.hex.player.utils.TRANSITION
import com.elfefe.hex.player.utils.extensions.crashlytics
import com.elfefe.hex.player.utils.extensions.resString
import com.elfefe.hex.player.utils.loge
import com.elfefe.hex.player.utils.transition
import com.google.firebase.crashlytics.FirebaseCrashlytics

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

    fun missingData(data: String = "") {
        val issue = resString(R.string.back_missing_data, if (data.isEmpty()) data else "missing $data")
        crashlytics.log(issue)
        Toast.makeText(activity, issue, Toast.LENGTH_SHORT).show()
        activity.onBackPressed()
    }

    companion object {
        var currentComponent: BaseComponent? = null
            private set
    }
}