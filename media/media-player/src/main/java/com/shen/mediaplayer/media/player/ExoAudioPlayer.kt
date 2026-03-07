package com.shen.mediaplayer.media.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoAudioPlayer @Inject constructor(
    private val context: Context
) : AudioPlayer {

    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()

    private val _playerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Idle)
    override val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    override val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _currentMediaUri = MutableStateFlow<Uri?>(null)
    override val currentMediaUri: StateFlow<Uri?> = _currentMediaUri.asStateFlow()

    private val _currentMediaTitle = MutableStateFlow<String>("")
    override val currentMediaTitle: StateFlow<String> = _currentMediaTitle.asStateFlow()

    private val _currentMediaArtist = MutableStateFlow<String>("")
    override val currentMediaArtist: StateFlow<String> = _currentMediaArtist.asStateFlow()

    private var updatePositionRunnable = object : Runnable {
        override fun run() {
            exoPlayer?.let { player ->
                if (player.isPlaying) {
                    _currentPosition.value = player.currentPosition
                }
                handler.postDelayed(this, 1000)
            }
        }
    }

    private val handler = android.os.Handler(context.mainLooper)

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                ExoPlayer.STATE_IDLE -> _playerState.value = AudioPlayerState.Idle
                ExoPlayer.STATE_BUFFERING -> _playerState.value = AudioPlayerState.Buffering
                ExoPlayer.STATE_READY -> {
                    _playerState.value = AudioPlayerState.Ready
                    _duration.value = exoPlayer?.duration ?: 0L
                }
                ExoPlayer.STATE_ENDED -> _playerState.value = AudioPlayerState.Ended
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                _playerState.value = AudioPlayerState.Playing
                startPositionUpdate()
            } else {
                _playerState.value = AudioPlayerState.Paused
                stopPositionUpdate()
            }
        }
    }

    init {
        initPlayer()
    }

    private fun initPlayer() {
        exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .build()
        exoPlayer?.addListener(playerListener)
        exoPlayer?.playWhenReady = false

        mediaSession = MediaSession.Builder(context, exoPlayer!!)
            .build()
    }

    override fun setMedia(uri: Uri, title: String, artist: String) {
        _currentMediaUri.value = uri
        _currentMediaTitle.value = title
        _currentMediaArtist.value = artist
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
        _playerState.value = AudioPlayerState.Idle
        _currentPosition.value = 0L
    }

    override fun release() {
        stopPositionUpdate()
        mediaSession?.release()
        exoPlayer?.release()
        exoPlayer = null
        mediaSession = null
    }

    override fun getPlayer(): Player? {
        return exoPlayer
    }

    override fun getMediaSession(): MediaSession? {
        return mediaSession
    }

    override fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
    }

    private fun startPositionUpdate() {
        handler.post(updatePositionRunnable)
    }

    private fun stopPositionUpdate() {
        handler.removeCallbacks(updatePositionRunnable)
    }
}

interface AudioPlayer {
    val playerState: StateFlow<AudioPlayerState>
    val currentPosition: StateFlow<Long>
    val duration: StateFlow<Long>
    val currentMediaUri: StateFlow<Uri?>
    val currentMediaTitle: StateFlow<String>
    val currentMediaArtist: StateFlow<String>

    fun setMedia(uri: Uri, title: String, artist: String)
    fun play()
    fun pause()
    fun seekTo(position: Long)
    fun stop()
    fun release()
    fun getPlayer(): Player?
    fun getMediaSession(): MediaSession?
    fun setPlaybackSpeed(speed: Float)
}

sealed interface AudioPlayerState {
    object Idle : AudioPlayerState
    object Buffering : AudioPlayerState
    object Ready : AudioPlayerState
    object Playing : AudioPlayerState
    object Paused : AudioPlayerState
    object Ended : AudioPlayerState
}
