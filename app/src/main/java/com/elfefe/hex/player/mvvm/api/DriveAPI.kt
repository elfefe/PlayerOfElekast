package com.elfefe.hex.player.mvvm.api

import com.elfefe.hex.player.R
import com.elfefe.hex.player.mvvm.model.DriveFile
import com.elfefe.hex.player.utils.app
import com.elfefe.hex.player.utils.extensions.resString
import com.elfefe.hex.player.utils.loge
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.api.services.drive.model.Permission
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class DriveAPI(private val scope: CoroutineScope) {
    private val credential = GoogleCredentials
        .fromStream(app.assets.open(resString(R.string.drive_service_account)))
        .createScoped(listOf(DriveScopes.DRIVE))
        get() {
            field.refreshIfExpired()
            return field
        }
    private val drive: Drive
        get() = Drive
            .Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                HttpCredentialsAdapter(credential)
            )
            .build()

    fun createFolder(
        file: DriveFile,
        parent: String,
        onSuccess: (File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                file.id?.let { id ->
                    readFile(
                        id,
                        onSuccess = { current ->
                            onSuccess(current)
                        },
                        onError = onError
                    )
                } ?: try {
                    val result = drive
                        .files()
                        .create(
                            File()
                                .setName(file.email)
                                .setMimeType(FOLDER_TYPE)
                                .setParents(Collections.singletonList(parent))
                        )
                        .setFields("id, name")
                        .execute()
                    onSuccess(Gson().fromJson(result.toString(), File::class.java))
                } catch (exception: Exception) {
                    onError(exception)
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private fun readFile(id: String, onSuccess: (File) -> Unit, onError: (Exception) -> Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                onSuccess(drive.files().get(id).execute())
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun readFiles(
        parent: String,
        onSuccess: (FileList) -> Unit,
        onError: (Exception) -> Unit
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                var pageToken: String? = null
                do {
                    val fileList = drive.files().list()
                        .setQ("'$parent' in parents and mimeType != '$FOLDER_TYPE'")
                        .setFields("files(id, name)")
                        .setPageToken(pageToken)
                        .execute()

                    fileList.nextPageToken?.let {
                        pageToken = it
                    } ?: onSuccess(fileList)
                } while (pageToken != null)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    public fun shareFile(
        folderId: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userPermission = Permission()
            .setType("user")
            .setRole("writer")
            .apply { emailAddress = email }

        try {
            if (drive.permissions().get(folderId, userPermission.id).runCatching {
                    execute()
            }.also {
               this@DriveAPI.loge = "Sharing ${it.getOrNull()?.keys?.toList()}"
            }.isFailure) {
                drive.permissions().create(folderId, userPermission)
                    .setFields("id")
                    .execute()
                onSuccess()
            }
        } catch (e: GoogleJsonResponseException) {
            onError(e)
        }
    }

    companion object {
        const val HEX_ID = "1yMsnJM8WelgOG39ihaanEkpe-7KEAYq2"
        const val FOLDER_TYPE = "application/vnd.google-apps.folder"
    }
}