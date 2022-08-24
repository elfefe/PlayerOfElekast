package com.elfefe.hex.player.utils.extensions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

open class ViewModelExtension: ViewModel() {
    fun <T> StateFlow<T>.toLivedata() = MutableLiveData<T>().apply {
        onEach { postValue(it) }
        launchIn(viewModelScope)
    }
}