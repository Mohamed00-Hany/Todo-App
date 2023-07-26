package com.projects.todoapp.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        val nightMode = sharedPreferences.getInt("NightModeInt", 1);
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
}