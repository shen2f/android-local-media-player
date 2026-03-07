package com.shen.mediaplayer.feature.imagelist

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
class ImageListViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _imageList = MutableStateFlow<List<MediaFile>>(emptyList())
    val imageList: StateFlow<List<MediaFile>> = _imageList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadImageList() {
        _isLoading.value = true
        viewModelScope.launch {
            val images = mediaRepository.getAllImageFiles()
            _imageList.value = images
            _isLoading.value = false
        }
    }

    fun refresh() {
        loadImageList()
    }
}
