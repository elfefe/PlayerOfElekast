package com.elfefe.elekast.player.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.elfefe.elekast.player.mvvm.FirebaseViewModel
import com.elfefe.elekast.player.ui.component.*
import com.elfefe.elekast.player.ui.theme.PlayerOfElekastTheme
import com.elfefe.elekast.player.utils.*
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class StartActivity : ComponentActivity() {
    private lateinit var controller: NavHostController

    private val navigating = AtomicBoolean(false)

    lateinit var oneTapClient: SignInClient
    lateinit var signInRequest: BeginSignInRequest
    lateinit var signUpRequest: BeginSignInRequest

    private val firebaseViewModel by viewModels<FirebaseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = signRequest()
        signUpRequest = signRequest()

        setContent {
            controller = rememberNavController()
            PlayerOfElekastTheme {
                NavHost(
                    navController = controller,
                    startDestination = START,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    StartComponent(this@StartActivity).run { compose() }
                    GamerComponent(this@StartActivity).run { compose() }
                    GameComponent(this@StartActivity).run { compose() }
                    MainComponent(this@StartActivity).run { compose() }
                }
                Loading()
            }
        }
    }

    @Composable
    private fun Loading() {
        var isLoading by remember { mutableStateOf(false) }
        var loadingValue by remember { mutableStateOf(0f) }
        val loadingHeight by animateDpAsState(targetValue = if (isLoading) 2.dp else 0.dp)

        firebaseViewModel.authentication.observe(this) {
            when (it) {
                is Authentication.Pending -> {
                    isLoading = true
                    loadingValue =  it.progress.toFloat() / it.max.toFloat()
                }
                else -> {
                    loadingValue = 0f
                    isLoading = false
                }
            }
        }

        Canvas(
            Modifier
                .fillMaxWidth()
                .height(loadingHeight)
        ) {
            drawLine(Color.Magenta, Offset.Zero, Offset(size.width * loadingValue, 0f), 10f)
        }
    }

    fun navigateTo(destination: String, args: String = "", delay: Number = 0) {
        if (!navigating.getAndSet(true))
            lifecycleScope.launch(Dispatchers.Default) {
                delay(BaseComponent.currentComponent?.state?.let { state ->
                    state(false)
                } ?: 0L)
                delay(delay.toLong())
                onMain { controller.navigate("${destination}${if (args.isNotEmpty()) "/$args" else ""}") }
                navigating.set(false)
            }
    }

    private fun signRequest() = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId("221223133518-vsq6fde3secf902oedi0g76n1uqetc6o.apps.googleusercontent.com")
                .setFilterByAuthorizedAccounts(false)
                .build()
        )
        .build()
}
