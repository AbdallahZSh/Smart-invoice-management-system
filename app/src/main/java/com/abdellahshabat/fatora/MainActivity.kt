package com.abdellahshabat.fatora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.abdellahshabat.fatora.di.AppContainer
import com.abdellahshabat.fatora.navigation.FatoraNavGraph
import com.abdellahshabat.fatora.ui.theme.FatoraTheme
import com.abdellahshabat.fatora.util.ShopPreferences
import com.abdellahshabat.fatora.util.ThemeMode

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val shopPreferences = remember { ShopPreferences(applicationContext) }
            var themeMode by remember { mutableStateOf(shopPreferences.getThemeMode()) }

            val isDark = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            FatoraTheme(darkTheme = isDark) {
                val appContainer = remember { AppContainer(applicationContext) }

                FatoraNavGraph(
                    appContainer = appContainer,
                    themeMode = themeMode,
                    onThemeModeChange = { mode ->
                        themeMode = mode
                        shopPreferences.saveThemeMode(mode)
                    }
                )
            }
        }
    }
}