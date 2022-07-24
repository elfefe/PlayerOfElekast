package com.elfefe.elekast.player.ui.component

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfefe.elekast.player.ui.StartActivity
import com.elfefe.elekast.player.utils.MAIN
import com.elfefe.elekast.player.utils.TRANSITION

class MainComponent(activity: StartActivity) : BaseComponent(activity) {
    override val destination = MAIN

    override fun NavGraphBuilder.compose() =
        composable(destination) {
            Animated(TRANSITION, TRANSITION) {
                Greeting()
            }
        }

    @Composable
    fun Greeting() {
        setComponent()
        Text(text = "Hello youuuuuuu!")
    }

    @Composable
    private fun Slider(modifier: Modifier) {
        LazyColumn {

        }
    }

    @Composable
    private fun CardsSlider(modifier: Modifier) {

    }
}