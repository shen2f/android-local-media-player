package com.shen.mediaplayer.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.domain.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<MediaFile>>(emptyList())
    val favorites: StateFlow<List<MediaFile>> = _favorites

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadFavorites() {
        _isLoading.value = true
        viewModelScope.launch {
            val favorites = favoritesRepository.getFavorites()
            _favorites.value = favorites
            _isLoading.value = false
        }
    }

    fun toggleFavorite(mediaFile: MediaFile) {
        viewModelScope.launch {
            if (favoritesRepository.isFavorite(mediaFile.id)) {
                favoritesRepository.removeFavorite(mediaFile.id)
            } else {
                favoritesRepository.addFavorite(mediaFile)
            }
            loadFavorites()
        }
    }

    fun removeFavorite(mediaFile: MediaFile) {
        viewModelScope.launch {
            favoritesRepository.removeFavorite(mediaFile.id)
            loadFavorites()
        }
    }

    fun refresh() {
        loadFavorites()
    }
}
