package com.shen.mediaplayer.feature.playbackhistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.core.common.model.PlaybackHistoryItem
import com.shen.mediaplayer.core.domain.repository.PlaybackHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaybackHistoryViewModel @Inject constructor(
    private val historyRepository: PlaybackHistoryRepository
) : ViewModel() {

    private val _history = MutableStateFlow<List<PlaybackHistoryItem>>(emptyList())
    val history: StateFlow<List<PlaybackHistoryItem>> = _history

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadHistory() {
        _isLoading.value = true
        viewModelScope.launch {
            val history = historyRepository.getHistory()
            _history.value = history.sortedByDescending { it.lastPlayedTime }
            _isLoading.value = false
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
            loadHistory()
        }
    }

    fun removeHistory(itemId: Long) {
        viewModelScope.launch {
            historyRepository.removeHistory(itemId)
            loadHistory()
        }
    }

    fun refresh() {
        loadHistory()
    }
}
