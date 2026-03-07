package com.shen.mediaplayer.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<MediaFile>>(emptyList())
    val searchResults: StateFlow<List<MediaFile>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val results = mediaRepository.searchMedia(query)
            _searchResults.value = results
            _isLoading.value = false
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
    }
}
