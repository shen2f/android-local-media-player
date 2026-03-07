package com.shen.mediaplayer.feature.videolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.core.common.model.MediaFile
import com.shen.mediaplayer.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _videoList = MutableLiveData<List<MediaFile>>()
    val videoList: LiveData<List<MediaFile>> = _videoList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadVideoList() {
        _isLoading.value = true
        viewModelScope.launch {
            val videos = mediaRepository.getAllVideos()
            _videoList.value = videos
            _isLoading.value = false
        }
    }

    fun refresh() {
        loadVideoList()
    }
}
