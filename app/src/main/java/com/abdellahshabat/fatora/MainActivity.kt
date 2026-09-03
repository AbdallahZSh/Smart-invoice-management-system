package com.abdellahshabat.fatora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.abdellahshabat.fatora.ui.dashboard.DashboardScreen
import com.abdellahshabat.fatora.ui.theme.FatoraTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FatoraTheme {
                DashboardScreen()
            }
        }
    }
}