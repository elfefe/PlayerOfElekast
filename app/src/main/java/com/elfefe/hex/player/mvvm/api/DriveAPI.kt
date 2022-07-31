package com.elfefe.hex.player.mvvm.api

import com.elfefe.hex.player.mvvm.model.DriveFile
import com.elfefe.hex.player.utils.app
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DriveAPI(private val scope: CoroutineScope) {
    private val credential = GoogleCredentials
        .fromStream(app.assets.open("playerofelekast-b623f641bc42.json"))
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
                            loge = "READ $current"
                        },
                        onError = { exception ->
                            loge = "READ $exception"
                        }
                    )
                } ?: onError(Exception("Id doesn't exist"))
//                drive.files().create(
//                    File()
//                        .setName(email)
//                        .setMimeType(FOLDER_TYPE)
//                        .setParents(Collections.singletonList(parent))
//                )
//                    .setFields("id")
//                    .execute()
//                    .also { file ->
//                        shareFile(
//                            file.id,
//                            email,
//                            onSuccess = { onSuccess(file) },
//                            onError = { onError(it) }
//                        )
//                    }
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

    private fun readFiles(parent: String, onSuccess: (FileList) -> Unit, onError: (Exception) -> Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                var pageToken: String? = null
                do {
                    val fileList = drive.files().list()
                        .setQ("'$parent' in parents")
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

    private fun shareFile(
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
            drive.permissions().create(folderId, userPermission)
                .setFields("id")
                .execute()
            onSuccess()
        } catch (e: GoogleJsonResponseException) {
            onError(e)
        }
    }

    companion object {
        const val HEX_ID = "1yMsnJM8WelgOG39ihaanEkpe-7KEAYq2"
        const val FOLDER_TYPE = "application/vnd.google-apps.folder"
    }
}