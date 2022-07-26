package com.elfefe.elekast.player.utils.extensions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.auth.User

val user: FirebaseUser?
    get() = FirebaseAuth.getInstance().currentUser

val crashlytics: FirebaseCrashlytics
    get() = FirebaseCrashlytics.getInstance()