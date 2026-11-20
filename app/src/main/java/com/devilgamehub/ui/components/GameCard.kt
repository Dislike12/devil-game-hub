package com.devilgamehub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devilgamehub.data.model.Game
import com.devilgamehub.ui.theme.*

@Composable
fun GameCard(
    game: Game,
    onPlayClick: (Game) -> Unit,
    onCardClick: (Game) -> Unit,
    onFavoriteClick: (Game) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = if (game.isFeatured) listOf(NeonPurple, NeonCyan) else listOf(SurfaceBorder, SurfaceDark)
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onCardClick(game) },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column {
            // Thumbnail container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(game.thumbnailUrl.ifEmpty { "https://images.unsplash.com/photo-1550745165-9bc0b252726f" })
                        .crossfade(true)
                        .build(),
                    contentDescription = game.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for bottom text contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC08080E))
                            )
                        )
                )

                // Favorite button (Top right)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xAA08080E)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { onFavoriteClick(game) }) {
                        Icon(
                            imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (game.isFavorite) NeonPink else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Rating badge (Top left)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xCC08080E))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = String.format("%.1f", game.rating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                // Category Tag (Bottom left)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonPurple.copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = game.category.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Info section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = game.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = game.description.ifEmpty { "Dynamic HTML5 Web Game" },
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Play Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(listOf(NeonPurple, NeonCyan))
                        )
                        .clickable { onPlayClick(game) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PLAY NOW",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
