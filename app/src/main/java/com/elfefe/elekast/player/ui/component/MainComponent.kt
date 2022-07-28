package com.elfefe.elekast.player.ui.component

import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.elfefe.elekast.player.mvvm.FirebaseViewModel
import com.elfefe.elekast.player.mvvm.api.FileAPI
import com.elfefe.elekast.player.ui.StartActivity
import com.elfefe.elekast.player.ui.theme.Subtitle
import com.elfefe.elekast.player.utils.*
import java.io.File

class MainComponent(activity: StartActivity) : BaseComponent(activity) {
    override val destination = MAIN

    private val firebaseViewModel by activity.viewModels<FirebaseViewModel>()

    override fun NavGraphBuilder.compose() =
        composable(destination) {
            Animated(TRANSITION, TRANSITION) {
                Greeting()
            }
        }

    @Composable
    fun Greeting() {
        Slider(modifier = Modifier)
    }

    @Composable
    private fun Slider(modifier: Modifier) {
        var sliders by remember {
            mutableStateOf(listOf("Rules"))
        }
        var rules by remember {
            mutableStateOf<List<String>>(listOf())
        }
        firebaseViewModel.authentication.observe(activity) {
            if (it is Authentication.Success) {
                rules = FileAPI.rules.listFiles()?.map { file -> file.absolutePath }?.toList() ?: listOf()
            }
        }
        LazyColumn {
            items(sliders) {name ->
                Column(modifier = Modifier.shadow(10.dp).padding(20.dp)) {
                    Row(modifier = Modifier) {
                        Space()
                        Subtitle(text = name)
                    }
                    CardsSlider(modifier = Modifier, paths = rules)
                }
                Space()
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun CardsSlider(modifier: Modifier, paths: List<String>) {
        var image by remember {
            mutableStateOf<AsyncImagePainter?>(null)
        }
        LazyRow {
            items(paths) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest
                    .Builder(LocalContext.current)
                        .data(File(it))
                        .size(Size.ORIGINAL)
                        .build()
                )

                Image(painter = painter, contentDescription = it, modifier = Modifier.clickable {
                    image = painter
                })
                Space()
            }
        }

        image?.run {
            Dialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                onDismissRequest = { image = null }
            ){
                Image(painter = this, contentDescription = "Dialog")
            }
        }
    }
}