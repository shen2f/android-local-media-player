package com.shen.mediaplayer.feature_audioplayer

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.media.player.AudioPlayer
import com.shen.mediaplayer.media.player.AudioPlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val audioUri = savedStateHandle.get<Uri>("audioUri")!!
    private val audioTitle = savedStateHandle.get<String>("audioTitle")!!
    private val audioArtist = savedStateHandle.get<String>("audioArtist")!!

    val uiState = combine(
        audioPlayer.playerState,
        audioPlayer.currentPosition,
        audioPlayer.duration,
        audioPlayer.currentMediaTitle,
        audioPlayer.currentMediaArtist
    ) { state, position, duration, title, artist ->
        AudioPlayerUiState(
            playerState = state,
            currentPosition = position,
            duration = duration,
            currentTitle = title,
            currentArtist = artist
        )
    }

    private val _isLyricsVisible = MutableStateFlow(false)
    val isLyricsVisible: StateFlow<Boolean> = _isLyricsVisible

    private val _currentSpeed = MutableStateFlow(1.0f)
    val currentSpeed: StateFlow<Float> = _currentSpeed

    init {
        initPlayer()
    }

    private fun initPlayer() {
        audioPlayer.setMedia(audioUri, audioTitle, audioArtist)
        audioPlayer.play()
    }

    fun playPauseToggle() {
        when (audioPlayer.playerState.value) {
            AudioPlayerState.Playing -> audioPlayer.pause()
            AudioPlayerState.Paused -> audioPlayer.play()
            AudioPlayerState.Ready -> audioPlayer.play()
            AudioPlayerState.Ended -> {
                audioPlayer.seekTo(0)
                audioPlayer.play()
            }
            else -> {}
        }
    }

    fun seekTo(position: Long) {
        audioPlayer.seekTo(position)
    }

    fun next() {
        // TODO: Implement next track in playlist
    }

    fun previous() {
        // TODO: Implement previous track in playlist
    }

    fun fastForward() {
        val newPosition = audioPlayer.currentPosition.value + 10000
        val max = audioPlayer.duration.value
        audioPlayer.seekTo(newPosition.coerceAtMost(max))
    }

    fun rewind() {
        val newPosition = audioPlayer.currentPosition.value - 10000
        audioPlayer.seekTo(newPosition.coerceAtLeast(0))
    }

    fun toggleLyricsVisibility() {
        _isLyricsVisible.value = !_isLyricsVisible.value
    }

    fun setPlaybackSpeed(speed: Float) {
        _currentSpeed.value = speed
        audioPlayer.setPlaybackSpeed(speed)
    }

    override fun onCleared() {
        super.onCleared()
        // Service handles player release
    }
}

data class AudioPlayerUiState(
    val playerState: AudioPlayerState,
    val currentPosition: Long,
    val duration: Long,
    val currentTitle: String,
    val currentArtist: String
)
