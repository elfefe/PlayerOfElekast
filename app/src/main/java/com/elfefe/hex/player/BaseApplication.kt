package com.elfefe.hex.player

import android.app.Application
import com.elfefe.hex.player.mvvm.api.APIRepository
import com.elfefe.hex.player.utils.extensions.crashlytics
import com.elfefe.hex.player.utils.extensions.resString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class BaseApplication: Application() {
    private val scope = CoroutineScope(Dispatchers.Default)
    lateinit var apiRepository: APIRepository

    override fun onCreate() {
        super.onCreate()
        instance = this
        apiRepository = APIRepository(scope)
        crashlytics.checkForUnsentReports()
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            scope.cancel("${resString(R.string.app_name)} as crashed.\n${e.localizedMessage}")
        }
    }
    companion object {
        lateinit var instance: BaseApplication
    }
}