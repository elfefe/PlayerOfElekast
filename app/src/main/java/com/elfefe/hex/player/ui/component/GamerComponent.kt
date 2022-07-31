package com.elfefe.hex.player.ui.component

import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfefe.hex.player.R
import com.elfefe.hex.player.mvvm.FirebaseViewModel
import com.elfefe.hex.player.ui.StartActivity
import com.elfefe.hex.player.ui.theme.IntroButton
import com.elfefe.hex.player.ui.theme.Subtitle
import com.elfefe.hex.player.utils.*
import com.elfefe.hex.player.utils.extensions.resString
import com.elfefe.hex.player.utils.extensions.user


class GamerComponent(activity: StartActivity) : BaseComponent(activity) {
    override val destination = GAMER

    private val firebaseViewModel by activity.viewModels<FirebaseViewModel>()

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
            Subtitle(text = stringResource(R.string.title_gamer_choice, user?.displayName ?: missingData("authentication")))
            Column(
                modifier = Modifier
                    .fillMaxHeight(.5f),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IntroButton(
                    text = "PLAYER",
                    onClick = {
                        activity.navigateTo(MAIN)
                    }
                )
                IntroButton(
                    text = "GAME MASTER",
                    onClick = {
                        activity.navigateTo(MAIN)
                    }
                )
            }
        }
    }
}