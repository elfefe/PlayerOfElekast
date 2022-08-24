package com.elfefe.hex.player.mvvm.api

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.ActivityResult
import com.elfefe.hex.player.mvvm.model.DriveFile
import com.elfefe.hex.player.mvvm.model.Friend
import com.elfefe.hex.player.mvvm.model.Player
import com.elfefe.hex.player.ui.StartActivity
import com.elfefe.hex.player.utils.*
import com.elfefe.hex.player.utils.extensions.friend
import com.elfefe.hex.player.utils.extensions.player
import com.elfefe.hex.player.utils.extensions.user
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File

class FirebaseAPI(private val scope: CoroutineScope) {
    private val drive = DriveAPI(scope)

    private val firebase: FirebaseApp = FirebaseApp.initializeApp(app) ?: FirebaseApp.getInstance()
    private val appCheck = FirebaseAppCheck.getInstance(firebase)
        .apply {
            installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance()
            )
        }
    private val firestore = FirebaseFirestore.getInstance()
    private val gamers: CollectionReference
        get() = firestore.collection("gamers")
    private val DocumentReference.friends: CollectionReference
        get() = collection("Friends")
    private val storage = FirebaseStorage.getInstance(firebase, "gs://playerofelekast.appspot.com")

    private val _authenticationFlow: MutableStateFlow<Authentication> =
        MutableStateFlow(Authentication.Pending())
    val authenticationFlow: StateFlow<Authentication>
        get() = _authenticationFlow

    private fun rules(onDownload: (Int, Int) -> Unit = { _, _ -> }, onFinish: () -> Unit = {}) {
        storage.reference.child("game/hex/rules").listAll().addOnCompleteListener {
            if (it.isSuccessful) {
                download(it.result.items, { progress ->
                    onDownload(progress, it.result.items.size)
                }, onFinish)
            } else throw RuntimeException("Files not in storage.")
        }
    }

    private fun download(
        references: List<StorageReference>,
        onDownload: (Int) -> Unit,
        onFinish: () -> Unit = {}
    ) {
        scope.launch(Dispatchers.IO) {
            val reference = references.firstOrNull()
            reference ?: run {
                onFinish()
                return@launch
            }
            reference.getFile(
                File(FileAPI.cards, reference.name).apply { createNewFile() }
            ).addOnCompleteListener {
                if (it.isSuccessful)
                    references.filter { left -> left != reference }.let { leftReferencies ->
                        onDownload(leftReferencies.size)
                        if (leftReferencies.isEmpty()) onFinish()
                        else download(leftReferencies, onDownload, onFinish)
                    }
                else this@FirebaseAPI.loge = "Download issue ${it.exception}"
            }
        }
    }

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
//            drive()
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

    fun authenticate(result: ActivityResult, client: SignInClient): StateFlow<Authentication> =
        _authenticationFlow.apply {
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credentials =
                        client.getSignInCredentialFromIntent(result.data)
                    credentials.googleIdToken?.let {
                        loge = "Credential $it"
                        credential(it) { exception ->
                            value = Authentication.Failure(exception)
                        }
                    } ?: throw ApiException(
                        Status(
                            17,
                            "Google credentials not found"
                        )
                    )
                } catch (it: ApiException) {
                    value = Authentication.Failure(it)
                }
            }
        }

    private fun MutableStateFlow<Authentication>.credential(
        credentials: String,
        onFailure: (java.lang.Exception?) -> Unit
    ) {
        val googleCredentials = GoogleAuthProvider.getCredential(credentials, null)
        FirebaseAuth.getInstance().signInWithCredential(googleCredentials)
            .addOnCompleteListener { auth ->
                if (auth.isSuccessful) {
                    player?.let { player ->
                        gamers.document(player.id).updatePlayer(player)
                        rules({ progress, max ->
                            value = Authentication.Pending(max - progress, max)
                        }) { value = Authentication.Success() }
                    } ?: onFailure(Exception("Missing user informations"))
                } else onFailure(auth.exception)
            }
    }

    private fun DocumentReference.setPlayer(
        player: Player,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        set(
            mutableMapOf(
                "name" to player.name,
                "email" to player.email,
                "visible" to player.isVisible
            ).apply {
                player.folder?.let { put("id", player.id) }
            }
        ).addOnCompleteListener {
            if (it.isSuccessful) onSuccess() else onError(
                it.exception ?: Exception("Could not create user")
            )
        }
    }

    private fun DocumentReference.updatePlayer(player: Player) {
        setPlayer(
            player = player,
            onSuccess = {
                get().addOnCompleteListener { doc ->
                    if (doc.isSuccessful)
                        updatePlayerFolder(player.apply { folder = doc.result["id"]?.toString() })
                    else loge = doc.exception?.localizedMessage
                        ?: "Error while querying id"
                }
            },
            onError = {
                loge = it.localizedMessage
                    ?: "Error while querying id"
            }
        )
    }

    private fun DocumentReference.updatePlayerFolder(player: Player) {
        drive.createFolder(
            DriveFile(
                player.email,
                player.folder
            ),
            DriveAPI.HEX_ID,
            onSuccess = { file ->
                setPlayer(player.apply { folder = file.id })
            },
            onError = { error ->
                loge = "Error while creating file. ${error.localizedMessage ?: "unkown"}"
            }
        )
    }

    fun friends(): StateFlow<List<Friend>> = MutableStateFlow<List<Friend>>(listOf()).apply {
        user?.uid?.let { id ->
            gamers.document(id).friends.addSnapshotListener { result, _ ->
                result?.documents?.map { doc ->
                    doc.friend
                }?.let { list -> value = list }
            }
        }
    }

    fun players(): StateFlow<List<Player>> = MutableStateFlow<List<Player>>(listOf()).apply {
        gamers.get().addOnCompleteListener {
            if (it.isSuccessful) value = it.result.documents.map { doc -> doc.player }
            else loge = "Error querying gamers. ${it.exception?.localizedMessage ?: "unkown"}"
        }
    }

    fun askGameMaster(onSuccess: () -> Unit, onPending: () -> Unit) {
        user?.email?.let { email ->
            drive.readFiles(
                email,
                onSuccess = {
                    loge = "askGameMaster ${it.files.map { file -> file.name }.toList()}"
                },
                onError = {
                    loge = "askGameMaster ${it.localizedMessage}"
                }
            )
        }
    }
}