package com.devilgamehub.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devilgamehub.data.model.Categories
import com.devilgamehub.data.model.Category
import com.devilgamehub.data.model.Game
import com.devilgamehub.data.repository.GamesState
import com.devilgamehub.ui.components.*
import com.devilgamehub.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPlayGame: (Game) -> Unit,
    onNavigateToLibrary: (Category?) -> Unit,
    onNavigateToFavorites: () -> Unit = {}
) {
    val state by viewModel.gamesState.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val featuredGames by viewModel.featuredGames.collectAsState()
    val popularGames by viewModel.popularGames.collectAsState()
    val recentlyPlayed by viewModel.recentlyPlayedGames.collectAsState()

    var selectedDetailGame by remember { mutableStateOf<Game?>(null) }

    val isOffline = !isOnline

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        when (state) {
            is GamesState.Loading -> {
                LoadingScreen(message = "Loading Games from Remote GitHub DB...")
            }
            is GamesState.Error -> {
                val errorMsg = (state as GamesState.Error).message
                ErrorMessage(message = errorMsg, onRetry = { viewModel.refreshGames() })
            }
            is GamesState.Success -> {
                val allGames = (state as GamesState.Success).games

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Glass Header
                    item(span = { GridItemSpan(2) }) {
                        GlassHeader(
                            isOffline = isOffline,
                            subtitle = "Dynamic Game Platform"
                        )
                    }

                    // Featured Carousel
                    if (featuredGames.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            FeaturedCarousel(
                                featuredGames = featuredGames,
                                onPlayClick = { game ->
                                    viewModel.recordGamePlay(game)
                                    onPlayGame(game)
                                },
                                onCardClick = { selectedDetailGame = it }
                            )
                        }
                    }

                    // Category Chips Row
                    item(span = { GridItemSpan(2) }) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            SectionTitle(title = "CATEGORIES", onSeeAll = { onNavigateToLibrary(null) })
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(Categories.list) { category ->
                                    CategoryChip(
                                        category = category,
                                        isSelected = false,
                                        onSelect = { onNavigateToLibrary(it) }
                                    )
                                }
                            }
                        }
                    }

                    // Recently Played Section (if history exists)
                    if (recentlyPlayed.isNotEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                SectionTitle(
                                    title = "RECENTLY PLAYED",
                                    icon = Icons.Default.History,
                                    onSeeAll = { onNavigateToFavorites() }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(recentlyPlayed) { game ->
                                        GameCard(
                                            game = game,
                                            onPlayClick = {
                                                viewModel.recordGamePlay(game)
                                                onPlayGame(game)
                                            },
                                            onCardClick = { selectedDetailGame = it },
                                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                                            modifier = Modifier.width(170.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Popular Games Title
                    item(span = { GridItemSpan(2) }) {
                        SectionTitle(
                            title = "POPULAR GAMES",
                            icon = Icons.Default.Whatshot,
                            onSeeAll = { onNavigateToLibrary(null) }
                        )
                    }

                    // Popular Games Items (2 columns grid)
                    items(popularGames.ifEmpty { allGames.take(6) }) { game ->
                        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                            GameCard(
                                game = game,
                                onPlayClick = {
                                    viewModel.recordGamePlay(game)
                                    onPlayGame(game)
                                },
                                onCardClick = { selectedDetailGame = it },
                                onFavoriteClick = { viewModel.toggleFavorite(it) }
                            )
                        }
                    }
                }
            }
        }

        // Game Detail Bottom Sheet
        if (selectedDetailGame != null) {
            GameDetailBottomSheet(
                game = selectedDetailGame,
                onDismiss = { selectedDetailGame = null },
                onPlayClick = { game ->
                    viewModel.recordGamePlay(game)
                    onPlayGame(game)
                },
                onFavoriteClick = { viewModel.toggleFavorite(it) }
            )
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onSeeAll: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title.uppercase(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = 0.5.sp
            )
        }

        onSeeAll?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { it() }
                    .padding(4.dp)
            ) {
                Text(
                    text = "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
