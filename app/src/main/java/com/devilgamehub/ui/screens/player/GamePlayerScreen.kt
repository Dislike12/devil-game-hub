package com.devilgamehub.ui.screens.player

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.devilgamehub.ui.components.NeonButton
import com.devilgamehub.ui.theme.*

@Composable
fun GamePlayerScreen(
    gameId: String,
    viewModel: GamePlayerViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val activity = context as? Activity

    val userPrefs by viewModel.userPreferences.collectAsState()
    val currentGame by viewModel.currentGame.collectAsState()
    val loadProgress by viewModel.loadProgress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasError by viewModel.hasError.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val requestedOrientation by viewModel.requestedOrientation.collectAsState()

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isOverlayVisible by remember { mutableStateOf(true) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    val isMuted = userPrefs.isGameAudioMuted(gameId)

    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)
    }

    // Handle Orientation requested by ViewModel
    DisposableEffect(requestedOrientation) {
        activity?.requestedOrientation = requestedOrientation
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Hide status bars & navigation bars for immersive full screen
    DisposableEffect(view) {
        val window = activity?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, view)
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        }
        onDispose {
            val window = activity?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Custom Back Handler
    BackHandler {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else {
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .clickable { isOverlayVisible = !isOverlayVisible }
    ) {
        currentGame?.let { game ->
            // WebView Container
            CustomWebView(
                url = game.resolvedGameUrl,
                isMuted = isMuted,
                fastLoadingEnabled = userPrefs.fastLoadingEnabled,
                onProgressChanged = { progress -> viewModel.updateProgress(progress) },
                onErrorReceived = { err -> viewModel.setError(err) },
                onWebViewCreated = { webViewInstance = it }
            )

            // Top Progress Bar
            if (isLoading && loadProgress < 100) {
                LinearProgressIndicator(
                    progress = { loadProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.TopCenter),
                    color = NeonCyan,
                    trackColor = Color.Transparent,
                )
            }

            // Minimal Clean Top Overlay Bar
            AnimatedVisibility(
                visible = isOverlayVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xEE08080E))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button & Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = game.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isLoading) "Loading ${loadProgress}%" else "${game.category.uppercase()} • HTML5",
                                fontSize = 10.sp,
                                color = if (isLoading) NeonCyan else NeonPurple,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Minimal Clean Overflow Menu (⋮)
                    Box {
                        IconButton(
                            onClick = { showOverflowMenu = !showOverflowMenu },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = TextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                            modifier = Modifier.background(SurfaceDark)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = if (isMuted) "Unmute Audio" else "Mute Audio",
                                        color = TextPrimary,
                                        fontSize = 13.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = if (isMuted) NeonRed else NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.toggleGameMute(game.id)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Refresh Game", color = TextPrimary, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.clearError()
                                    webViewInstance?.reload()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Rotate Screen", color = TextPrimary, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ScreenRotation,
                                        contentDescription = null,
                                        tint = NeonPurple,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.toggleOrientation()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Share Game", color = TextPrimary, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "Playing ${game.title} on Devil Game Hub: ${game.resolvedGameUrl}")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Game"))
                                }
                            )
                        }
                    }
                }
            }

            // Error Overlay if offline / web error occurs
            if (hasError) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(VoidBlack.copy(alpha = 0.95f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = NeonRed,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "GAME LOAD ERROR",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        Text(
                            text = errorMessage.ifEmpty { "Unable to connect to game server. Please check your internet connection." },
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("EXIT", color = TextPrimary)
                            }

                            NeonButton(
                                text = "RETRY",
                                onClick = {
                                    viewModel.clearError()
                                    webViewInstance?.reload()
                                },
                                modifier = Modifier.weight(1f),
                                height = 44.dp
                            )
                        }
                    }
                }
            }
        }
    }
}
