package com.shen.mediaplayer.feature.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.core.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _folderItems = MutableStateFlow<List<FolderItem>>(emptyList())
    val folderItems: StateFlow<List<FolderItem>> = _folderItems

    private val _currentPath = MutableStateFlow<String?>(null)
    val currentPath: StateFlow<String?> = _currentPath

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentTitle = MutableStateFlow("文件夹")
    val currentTitle: StateFlow<String> = _currentTitle

    fun loadFolder(path: String? = null) {
        _isLoading.value = true
        _currentPath.value = path
        viewModelScope.launch {
            val items = mediaRepository.getFolderContent(path)
            _folderItems.value = items
            _currentTitle.value = if (path == null) {
                "根目录"
            } else {
                path.substringAfterLast('/')
            }
            _isLoading.value = false
        }
    }

    fun navigateBack(): Boolean {
        val currentPath = _currentPath.value ?: return false
        val parentPath = currentPath.substringBeforeLast('/')
        loadFolder(if (parentPath.isEmpty()) null else parentPath)
        return true
    }

    fun refresh() {
        loadFolder(_currentPath.value)
    }
}
