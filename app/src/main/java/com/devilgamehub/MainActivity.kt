package com.devilgamehub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.devilgamehub.data.local.SettingsRepository
import com.devilgamehub.ui.navigation.BottomNavBar
import com.devilgamehub.ui.navigation.NavGraph
import com.devilgamehub.ui.theme.DevilGameHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsRepo = SettingsRepository.getInstance(applicationContext)
            val userPrefs by settingsRepo.userPreferences.collectAsState()

            DevilGameHubTheme(accentTheme = userPrefs.accentTheme) {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavGraph(
                            navController = navController,
                            modifier = Modifier.fillMaxSize()
                        )

                        BottomNavBar(
                            navController = navController,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}
