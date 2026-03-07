package com.shen.mediaplayer.feature_videoplayer

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.media.player.PlayerState
import com.shen.mediaplayer.media.player.VideoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val videoPlayer: VideoPlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val videoUri = savedStateHandle.get<Uri>("videoUri")!!

    private val _isControlsVisible = MutableStateFlow(true)
    val isControlsVisible: StateFlow<Boolean> = _isControlsVisible

    private val _currentSpeed = MutableStateFlow(1.0f)
    val currentSpeed: StateFlow<Float> = _currentSpeed

    private val _scaleMode = MutableStateFlow(ScaleMode.FIT)
    val scaleMode: StateFlow<ScaleMode> = _scaleMode

    private val _isBrightnessChanging = MutableStateFlow(false)
    val isBrightnessChanging: StateFlow<Boolean> = _isBrightnessChanging

    private val _currentBrightness = MutableStateFlow(0.5f)
    val currentBrightness: StateFlow<Float> = _currentBrightness

    private val _isVolumeChanging = MutableStateFlow(false)
    val isVolumeChanging: StateFlow<Boolean> = _isVolumeChanging

    private val _currentVolume = MutableStateFlow(1.0f)
    val currentVolume: StateFlow<Float> = _currentVolume

    private val _isSeeking = MutableStateFlow(false)
    val isSeeking: StateFlow<Boolean> = _isSeeking

    private val _seekProgress = MutableStateFlow(0f)
    val seekProgress: StateFlow<Float> = _seekProgress

    val uiState = combine(
        videoPlayer.playerState,
        videoPlayer.currentPosition,
        videoPlayer.duration,
        videoPlayer.bufferedPosition,
        _isControlsVisible,
        _currentSpeed,
        _scaleMode
    ) { state, position, duration, buffered, controlsVisible, speed, scale ->
        VideoPlayerUiState(
            playerState = state,
            currentPosition = position,
            duration = duration,
            bufferedPosition = buffered,
            isControlsVisible = controlsVisible,
            currentSpeed = speed,
            scaleMode = scale
        )
    }

    init {
        initPlayer()
    }

    private fun initPlayer() {
        videoPlayer.setVideoUri(videoUri)
        videoPlayer.play()
    }

    fun toggleControlsVisibility() {
        _isControlsVisible.value = !_isControlsVisible.value
    }

    fun playPauseToggle() {
        when (videoPlayer.playerState.value) {
            PlayerState.Playing -> videoPlayer.pause()
            PlayerState.Paused -> videoPlayer.play()
            PlayerState.Ready -> videoPlayer.play()
            PlayerState.Ended -> {
                videoPlayer.seekTo(0)
                videoPlayer.play()
            }
            else -> {}
        }
    }

    fun seekTo(position: Long) {
        videoPlayer.seekTo(position)
    }

    fun setPlaybackSpeed(speed: Float) {
        _currentSpeed.value = speed
        videoPlayer.setPlaybackSpeed(speed)
    }

    fun cycleScaleMode() {
        _scaleMode.value = when (_scaleMode.value) {
            ScaleMode.FIT -> ScaleMode.FILL
            ScaleMode.FILL -> ScaleMode.ZOOM
            ScaleMode.ZOOM -> ScaleMode.FIT
        }
    }

    fun startSeeking() {
        _isSeeking.value = true
    }

    fun updateSeekProgress(progress: Float) {
        _seekProgress.value = progress
    }

    fun finishSeeking() {
        val duration = videoPlayer.duration.value
        val position = (duration * _seekProgress.value).toLong()
        videoPlayer.seekTo(position)
        _isSeeking.value = false
    }

    fun fastForward() {
        val newPosition = videoPlayer.currentPosition.value + 10000
        val max = videoPlayer.duration.value
        videoPlayer.seekTo(newPosition.coerceAtMost(max))
    }

    fun rewind() {
        val newPosition = videoPlayer.currentPosition.value - 10000
        videoPlayer.seekTo(newPosition.coerceAtLeast(0))
    }

    fun startBrightnessChange() {
        _isBrightnessChanging.value = true
    }

    fun updateBrightness(delta: Float) {
        val newBrightness = (_currentBrightness.value + delta).coerceIn(0f, 1f)
        _currentBrightness.value = newBrightness
    }

    fun finishBrightnessChange() {
        _isBrightnessChanging.value = false
    }

    fun startVolumeChange() {
        _isVolumeChanging.value = true
    }

    fun updateVolume(delta: Float) {
        val newVolume = (_currentVolume.value + delta).coerceIn(0f, 1f)
        _currentVolume.value = newVolume
        if (newVolume == 0f && videoPlayer.getPlayer()?.volume != 0f) {
            videoPlayer.toggleMute()
        } else if (newVolume > 0f && videoPlayer.getPlayer()?.volume == 0f) {
            videoPlayer.toggleMute()
        }
    }

    fun finishVolumeChange() {
        _isVolumeChanging.value = false
    }

    fun screenshot(): Bitmap? {
        val player = videoPlayer.getPlayer() ?: return null
        // 实现截图功能，通过获取当前帧
        return null
    }

    override fun onCleared() {
        super.onCleared()
        videoPlayer.release()
    }
}

data class VideoPlayerUiState(
    val playerState: PlayerState,
    val currentPosition: Long,
    val duration: Long,
    val bufferedPosition: Long,
    val isControlsVisible: Boolean,
    val currentSpeed: Float,
    val scaleMode: ScaleMode
)

enum class ScaleMode {
    FIT,
    FILL,
    ZOOM
}
