package com.abdellahshabat.fatora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.abdellahshabat.fatora.di.AppContainer
import com.abdellahshabat.fatora.navigation.FatoraNavGraph
import com.abdellahshabat.fatora.ui.theme.FatoraTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FatoraTheme {
                val appContainer = remember { AppContainer(applicationContext) }

                FatoraNavGraph(appContainer = appContainer)
            }
        }
    }
}