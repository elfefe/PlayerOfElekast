package com.elfefe.elekast.player.ui.component

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import com.elfefe.elekast.player.ui.StartActivity
import com.elfefe.elekast.player.ui.theme.IntroButton
import com.elfefe.elekast.player.ui.theme.Title
import com.elfefe.elekast.player.utils.*


class GameComponent(activity: StartActivity) : BaseComponent(activity) {
    override val destination = GAME

    override fun NavGraphBuilder.compose() =
        composable(
            route = "${destination}/{gamer}",
            arguments = listOf(navArgument("gamer") {})
        ) { entry ->
            Animated(TRANSITION, TRANSITION) {
                entry.arguments?.getString("gamer")?.let { gamer ->
                    SelectGame(gamer)
                } ?: run {
                    Toast.makeText(activity, "Your choice has not been saved", Toast.LENGTH_SHORT).show()
                    activity.onBackPressed()
                }
            }
        }

    @Composable
    fun SelectGame(gamer: String) {
        setComponent()
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val vals = listOf("CRETE ONE","CREAT ONE","CREAT&E ONE","CREATéE ONE","CRE(ATE ONE","CR'EATE ONE","CREAT_E ONE","CREATE çONE","CREA1TE ONE","CR2EATE ONE","CRE4TE ONE","CREATE 6ONE","CREA7TE ONE","CREATE O8NE","CR8EATE ONE",)
            Column(
                modifier = Modifier
                .fillMaxHeight(.2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Title(text = "$gamer find your game")
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                items(vals) { text ->
                    IntroButton(
                        text = text,
                        onClick = {
                            activity.navigateTo(MAIN)
                        }
                    )
                    Space()
                }
            }
        }
    }
}