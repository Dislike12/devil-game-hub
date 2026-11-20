package com.devilgamehub.ui.screens.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.devilgamehub.data.model.Categories
import com.devilgamehub.data.model.Category
import com.devilgamehub.data.model.Game
import com.devilgamehub.data.repository.GameRepository
import com.devilgamehub.data.repository.GamesState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption {
    POPULAR, RATING, TITLE_AZ
}

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    val repository = GameRepository.getInstance(application)

    val gamesState: StateFlow<GamesState> = repository.gamesState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(Categories.ALL)
    val selectedCategory: StateFlow<Category> = _selectedCategory.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.POPULAR)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    init {
        if (repository.gamesState.value is GamesState.Loading) {
            viewModelScope.launch {
                repository.loadGames()
            }
        }
    }

    val filteredGames: StateFlow<List<Game>> = combine(
        repository.gamesState,
        _searchQuery,
        _selectedCategory,
        _sortOption
    ) { state, query, category, sort ->
        val rawList = when (state) {
            is GamesState.Success -> state.games
            is GamesState.Error -> state.fallbackGames
            else -> emptyList()
        }

        rawList.filter { game ->
            val matchesCategory = (category.id == Categories.ALL.id) ||
                    game.category.equals(category.name, ignoreCase = true) ||
                    game.category.equals(category.id, ignoreCase = true)

            val matchesQuery = query.isBlank() ||
                    game.title.contains(query, ignoreCase = true) ||
                    game.category.contains(query, ignoreCase = true) ||
                    game.tags.any { it.contains(query, ignoreCase = true) }

            matchesCategory && matchesQuery
        }.let { list ->
            when (sort) {
                SortOption.POPULAR -> list.sortedByDescending { it.playsCount }
                SortOption.RATING -> list.sortedByDescending { it.rating }
                SortOption.TITLE_AZ -> list.sortedBy { it.title }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun toggleFavorite(game: Game) {
        repository.toggleFavorite(game.id)
    }

    fun recordGamePlay(game: Game) {
        repository.recordGamePlay(game.id)
    }
}
