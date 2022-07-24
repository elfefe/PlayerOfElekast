package com.elfefe.elekast.player.ui.theme

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FixedSurface(modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) = Surface(
    color = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground
) {
    content()
}

@Composable
fun FixedText(text: String) =
    Text(text = text)

@Composable
fun Title(text: String) =
    Text(
        text = text,
        style = titleStyle
    )

@Composable
fun Subtitle(text: String) =
    Text(
        text = text,
        style = subtitleStyle
    )

@Composable
fun FixedButtons(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) = Button(
    modifier = modifier,
    onClick = onClick,
    colors = ButtonDefaults
        .buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
    shape = MaterialTheme.shapes.small
) {
    Text(text = text, color = MaterialTheme.colorScheme.onPrimary, style = buttonStyle)
}

@Composable
fun IntroButton(text: String, onClick: () -> Unit) = FixedButtons(
    text = text,
    onClick = onClick,
    modifier = Modifier
        .height(64.dp)
        .width(192.dp)
)