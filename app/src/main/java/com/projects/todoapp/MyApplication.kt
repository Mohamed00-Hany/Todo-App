package com.projects.todoapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        val NightMode = sharedPreferences.getInt("NightModeInt", 1);
        AppCompatDelegate.setDefaultNightMode(NightMode);
    }
}