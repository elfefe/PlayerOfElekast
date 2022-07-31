package com.elfefe.hex.player.mvvm

import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfefe.hex.player.ui.StartActivity
import com.elfefe.hex.player.utils.Authentication
import com.elfefe.hex.player.utils.GoogleOneTap
import com.elfefe.hex.player.utils.app
import com.google.android.gms.auth.api.identity.SignInClient
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FirebaseViewModel: ViewModel() {
    private val repository = app.apiRepository

    private val _googleSignIn = MutableLiveData<GoogleOneTap>()
    private val _authentication = MutableLiveData<Authentication>()
    val authentication: LiveData<Authentication>
        get() = _authentication

    fun StartActivity.googleSign(): LiveData<GoogleOneTap> = _googleSignIn.apply {
        repository.run {
            googleSignIn
                .onEach {
                    postValue(it)
                }
                .launchIn(viewModelScope)
        }
    }

    fun authentication(result: ActivityResult, client: SignInClient): LiveData<Authentication> = _authentication.apply {
        repository
            .firebaseAuthentication(result, client)
            .onEach {
                postValue(it)
            }
            .launchIn(viewModelScope)
    }
}