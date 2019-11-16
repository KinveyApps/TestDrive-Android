package com.kinvey.sample.testdrive

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.kinvey.android.Client
import com.kinvey.android.Client.Builder
import com.kinvey.android.model.User


class App: Application() {

    companion object {
        var instance: App? = null
    }

    var kinveyClient: Client<User>? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        kinveyClient = Builder<User>(this).build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
}
