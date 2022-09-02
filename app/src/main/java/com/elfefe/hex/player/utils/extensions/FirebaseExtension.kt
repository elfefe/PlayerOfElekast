package com.elfefe.hex.player.utils.extensions

import com.elfefe.hex.player.mvvm.model.Friend
import com.elfefe.hex.player.mvvm.model.Player
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot

val user: FirebaseUser?
    get() = FirebaseAuth.getInstance().currentUser

val crashlytics: FirebaseCrashlytics
    get() = FirebaseCrashlytics.getInstance()

val player: Player?
    get() = user?.uid?.let { id ->
        user?.displayName?.let { name ->
            user?.email?.let { email ->
                Player(id, name, email, true, null)
            }
        }
    }

val DocumentSnapshot.friend: Friend
    get() = Friend(
        id = this["id"].toString(),
        name = this["name"].toString(),
        added = this["added"] as Boolean
    )

val DocumentSnapshot.player: Player?
    get() = (this["email"] as String?)?.let { email ->
        Player(
            id = id,
            name = this["email"]?.toString() ?: "",
            email = email,
            visible = this["visible"] as Boolean? ?: false,
            folder = this["folder"]?.toString() ?: ""
        )
    }