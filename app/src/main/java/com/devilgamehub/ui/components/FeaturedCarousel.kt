package com.devilgamehub.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.delay

@Composable
fun FeaturedCarousel(
    featuredGames: List<Game>,
    onPlayClick: (Game) -> Unit,
    onCardClick: (Game) -> Unit,
    modifier: Modifier = Modifier
) {
    if (featuredGames.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { featuredGames.size })

    // Auto scroll timer
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            if (featuredGames.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % featuredGames.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
        ) { page ->
            val game = featuredGames[page]

            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(listOf(NeonPurple, NeonCyan)),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .clickable { onCardClick(game) },
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Background Image
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(game.thumbnailUrl.ifEmpty { "https://images.unsplash.com/photo-1550745165-9bc0b252726f" })
                            .crossfade(true)
                            .build(),
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cyber Overlay Gradients
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x33000000),
                                        Color(0xEE08080E)
                                    )
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xCC08080E),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Content Overlay
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                            .fillMaxWidth(0.85f)
                    ) {
                        // Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonPurple)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Whatshot,
                                        contentDescription = null,
                                        tint = TextPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "FEATURED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xAA000000))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = NeonAmber,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${game.rating}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = game.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = game.description,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Floating Play Button (Bottom Right)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(NeonPurple, NeonCyan))
                            )
                            .clickable { onPlayClick(game) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Now",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Pager Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(featuredGames.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(if (isSelected) 24.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) NeonCyan else SurfaceBorder)
                )
            }
        }
    }
}
