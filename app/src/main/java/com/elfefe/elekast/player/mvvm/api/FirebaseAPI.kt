package com.elfefe.elekast.player.mvvm.api

import android.content.IntentSender
import com.elfefe.elekast.player.ui.StartActivity
import com.elfefe.elekast.player.utils.Connection
import com.elfefe.elekast.player.utils.GoogleOneTap
import com.elfefe.elekast.player.utils.app
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FirebaseAPI(private val scope: CoroutineScope) {
    val firebase = FirebaseApp.initializeApp(app)

    fun StartActivity.signIn(): StateFlow<GoogleOneTap> =
        MutableStateFlow<GoogleOneTap>(GoogleOneTap.Loading()).apply {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this@signIn) { result ->
                    value = try {
                        GoogleOneTap.Success(result)
                    } catch (e: IntentSender.SendIntentException) {
                        GoogleOneTap.Failure(e)
                    }
                }
                .addOnFailureListener(this@signIn) {
                    signUp()
                        .onEach { value = it }
                        .launchIn(scope)
                }
        }

    private fun StartActivity.signUp(): StateFlow<GoogleOneTap> =
        MutableStateFlow<GoogleOneTap>(GoogleOneTap.Loading()).apply {
            oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this@signUp) { result ->
                    value = try {
                        GoogleOneTap.Success(result)
                    } catch (e: IntentSender.SendIntentException) {
                        GoogleOneTap.Failure(e)
                    }
                }
                .addOnFailureListener(this@signUp) { e ->
                    value = GoogleOneTap.Failure(e)
                }
        }
}