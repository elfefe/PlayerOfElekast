package com.elfefe.elekast.player.utils

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult

sealed class Connection {
    class Success(val result: String) : Connection()
    class Failure(val error: Exception) : Connection()
    class Loading() : Connection()
}

sealed class GoogleOneTap: Connection() {
    class Success(val result: BeginSignInResult) : GoogleOneTap()
    class Failure(val error: Exception) : GoogleOneTap()
    class Loading() : GoogleOneTap()
}