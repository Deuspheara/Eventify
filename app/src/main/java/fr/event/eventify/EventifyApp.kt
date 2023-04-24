package fr.event.eventify

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class EventifyApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}