package com.elfefe.elekast.player.ui.component

import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import com.elfefe.elekast.player.R
import com.elfefe.elekast.player.ui.StartActivity
import com.elfefe.elekast.player.ui.theme.FixedIconButton
import com.elfefe.elekast.player.ui.theme.IntroButton
import com.elfefe.elekast.player.ui.theme.Title
import com.elfefe.elekast.player.utils.*
import com.elfefe.elekast.player.utils.extensions.resString
import com.google.android.material.resources.MaterialResources


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
                } ?: missingData("gamer")
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
            val vals = listOf(
                "CRETE ONE",
                "CREAT ONE",
                "CREAT&E ONE",
                "CREATéE ONE",
                "CRE(ATE ONE",
                "CR'EATE ONE",
                "CREAT_E ONE",
                "CREATE çONE",
                "CREA1TE ONE",
                "CR2EATE ONE",
                "CRE4TE ONE",
                "CREATE 6ONE",
                "CREA7TE ONE",
                "CREATE O8NE",
                "CR8EATE ONE",
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight(.2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Title(text = stringResource(R.string.title_games_list, gamer))
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(.6f)
                    .graphicsLayer { alpha = 0.99F }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to Color.Transparent,
                                .1f to Color.Black,
                                .9f to Color.Black,
                                1f to Color.Transparent
                            ),
                            blendMode = BlendMode.DstIn
                        )
                    }
            ) {
                items(vals) { text ->
                    Space()
                    IntroButton(text = text) {
                        activity.navigateTo(MAIN)
                    }
                }
            }
            FixedIconButton(Icons.Default.Add) {

            }
        }
    }
}