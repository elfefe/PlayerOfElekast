package com.elfefe.elekast.player.mvvm.api

import com.elfefe.elekast.player.utils.app
import java.io.File

class FileAPI {
    companion object {
        val rules: File
            get() = File("${app.filesDir.absolutePath}${File.separator}rules").apply { mkdirs() }
    }
}