package com.shen.mediaplayer.feature_audioplayer

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.support.v4.media.session.MediaSessionCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.shen.mediaplayer.core_common.R
import com.shen.mediaplayer.media.player.AudioPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AudioPlayerService : MediaSessionService() {

    @Inject
    lateinit var audioPlayer: AudioPlayer

    private lateinit var mediaSession: MediaSession
    private lateinit var playerNotificationManager: PlayerNotificationManager

    override fun onCreate() {
        super.onCreate()
        initMediaSession()
        initNotificationManager()
    }

    private fun initMediaSession() {
        mediaSession = MediaSession.Builder(this, audioPlayer.getPlayer()!!)
            .setSessionActivity(getSessionActivityPendingIntent())
            .build()
        setMediaSession(mediaSession)
    }

    private fun getSessionActivityPendingIntent(): PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun initNotificationManager() {
        val notificationId = 12345
        val channelId = getString(R.string.audio_playback_channel_id)

        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            notificationId,
            channelId
        )
            .setMediaSession(mediaSession)
            .setSmallIcon(R.drawable.ic_music_note)
            .setChannelNameResourceId(R.string.audio_playback_channel_name)
            .setChannelDescriptionResourceId(R.string.audio_playback_channel_description)
            .setUseRewindAction(true)
            .setUseFastForwardAction(true)
            .build()

        playerNotificationManager.setPlayer(audioPlayer.getPlayer()!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerNotificationManager.setPlayer(null)
        mediaSession.release()
        audioPlayer.release()
    }
}
