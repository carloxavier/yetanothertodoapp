package com.myapplication

import DriverFactory
import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import driverProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        driverProvider = DriverFactory(applicationContext).createDriver()
        setContent {
            MainView()
        }
    }
}