package com.myapplication

import DriverFactory
import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import driver

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        driver = DriverFactory(applicationContext).createDriver()
        setContent {
            MainView()
        }
    }
}