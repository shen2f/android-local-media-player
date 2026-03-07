package com.shen.mediaplayer.feature_playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.core_domain.repository.PlaylistRepository
import com.shen.mediaplayer.data_local.entity.PlaylistEntity
import com.shen.mediaplayer.data_local.entity.PlaylistEntryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<PlaylistEntity>>(emptyList())
    val playlists: StateFlow<List<PlaylistEntity>> = _playlists

    private val _currentPlaylistEntries = MutableStateFlow<List<PlaylistEntryEntity>>(emptyList())
    val currentPlaylistEntries: StateFlow<List<PlaylistEntryEntity>> = _currentPlaylistEntries

    private val _currentPlaylist = MutableStateFlow<PlaylistEntity?>(null)
    val currentPlaylist: StateFlow<PlaylistEntity?> = _currentPlaylist

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistRepository.getAllPlaylists().collectLatest { playlists ->
                _playlists.value = playlists
            }
        }
    }

    fun loadPlaylistEntries(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.getEntriesForPlaylist(playlistId).collectLatest { entries ->
                _currentPlaylistEntries.value = entries
                _currentPlaylist.value = _playlists.value.find { it.id == playlistId }
            }
        }
    }

    fun createPlaylist(name: String, description: String = "", coverPath: String? = null) {
        viewModelScope.launch {
            val playlist = PlaylistEntity(
                name = name,
                description = description,
                coverPath = coverPath
            )
            playlistRepository.insertPlaylist(playlist)
        }
    }

    fun updatePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            playlistRepository.updatePlaylist(playlist)
        }
    }

    fun deletePlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlist)
        }
    }

    fun addSongToPlaylist(
        playlistId: Long,
        filePath: String,
        fileName: String,
        sortOrder: Int
    ) {
        viewModelScope.launch {
            val entry = PlaylistEntryEntity(
                playlistId = playlistId,
                filePath = filePath,
                fileName = fileName,
                sortOrder = sortOrder
            )
            playlistRepository.insertEntry(entry)
            loadPlaylistEntries(playlistId)
        }
    }

    fun removeSongFromPlaylist(entry: PlaylistEntryEntity) {
        viewModelScope.launch {
            playlistRepository.deleteEntry(entry)
            _currentPlaylistEntries.value = _currentPlaylistEntries.value.filter { it.id != entry.id }
        }
    }

    fun updateEntrySortOrder(entries: List<PlaylistEntryEntity>) {
        viewModelScope.launch {
            entries.forEachIndexed { index, entry ->
                playlistRepository.updateEntry(entry.copy(sortOrder = index))
            }
        }
    }
}
