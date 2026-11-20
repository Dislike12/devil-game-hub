package com.devilgamehub.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devilgamehub.data.model.Game
import com.devilgamehub.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailBottomSheet(
    game: Game?,
    onDismiss: () -> Unit,
    onPlayClick: (Game) -> Unit,
    onFavoriteClick: (Game) -> Unit
) {
    if (game == null) return

    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        scrimColor = Color(0xAA000000),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(game.thumbnailUrl.ifEmpty { "https://images.unsplash.com/photo-1550745165-9bc0b252726f" })
                        .crossfade(true)
                        .build(),
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, SurfaceDark)
                            )
                        )
                )

                // Close Button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color(0xAA08080E), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextPrimary
                    )
                }

                // Favorite Button
                IconButton(
                    onClick = { onFavoriteClick(game) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color(0xAA08080E), CircleShape)
                ) {
                    Icon(
                        imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (game.isFavorite) NeonPink else TextSecondary
                    )
                }
            }

            // Info Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = game.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )

                        Text(
                            text = game.category.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonPurple,
                            letterSpacing = 1.sp
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putchar(Intent.EXTRA_TEXT, "Play ${game.title} on Devil Game Hub! ${game.resolvedGameUrl}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Game"))
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceLightDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = NeonCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceLightDark)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatItem(icon = Icons.Default.Star, value = "${game.rating}", label = "Rating", tint = NeonAmber)
                    StatItem(icon = Icons.Default.VideogameAsset, value = game.type.uppercase(), label = "Type", tint = NeonCyan)
                    StatItem(icon = Icons.Default.ScreenRotation, value = game.orientation.capitalize(), label = "Mode", tint = NeonPurple)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "ABOUT THE GAME",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = game.description.ifEmpty { "Experience seamless HTML5 gameplay directly inside Devil Game Hub with instant full screen response." },
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )

                if (game.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        game.tags.take(4).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceLightDark)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "#$tag",
                                    fontSize = 11.sp,
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Play Now Big Neon Button
                NeonButton(
                    text = "LAUNCH GAME",
                    onClick = {
                        onDismiss()
                        onPlayClick(game)
                    },
                    icon = Icons.Default.PlayArrow,
                    height = 54.dp
                )
            }
        }
    }
}

private fun Intent.putchar(extraText: String, s: String) {
    putExtra(extraText, s)
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Text(text = label, fontSize = 11.sp, color = TextMuted)
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
