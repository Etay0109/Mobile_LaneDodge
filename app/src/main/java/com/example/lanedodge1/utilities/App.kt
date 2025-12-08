package com.example.lanedodge1

import android.app.Application
import com.example.lanedodge1.utilities.SignalManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        SignalManager.init(this)
    }
}
