package com.devilgamehub.ui.screens.favorites

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.devilgamehub.data.model.Game
import com.devilgamehub.ui.components.*
import com.devilgamehub.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onPlayGame: (Game) -> Unit
) {
    val context = LocalContext.current
    val favorites by viewModel.favoriteGames.collectAsState()
    val history by viewModel.historyGames.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Favorites, 1: History
    var selectedDetailGame by remember { mutableStateOf<Game?>(null) }

    // Multi-select state for History
    var selectedGameIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showDeleteSelectedDialog by remember { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Title
            Text(
                text = "MY GAME HUB",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp)
            )

            // Tab Switcher Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                TabButton(
                    text = "FAVORITES (${favorites.size})",
                    icon = Icons.Default.Favorite,
                    isSelected = selectedTab == 0,
                    activeColor = NeonPink,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )

                TabButton(
                    text = "HISTORY (${history.size})",
                    icon = Icons.Default.History,
                    isSelected = selectedTab == 1,
                    activeColor = NeonCyan,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Favorites Tab Content
                if (favorites.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.Favorite,
                        title = "No Favorite Games Yet",
                        subtitle = "Tap the heart icon on any game card to add it to your favorites."
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(favorites) { game ->
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
            } else {
                // History Tab Content
                if (history.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.History,
                        title = "No Play History",
                        subtitle = "Games you launch will automatically appear here so you can resume anytime."
                    )
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Action Bar: Select All / Selected Count
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val allSelected = history.isNotEmpty() && selectedGameIds.size == history.size

                            OutlinedButton(
                                onClick = {
                                    selectedGameIds = if (allSelected) {
                                        emptySet()
                                    } else {
                                        history.map { it.id }.toSet()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SelectAll,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (allSelected) "Deselect All" else "Select All",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            // Selected Count Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedGameIds.isNotEmpty()) SurfaceLightDark else VoidBlack)
                                    .border(
                                        width = 1.dp,
                                        color = if (selectedGameIds.isNotEmpty()) NeonCyan else SurfaceBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Selected: ${selectedGameIds.size}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedGameIds.isNotEmpty()) NeonCyan else TextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // History Cards List
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            items(history) { game ->
                                val isSelected = selectedGameIds.contains(game.id)
                                HistoryItemCard(
                                    game = game,
                                    isSelected = isSelected,
                                    onSelectToggle = {
                                        selectedGameIds = if (isSelected) {
                                            selectedGameIds - game.id
                                        } else {
                                            selectedGameIds + game.id
                                        }
                                    },
                                    onPlayClick = {
                                        viewModel.recordGamePlay(game)
                                        onPlayGame(game)
                                    },
                                    onCardClick = {
                                        if (selectedGameIds.isNotEmpty()) {
                                            selectedGameIds = if (isSelected) {
                                                selectedGameIds - game.id
                                            } else {
                                                selectedGameIds + game.id
                                            }
                                        } else {
                                            selectedDetailGame = game
                                        }
                                    }
                                )
                            }
                        }

                        // Bottom Actions: Delete Selected & Clear All
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Delete Selected Button
                            Button(
                                onClick = { showDeleteSelectedDialog = true },
                                enabled = selectedGameIds.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonRed,
                                    disabledContainerColor = SurfaceLightDark
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = if (selectedGameIds.isNotEmpty()) TextPrimary else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Delete Selected (${selectedGameIds.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedGameIds.isNotEmpty()) TextPrimary else TextMuted
                                )
                            }

                            // Clear All History Button
                            OutlinedButton(
                                onClick = { showClearAllDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "Clear All",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonRed
                                )
                            }
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

        // Confirmation Dialog: Delete Selected Entries
        if (showDeleteSelectedDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteSelectedDialog = false },
                title = {
                    Text(
                        text = "Delete Selected Entries?",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete ${selectedGameIds.size} selected games from your play history? Saved favorites will not be affected.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val count = selectedGameIds.size
                            viewModel.deleteSelectedHistory(selectedGameIds)
                            selectedGameIds = emptySet()
                            showDeleteSelectedDialog = false
                            Toast.makeText(context, "$count history entries deleted", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Delete", color = NeonRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteSelectedDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = SurfaceDark
            )
        }

        // Confirmation Dialog: Clear All History
        if (showClearAllDialog) {
            AlertDialog(
                onDismissRequest = { showClearAllDialog = false },
                title = {
                    Text(
                        text = "Clear All Play History?",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to clear all play history? Your saved favorite games will remain intact.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearHistory()
                            selectedGameIds = emptySet()
                            showClearAllDialog = false
                            Toast.makeText(context, "All play history cleared", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Clear All", color = NeonRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearAllDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = SurfaceDark
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) SurfaceLightDark else VoidBlack)
            .border(
                width = 1.dp,
                color = if (isSelected) activeColor else VoidBlack,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) activeColor else TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isSelected) TextPrimary else TextMuted
            )
        }
    }
}

@Composable
private fun HistoryItemCard(
    game: Game,
    isSelected: Boolean,
    onSelectToggle: () -> Unit,
    onPlayClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val formattedTime = remember(game.lastPlayedTimestamp) {
        if (game.lastPlayedTimestamp > 0) {
            val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            sdf.format(Date(game.lastPlayedTimestamp))
        } else ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) SurfaceLightDark else SurfaceDark)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) NeonCyan else SurfaceBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) SurfaceLightDark else SurfaceDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Checkbox / Select Indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) NeonCyan else VoidBlack)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) NeonCyan else TextMuted,
                        shape = CircleShape
                    )
                    .clickable { onSelectToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = VoidBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Game Thumbnail
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(game.thumbnailUrl.ifEmpty { "https://images.unsplash.com/photo-1550745165-9bc0b252726f" })
                    .crossfade(true)
                    .build(),
                contentDescription = game.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            // Titles & Timestamp
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = game.category.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple
                )

                if (formattedTime.isNotEmpty()) {
                    Text(
                        text = "Played: $formattedTime",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // Quick Play Icon Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(NeonPurple, NeonCyan)))
                    .clickable { onPlayClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 18.sp
            )
        }
    }
}
