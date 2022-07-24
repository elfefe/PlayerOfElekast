package com.elfefe.elekast.player.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfefe.elekast.player.ui.StartActivity
import com.elfefe.elekast.player.utils.GoogleOneTap
import com.elfefe.elekast.player.utils.app
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FirebaseViewModel: ViewModel() {
    private val repository = app.apiRepository

    private val _googleSignIn = MutableLiveData<GoogleOneTap>()

    fun StartActivity.googleSign(): LiveData<GoogleOneTap> = _googleSignIn.apply {
        repository.run {
            googleSignIn
                .onEach {
                    postValue(it)
                }
                .launchIn(viewModelScope)
        }
    }
}