package com.elfefe.elekast.player.ui.component

import android.app.Activity.RESULT_OK
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.elfefe.elekast.player.mvvm.FirebaseViewModel
import com.elfefe.elekast.player.ui.StartActivity
import com.elfefe.elekast.player.ui.theme.IntroButton
import com.elfefe.elekast.player.ui.theme.Subtitle
import com.elfefe.elekast.player.ui.theme.Title
import com.elfefe.elekast.player.utils.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider.getCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class StartComponent(override val activity: StartActivity) : BaseComponent(activity) {
    override val destination = START

    val firebaseViewModel by activity.viewModels<FirebaseViewModel>()

    override fun NavGraphBuilder.compose() =
        composable(destination) {
            Animated(exit = TRANSITION) {
                Start()
            }
        }

    @Composable
    fun Start() {
        setComponent()

        Column(
            modifier = Modifier
                .fillMaxWidth(.8f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var isClickable = true
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                    if (result.resultCode == RESULT_OK) {
                        try {
                            val credentials =
                                activity.oneTapClient.getSignInCredentialFromIntent(result.data)
                            val googleIdToken = credentials.googleIdToken
                            val googleCredentials = getCredential(googleIdToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(googleCredentials)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        activity.navigateTo(GAMER)
                                    } else {
                                        this@StartComponent.loge =
                                            "Could not connect to your google account.\n${it.exception?.localizedMessage}"
                                        isClickable = true
                                    }
                                }
//                                viewModel.signInWithGoogle(googleCredentials)
                        } catch (it: ApiException) {
                            print(it)
                        }
                    }
                }
            Column(
                modifier = Modifier
                    .fillMaxHeight(.6f)
                    .fillMaxWidth(.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Subtitle(text = "Welcome in the realms of Elekast !\nPlease authenticate your self.\nI need to know a little more about you..")
            }
            var showButton by remember { mutableStateOf(false) }
            LaunchedEffect(null) {
                delay(TRANSITION.toLong())
                showButton = true
            }

            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(transition(TRANSITION))
            ) {
                IntroButton(text = "Authenticate") {
                    if (isClickable)
                        firebaseViewModel.run {
                            isClickable = false
                            activity.googleSign().observe(activity) { oneTap ->
                                when (oneTap) {
                                    is GoogleOneTap.Success -> {
                                        this@StartComponent.logd =
                                            "You are successfully connected with your google account."
                                        val intent =
                                            IntentSenderRequest.Builder(oneTap.result.pendingIntent.intentSender)
                                                .build()
                                        launcher.launch(intent)
                                    }
                                    is GoogleOneTap.Failure -> {
                                        this@StartComponent.loge =
                                            "Could not connect to your google account.\n${oneTap.error.localizedMessage}"
                                        isClickable = true
                                    }
                                    else -> {
                                        this@StartComponent.logw =
                                            "We are trying to connect your google account."
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}