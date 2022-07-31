package com.elfefe.hex.player.ui.theme

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    FixedSurface {
        Text(text = text)
    }

@Composable
fun Title(text: String) =
    FixedSurface {
        Text(
            text = text,
            style = titleStyle
        )
    }

@Composable
fun Subtitle(text: String) =
    FixedSurface {
        Text(
            text = text,
            style = subtitleStyle
        )
    }

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

@Composable
fun FixedIconButton(icon: ImageVector, onClick: () -> Unit) =
    IconButton(
        modifier = Modifier
            .height(64.dp)
            .width(192.dp),
        onClick = onClick,
        colors = IconButtonDefaults
            .iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = icon.name
        )
    }