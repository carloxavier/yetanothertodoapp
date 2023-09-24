package com.myapplication

import MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import common.di.DriverFactory
import common.di.DependencyProvider.driver

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        driver = DriverFactory(applicationContext).createDriver()
        setContent {
            MainView()
        }
    }
}