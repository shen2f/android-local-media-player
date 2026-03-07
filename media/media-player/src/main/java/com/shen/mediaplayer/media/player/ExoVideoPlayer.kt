package com.shen.mediaplayer.media.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.util.EventLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoVideoPlayer @Inject constructor(
    private val context: Context
) : VideoPlayer {

    private var exoPlayer: ExoPlayer? = null
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .build()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Idle)
    override val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _bufferedPosition = MutableStateFlow(0L)
    override val bufferedPosition: StateFlow<Long> = _bufferedPosition.asStateFlow()

    private var updatePositionRunnable = object : Runnable {
        override fun run() {
            exoPlayer?.let { player ->
                if (player.isPlaying) {
                    _currentPosition.value = player.currentPosition
                    _bufferedPosition.value = player.bufferedPosition
                    player.currentPosition.let { _currentPosition.value = it }
                }
                handler.postDelayed(this, 100)
            }
        }
    }

    private val handler = android.os.Handler(context.mainLooper)

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                ExoPlayer.STATE_IDLE -> _playerState.value = PlayerState.Idle
                ExoPlayer.STATE_BUFFERING -> _playerState.value = PlayerState.Buffering
                ExoPlayer.STATE_READY -> {
                    _playerState.value = PlayerState.Ready
                    _duration.value = exoPlayer?.duration ?: 0L
                }
                ExoPlayer.STATE_ENDED -> _playerState.value = PlayerState.Ended
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                _playerState.value = PlayerState.Playing
                startPositionUpdate()
            } else {
                _playerState.value = PlayerState.Paused
                stopPositionUpdate()
            }
        }
    }

    init {
        initPlayer()
    }

    private fun initPlayer() {
        val trackSelector = DefaultTrackSelector(context)
        exoPlayer = ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(audioAttributes, true)
            .build()
        exoPlayer?.addListener(playerListener)
        exoPlayer?.addAnalyticsListener(EventLogger())
        exoPlayer?.playWhenReady = false
    }

    override fun setVideoUri(uri: Uri) {
        exoPlayer?.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer?.prepare()
    }

    override fun play() {
        exoPlayer?.play()
    }

    override fun pause() {
        exoPlayer?.pause()
    }

    override fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        _currentPosition.value = position
    }

    override fun stop() {
        exoPlayer?.stop()
        _playerState.value = PlayerState.Idle
        _currentPosition.value = 0L
    }

    override fun release() {
        stopPositionUpdate()
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun getPlayer(): Player? {
        return exoPlayer
    }

    override fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
    }

    override fun getPlaybackSpeed(): Float {
        return exoPlayer?.playbackSpeed ?: 1f
    }

    override fun toggleMute(): Boolean {
        val isMuted = exoPlayer?.volume == 0f
        exoPlayer?.volume = if (isMuted) 1f else 0f
        return !isMuted
    }

    private fun startPositionUpdate() {
        handler.post(updatePositionRunnable)
    }

    private fun stopPositionUpdate() {
        handler.removeCallbacks(updatePositionRunnable)
    }
}

interface VideoPlayer {
    val playerState: StateFlow<PlayerState>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val bufferedPosition: StateFlow<Long>

    fun setVideoUri(uri: Uri)
    fun play()
    fun pause()
    fun seekTo(position: Long)
    fun stop()
    fun release()
    fun getPlayer(): Player?
    fun setPlaybackSpeed(speed: Float)
    fun getPlaybackSpeed(): Float
    fun toggleMute(): Boolean
}

sealed interface PlayerState {
    object Idle : PlayerState
    object Buffering : PlayerState
    object Ready : PlayerState
    object Playing : PlayerState
    object Paused : PlayerState
    object Ended : PlayerState
}
