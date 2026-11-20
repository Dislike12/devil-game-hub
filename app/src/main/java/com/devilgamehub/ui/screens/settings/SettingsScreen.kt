package com.devilgamehub.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devilgamehub.data.model.UserPreferences
import com.devilgamehub.ui.theme.*
import com.devilgamehub.util.AppAudioManager
import com.devilgamehub.util.AppLanguageManager

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val view = LocalView.current
    val userPrefs by viewModel.userPreferences.collectAsState()

    var languageExpanded by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    val lang = userPrefs.language

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Title
            Text(
                text = AppLanguageManager.getString("settings_title", lang),
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // 1. SOUND SETTINGS CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardHeader(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        title = AppLanguageManager.getString("sound_settings", lang),
                        tint = NeonCyan
                    )

                    SettingSwitchRow(
                        title = "Sound Effects (SFX)",
                        subtitle = "Enable in-game button taps and audio feedback",
                        checked = userPrefs.soundEnabled,
                        onCheckedChange = {
                            AppAudioManager.playTapSound(view, userPrefs)
                            AppAudioManager.performHaptic(view, userPrefs)
                            viewModel.setSoundEnabled(it)
                        }
                    )

                    HorizontalDivider(color = SurfaceBorder)

                    SettingSwitchRow(
                        title = "In-Game Audio",
                        subtitle = "Enable HTML5 game music and sound effects",
                        checked = userPrefs.bgMusicEnabled,
                        onCheckedChange = {
                            AppAudioManager.playTapSound(view, userPrefs)
                            AppAudioManager.performHaptic(view, userPrefs)
                            viewModel.setBgMusicEnabled(it)
                        }
                    )
                }
            }

            // 2. LANGUAGE PREFERENCES CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardHeader(
                        icon = Icons.Default.Language,
                        title = AppLanguageManager.getString("language_settings", lang),
                        tint = NeonPurple
                    )

                    Text(
                        text = "Select your preferred display language for Devil Game Hub.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceLightDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                AppAudioManager.playTapSound(view, userPrefs)
                                languageExpanded = !languageExpanded
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = userPrefs.language,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Icon(
                                imageVector = if (languageExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = NeonPurple
                            )
                        }

                        DropdownMenu(
                            expanded = languageExpanded,
                            onDismissRequest = { languageExpanded = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            UserPreferences.LANGUAGES.forEach { selectedLang ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = selectedLang,
                                            color = if (selectedLang == userPrefs.language) NeonCyan else TextPrimary,
                                            fontWeight = if (selectedLang == userPrefs.language) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        AppAudioManager.playTapSound(view, userPrefs)
                                        viewModel.setLanguage(selectedLang)
                                        languageExpanded = false
                                        Toast.makeText(context, "Language set to $selectedLang", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 3. GAMEPLAY PREFERENCES CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardHeader(
                        icon = Icons.Default.SportsEsports,
                        title = AppLanguageManager.getString("gameplay_settings", lang),
                        tint = NeonAmber
                    )

                    SettingSwitchRow(
                        title = "Auto-Rotate Orientation",
                        subtitle = "Automatically adjust screen mode based on game metadata",
                        checked = userPrefs.autoRotateEnabled,
                        onCheckedChange = {
                            AppAudioManager.playTapSound(view, userPrefs)
                            AppAudioManager.performHaptic(view, userPrefs)
                            viewModel.setAutoRotateEnabled(it)
                        }
                    )

                    HorizontalDivider(color = SurfaceBorder)

                    SettingSwitchRow(
                        title = "Haptic Feedback",
                        subtitle = "Vibrate device on touch gestures and tile moves",
                        checked = userPrefs.hapticFeedbackEnabled,
                        onCheckedChange = {
                            AppAudioManager.playTapSound(view, userPrefs)
                            AppAudioManager.performHaptic(view, userPrefs)
                            viewModel.setHapticFeedbackEnabled(it)
                        }
                    )

                    HorizontalDivider(color = SurfaceBorder)

                    SettingSwitchRow(
                        title = "Fast Loading Mode",
                        subtitle = "Optimize WebView caching for faster game launch",
                        checked = userPrefs.fastLoadingEnabled,
                        onCheckedChange = {
                            AppAudioManager.playTapSound(view, userPrefs)
                            AppAudioManager.performHaptic(view, userPrefs)
                            viewModel.setFastLoadingEnabled(it)
                        }
                    )
                }
            }

            // 4. APP THEME SETTINGS CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardHeader(
                        icon = Icons.Default.Palette,
                        title = AppLanguageManager.getString("theme_settings", lang),
                        tint = NeonPink
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Dark Cyber Theme",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Void black gaming interface with glowing accents",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Text(
                            text = "ACTIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGreen,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x2200FF66))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Accent Theme Color",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        UserPreferences.ACCENT_THEMES.forEach { theme ->
                            val isSelected = theme == userPrefs.accentTheme
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) SurfaceLightDark else VoidBlack)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) NeonCyan else SurfaceBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        AppAudioManager.playTapSound(view, userPrefs)
                                        AppAudioManager.performHaptic(view, userPrefs)
                                        viewModel.setAccentTheme(theme)
                                        Toast.makeText(context, "Theme set to $theme", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = theme,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) TextPrimary else TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // 5. LOCAL STORAGE & HISTORY CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CardHeader(
                        icon = Icons.Default.Storage,
                        title = "Local Storage & History",
                        tint = NeonRed
                    )

                    Text(
                        text = "Clear saved play history and recent game records from your device.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedButton(
                        onClick = {
                            AppAudioManager.playTapSound(view, userPrefs)
                            showClearHistoryDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = NeonRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppLanguageManager.getString("clear_history", lang),
                            fontSize = 12.sp,
                            color = NeonRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // APP INFO FOOTER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Devil Game Hub v1.0",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    Text(
                        text = "Production Quality Android Gaming Platform",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Confirmation Dialog for Clearing Play History
        if (showClearHistoryDialog) {
            AlertDialog(
                onDismissRequest = { showClearHistoryDialog = false },
                title = {
                    Text(
                        text = "Clear Play History?",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to clear your recently played history? Saved favorites and game files will not be affected.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearHistoryDialog = false
                            viewModel.clearHistory()
                            Toast.makeText(context, "Play history cleared successfully!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Clear History", color = NeonRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showClearHistoryDialog = false }
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = SurfaceDark
            )
        }
    }
}

@Composable
private fun CardHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VoidBlack,
                checkedTrackColor = NeonCyan,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SurfaceLightDark
            )
        )
    }
}
