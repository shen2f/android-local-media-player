package com.shen.mediaplayer.feature_imagebrowser

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ImageBrowserViewModel @Inject constructor() : ViewModel() {

    private val _imageUris = MutableStateFlow<List<Uri>>(emptyList())
    val imageUris: StateFlow<List<Uri>> = _imageUris

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition

    fun setImageList(images: List<Uri>, initialPosition: Int) {
        _imageUris.value = images
        _currentPosition.value = initialPosition
    }

    fun goToNext(): Boolean {
        if (_currentPosition.value < _imageUris.value.size - 1) {
            _currentPosition.value += 1
            return true
        }
        return false
    }

    fun goToPrevious(): Boolean {
        if (_currentPosition.value > 0) {
            _currentPosition.value -= 1
            return true
        }
        return false
    }

    fun getCurrentImageUri(): Uri? {
        val index = _currentPosition.value
        return if (index >= 0 && index < _imageUris.value.size) {
            _imageUris.value[index]
        } else {
            null
        }
    }
}
