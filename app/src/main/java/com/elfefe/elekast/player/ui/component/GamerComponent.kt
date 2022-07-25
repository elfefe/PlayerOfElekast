package com.elfefe.elekast.player.ui.component

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfefe.elekast.player.ui.StartActivity
import com.elfefe.elekast.player.ui.theme.IntroButton
import com.elfefe.elekast.player.ui.theme.Subtitle
import com.elfefe.elekast.player.ui.theme.Title
import com.elfefe.elekast.player.utils.*
import com.google.firebase.auth.FirebaseAuth


class GamerComponent(activity: StartActivity) : BaseComponent(activity) {
    override val destination = GAMER

    override fun NavGraphBuilder.compose() =
        composable(destination) {
            Animated(TRANSITION, TRANSITION) {
                SelectGamer()
            }
        }

    @Composable
    fun SelectGamer() {
        setComponent()
        Column(
            modifier = Modifier
                .fillMaxWidth(.8f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Subtitle(text = "Tell me ${FirebaseAuth.getInstance().currentUser?.displayName}, what's your rôle in Elekast ?")
            Column(
                modifier = Modifier
                    .fillMaxHeight(.5f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IntroButton(
                    text = "PLAYER",
                    onClick = {
                        activity.navigateTo(GAME, "Player")
                    }
                )
                IntroButton(
                    text = "GAME MASTER",
                    onClick = {
                        activity.navigateTo(GAME, "Game master")
                    }
                )
            }
        }
    }
}