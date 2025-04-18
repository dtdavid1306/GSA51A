package com.golfapp.gsa51

import android.app.Application
import com.golfapp.gsa51.data.GSA51Database
import com.golfapp.gsa51.repositories.GolfRepository

class GSA51Application : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}

interface AppContainer {
    val golfRepository: GolfRepository
}

class AppContainerImpl(private val application: Application) : AppContainer {

    override val golfRepository: GolfRepository by lazy {
        GolfRepository(GSA51Database.getDatabase(application))
    }
}