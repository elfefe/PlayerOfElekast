package com.elfefe.hex.player.ui.component

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import com.elfefe.hex.player.mvvm.FirebaseViewModel
import com.elfefe.hex.player.mvvm.api.FileAPI
import com.elfefe.hex.player.ui.StartActivity
import com.elfefe.hex.player.ui.theme.FixedButtons
import com.elfefe.hex.player.ui.theme.Subtitle
import com.elfefe.hex.player.utils.*
import com.elfefe.hex.player.utils.extensions.GAME_MASTER
import java.io.File
import androidx.compose.ui.Alignment
import com.elfefe.hex.player.R
import com.elfefe.hex.player.mvvm.model.Friend
import com.elfefe.hex.player.mvvm.model.Player
import com.elfefe.hex.player.ui.theme.FixedText
import com.elfefe.hex.player.utils.extensions.resString

class GameMasterComponent(activity: StartActivity) : BaseComponent(activity) {
    override val destination = GAME_MASTER

    private val firebaseViewModel by activity.viewModels<FirebaseViewModel>()

    override fun NavGraphBuilder.compose() =
        composable(destination) {
            Animated(TRANSITION, TRANSITION) {
                Main(
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }

    @Composable
    fun Main(modifier: Modifier) {
        firebaseViewModel.askGameMaster()
        var friends by remember {
            mutableStateOf<List<Friend>>(listOf())
        }
        firebaseViewModel.friends().observe(activity) { found ->
            friends = found
        }
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            var drag by remember {
                mutableStateOf(0f)
            }
            Slider(modifier = Modifier
            )
            FixedText(text = "Drag: $drag")
            Friends(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(512.dp)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            drag = dragAmount
                        }
                    },
                friends
            )
        }
    }

    @Composable
    private fun Slider(modifier: Modifier) {
        var sliders by remember {
            mutableStateOf(listOf("Rules"))
        }
        var rules by remember {
            mutableStateOf<List<File>>(listOf())
        }
        firebaseViewModel.authentication.observe(activity) {
            if (it is Authentication.Success) {
                rules = FileAPI.cards.listFiles()?.toList() ?: listOf()
            }
        }
        LazyColumn(modifier = modifier) {
            items(sliders) { name ->
                Column(
                    modifier = Modifier
                        .shadow(10.dp)
                        .padding(20.dp)
                ) {
                    Row(modifier = Modifier) {
                        Space()
                        Subtitle(text = name)
                    }
                    rules.forEach { cards ->
                        Space()
                        cards.list()?.toList()?.let { CardsSlider(modifier = Modifier, paths = it) }
                    }
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
            ) {
                var scale by remember { mutableStateOf(1f) }
                var offset by remember { mutableStateOf(Offset.Zero) }
                val backPressedOwner = LocalOnBackPressedDispatcherOwner.current
                Image(
                    painter = this,
                    contentDescription = "Dialog",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = if (scale > 1f) offset.x else 0f,
                            translationY = if (scale > 1f) offset.y else 0f
                        )
                        .pointerInput(Unit) {
                            detectTransformGestures(
                                onGesture = { _, pan: Offset, zoom: Float, _ ->
                                    offset += pan * 2f
                                    scale = (scale * zoom).coerceIn(0.5f, 4f)
                                }
                            )
                            detectTapGestures {
                                backPressedOwner?.onBackPressedDispatcher?.onBackPressed()
                            }
                        }
                )
            }
        }
    }

    @Composable
    fun Friends(modifier: Modifier, list: List<Friend>) {
        var unknowns by remember {
            mutableStateOf(listOf<Player>())
        }
        firebaseViewModel.players().observe(activity) { players ->
            unknowns = players.filter { player -> !list.any { friend -> friend.id == player.id } }
        }
        Column(
            modifier = modifier
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(
                    listOf(
                        resString(R.string.current_friend),
                        resString(R.string.not_yet_friends)
                    )
                ) {
                    if (it == resString(R.string.current_friend))
                        Current(
                            modifier = Modifier
                                .fillParentMaxSize(),
                            list.filter { friends -> friends.added }
                        )
                    else NotYet(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        list.filter { friends -> !friends.added }
                    )
                }
            }
        }
    }

    @Composable
    fun Current(modifier: Modifier, list: List<Friend>) {
        Friend(
            modifier = modifier,
            list = list,
            onTop = {
                FixedButtons(text = "Remove", onClick = { /*TODO*/ })
            },
            onItem = {
                FixedText(text = it.name)
            }
        )
    }

    @Composable
    fun NotYet(modifier: Modifier, list: List<Friend>) {
        Friend(
            modifier = modifier,
            list = list,
            onTop = {
                TextField(
                    value = "Enter the player you are searching for here",
                    onValueChange = {},
                    modifier = Modifier.fillMaxSize()
                )
                FixedButtons(text = "Add", onClick = { /*TODO*/ })
            },
            onItem = {
                FixedText(text = it.name)
            }
        )
    }

    @Composable
    fun Friend(modifier: Modifier, list: List<Friend>, onTop: @Composable () -> Unit, onItem: @Composable (Friend) -> Unit) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var select by remember { mutableStateOf("") }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(32.dp)
            ) {
                onTop()
            }
            LazyRow {
                items(list) {
                    Column(
                        modifier = Modifier.selectable(
                            selected = it.id == select,
                            onClick = { select = it.id })
                    ) {
                        onItem(it)
                    }
                }
            }
        }
    }
}