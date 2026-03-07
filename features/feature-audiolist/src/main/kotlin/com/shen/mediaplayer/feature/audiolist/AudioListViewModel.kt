package com.shen.mediaplayer.feature.audiolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.domain.repository.MediaRepository
import com.shen.mediaplayer.feature.audiolist.adapter.AudioListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioListViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _audioList = MutableStateFlow<List<AudioListAdapter.Item>>(emptyList())
    val audioList: StateFlow<List<AudioListAdapter.Item>> = _audioList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadAudioList() {
        _isLoading.value = true
        viewModelScope.launch {
            val allAudio = mediaRepository.getAllAudioFiles()
            val groupedByAlbum = allAudio.groupBy { it.album ?: "未知专辑" }
            val items = mutableListOf<AudioListAdapter.Item>()
            groupedByAlbum.forEach { (album, audios) ->
                items.add(AudioListAdapter.Item.Category("$album (${audios.size})"))
                items.addAll(audios.map { AudioListAdapter.Item.Audio(it) })
            }
            _audioList.value = items
            _isLoading.value = false
        }
    }

    fun refresh() {
        loadAudioList()
    }
}
