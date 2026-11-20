package com.devilgamehub.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onPlayGame: (Game) -> Unit,
    initialCategory: Category? = null
) {
    val gamesState by viewModel.gamesState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredGames by viewModel.filteredGames.collectAsState()

    var selectedDetailGame by remember { mutableStateOf<Game?>(null) }

    LaunchedEffect(initialCategory) {
        if (initialCategory != null) {
            viewModel.selectCategory(initialCategory)
        } else {
            viewModel.selectCategory(Categories.ALL)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        when (gamesState) {
            is GamesState.Loading -> {
                LoadingScreen(message = "Loading Game Library...")
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header
                    item(span = { GridItemSpan(2) }) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = "GAME LIBRARY",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )

                            // Search Bar
                            SearchBar(
                                query = searchQuery,
                                onQueryChange = { viewModel.updateSearchQuery(it) },
                                onClearQuery = { viewModel.updateSearchQuery("") },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Categories Row
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(Categories.list) { category ->
                                    CategoryChip(
                                        category = category,
                                        isSelected = category.id == selectedCategory.id,
                                        onSelect = { viewModel.selectCategory(it) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Status / Count Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${filteredGames.size} GAMES FOUND",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    // Empty state
                    if (filteredGames.isEmpty()) {
                        item(span = { GridItemSpan(2) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No Games Found",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Try adjusting your search terms or category filter.",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // Games Grid
                    items(filteredGames) { game ->
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

        // Game Detail Sheet
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
